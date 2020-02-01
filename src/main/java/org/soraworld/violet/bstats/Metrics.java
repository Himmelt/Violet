package org.soraworld.violet.bstats;

import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.core.PluginCore;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ScheduledExecutorService;
import java.util.zip.GZIPOutputStream;

/**
 * @author Himmelt
 */
public class Metrics {

    private static final String OS_NAME = System.getProperty("os.name");
    private static final String OS_ARCH = System.getProperty("os.arch");
    private static final String OS_VERSION = System.getProperty("os.version");
    private static final String CORE_COUNT = String.valueOf(Runtime.getRuntime().availableProcessors());
    private static final String JAVA_VERSION = System.getProperty("java.version");
    private static final String BUKKIT_JSON = "{\"serverUUID\":\"%serverUUID%\",\"playerAmount\":%playerAmount%,\"onlineMode\":%onlineMode%,\"bukkitVersion\":\"%bukkitVersion%\",\"javaVersion\":\"%javaVersion%\",\"osName\":\"%osName%\",\"osArch\":\"%osArch%\",\"osVersion\":\"%osVersion%\",\"coreCount\":%coreCount%,\"plugins\":[%pluginList%]}";
    private static final String SPONGE_JSON = "{\"serverUUID\":\"%serverUUID%\",\"playerAmount\":%playerAmount%,\"onlineMode\":%onlineMode%,\"minecraftVersion\":\"%minecraftVersion%\",\"spongeImplementation\":\"%spongeImplementation%\",\"javaVersion\":\"%javaVersion%\",\"osName\":\"%osName%\",\"osArch\":\"%osArch%\",\"osVersion\":\"%osVersion%\",\"coreCount\":%coreCount%,\"plugins\":[%pluginList%]}";

    private static final int B_STATS_VERSION = 1;
    private static final String BUKKIT_URL = "https://bStats.org/submitData/bukkit";
    private static final String SPONGE_URL = "https://bStats.org/submitData/sponge";
    private static final String PLUGIN_JSON = "{\"pluginName\":\"%name%\",\"id\":\"%id%\",\"pluginVersion\":\"%version%\"}";

    protected final IPlugin plugin;
    String serverJson = null;
    // TODO use this
    ScheduledExecutorService service;

    Metrics(IPlugin plugin) {
        this.plugin = plugin;
    }

    void submitData(boolean sponge) {
        final String json = getServerJson().replace("%pluginList%", getPluginsJson());
        new Thread(() -> {
            try {
                sendData(json, sponge);
            } catch (Throwable e) {
                plugin.debugKey("bStatsFailed");
                plugin.debug(e);
            }
        }).start();
    }

    private String getServerJson() {
        return "";
    }

    private static void sendData(String json, boolean sponge) throws Exception {
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

    private static byte[] compress(final String text) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(output);
        gzip.write(text.getBytes(StandardCharsets.UTF_8));
        gzip.close();
        return output.toByteArray();
    }

    private static String getPluginsJson() {
        StringBuilder builder = new StringBuilder();
        PluginCore.getPlugins().forEach(plugin -> builder.append(PLUGIN_JSON
                .replace("%name%", plugin.name())
                .replace("%id%", plugin.bStatsId())
                .replace("%version%", plugin.version())).append(','));
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}