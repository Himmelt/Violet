package org.soraworld.violet.bstats;

import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.manager.VioletManager;

import javax.annotation.Nonnull;
import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.zip.GZIPOutputStream;

public abstract class Metrics<T extends VioletManager> {

    protected static final int B_STATS_VERSION = 1;

    protected static final String OS_NAME = System.getProperty("os.name");
    protected static final String OS_ARCH = System.getProperty("os.arch");
    protected static final String OS_VERSION = System.getProperty("os.version");
    protected static final String CORE_COUNT = String.valueOf(Runtime.getRuntime().availableProcessors());
    protected static final String JAVA_VERSION = System.getProperty("java.version");

    protected static final String BUKKIT_URL = "https://bStats.org/submitData/bukkit";
    protected static final String SPONGE_URL = "https://bStats.org/submitData/sponge";
    protected static final String BUKKIT_JSON = "{\"serverUUID\":\"%serverUUID%\",\"playerAmount\":%playerAmount%,\"onlineMode\":%onlineMode%,\"bukkitVersion\":\"%bukkitVersion%\",\"javaVersion\":\"%javaVersion%\",\"osName\":\"%osName%\",\"osArch\":\"%osArch%\",\"osVersion\":\"%osVersion%\",\"coreCount\":%coreCount%,\"plugins\":[%pluginList%]}";
    protected static final String SPONGE_JSON = "{\"serverUUID\":\"%serverUUID%\",\"playerAmount\":%playerAmount%,\"onlineMode\":%onlineMode%,\"minecraftVersion\":\"%minecraftVersion%\",\"spongeImplementation\":\"%spongeImplementation%\",\"javaVersion\":\"%javaVersion%\",\"osName\":\"%osName%\",\"osArch\":\"%osArch%\",\"osVersion\":\"%osVersion%\",\"coreCount\":%coreCount%,\"plugins\":[%pluginList%]}";
    protected static final String PLUGIN_JSON = "{\"pluginName\":\"%pluginName%\",\"pluginVersion\":\"%pluginVersion%\"}";

    protected final T manager;
    String serverJson = null;
    Timer timer;

    Metrics(T manager) {
        this.manager = manager;
    }

    public abstract void start();

    public abstract String getServerJson();

    void submitData(boolean sponge) {
        final String json = getServerJson().replace("%pluginList%", getPluginsJson());
        new Thread(() -> {
            try {
                sendData(json, sponge);
            } catch (Throwable e) {
                if (manager.isDebug()) {
                    e.printStackTrace();
                    manager.consoleKey("bStatsFailed");
                }
            }
        }).start();
    }

    private static void sendData(@Nonnull String json, boolean sponge) throws Exception {
        HttpsURLConnection https = (HttpsURLConnection) new URL(sponge ? SPONGE_URL : BUKKIT_URL).openConnection();
        // Compress the data to save bandwidth
        byte[] bytes = compress(json);
        // Add headers
        https.setRequestMethod("POST");
        https.addRequestProperty("Accept", "application/json");
        https.addRequestProperty("Connection", "close");
        https.addRequestProperty("Content-Encoding", "gzip"); // We gzip our request
        https.addRequestProperty("Content-Length", String.valueOf(bytes.length));
        https.setRequestProperty("Content-Type", "application/json"); // We send our data in JSON format
        https.setRequestProperty("User-Agent", "MC-Server/" + B_STATS_VERSION);
        // Send data
        https.setDoOutput(true);
        DataOutputStream output = new DataOutputStream(https.getOutputStream());
        output.write(bytes);
        output.flush();
        output.close();
        // We don't care about the response - Just send our data :)
        https.getInputStream().close();
    }

    private static byte[] compress(@Nonnull final String text) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(output);
        gzip.write(text.getBytes(StandardCharsets.UTF_8));
        gzip.close();
        return output.toByteArray();
    }

    private static String getPluginsJson() {
        StringBuilder builder = new StringBuilder();
        int length = VioletManager.pluginsSize();
        for (int i = 0; i < length; i++) {
            IPlugin plugin = VioletManager.getPluginAt(i);
            if (plugin != null) {
                builder.append(PLUGIN_JSON
                        .replace("%pluginName%", plugin.getName())
                        .replace("%pluginVersion%", plugin.getVersion()));
                if (i < length - 1) builder.append(',');
            }
        }
        return builder.toString();
    }
}
