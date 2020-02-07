package org.soraworld.violet.bstats;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.soraworld.violet.Violet;
import org.soraworld.violet.api.IPlugin;
import org.soraworld.violet.core.PluginCore;
import org.spongepowered.api.Sponge;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

/**
 * @author Himmelt
 */
public class Metrics {

    private static final String BUKKIT_JSON = "{" +
            "\"serverUUID\":\"" + Violet.getServerId() + "\"," +
            "\"playerAmount\":%playerAmount%," +
            "\"onlineMode\":" + (Violet.ONLINE_MODE ? 1 : 0) + "," +
            "\"bukkitVersion\":\"" + Violet.MC_VERSION + "\"," +
            "\"javaVersion\":\"" + System.getProperty("java.version") + "\"," +
            "\"osName\":\"" + System.getProperty("os.name") + "\"," +
            "\"osArch\":\"" + System.getProperty("os.arch") + "\"," +
            "\"osVersion\":\"" + System.getProperty("os.version") + "\"," +
            "\"coreCount\":" + Runtime.getRuntime().availableProcessors() + "," +
            "\"plugins\":[%pluginList%]}";
    private static final String SPONGE_JSON = "{" +
            "\"serverUUID\":\"" + Violet.getServerId() + "\"," +
            "\"playerAmount\":%playerAmount%," +
            "\"onlineMode\":" + (Violet.ONLINE_MODE ? 1 : 0) + "," +
            "\"minecraftVersion\":\"" + Violet.MC_VERSION + "\"," +
            "\"spongeImplementation\":\"" + Violet.SPONGE_IMPL + "\"," +
            "\"javaVersion\":\"" + System.getProperty("java.version") + "\"," +
            "\"osName\":\"" + System.getProperty("os.name") + "\"," +
            "\"osArch\":\"" + System.getProperty("os.arch") + "\"," +
            "\"osVersion\":\"" + System.getProperty("os.version") + "\"," +
            "\"coreCount\":" + Runtime.getRuntime().availableProcessors() + "," +
            "\"plugins\":[%pluginList%]}";

    private static final int B_STATS_VERSION = 1;
    private static final int B_STATS_CLASS_REVISION = 2;
    private static final String BUKKIT_URL = "https://bStats.org/submitData/bukkit";
    private static final String SPONGE_URL = "https://bStats.org/submitData/sponge";
    private static final String PLUGIN_JSON = "{\"pluginName\":\"%name%\",\"id\":\"%id%\",\"pluginVersion\":\"%version%\",\"metricsRevision\":" + B_STATS_CLASS_REVISION + "}";

    public Metrics(@NotNull IPlugin plugin) {
        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1, task -> {
            Thread thread = Executors.defaultThreadFactory().newThread(task);
            thread.setName("bStatus");
            return thread;
        });
        service.scheduleAtFixedRate(() -> plugin.runTask(() -> {
            if (Violet.BUKKIT) {
                String json = BUKKIT_JSON.replace("%playerAmount%", String.valueOf(Bukkit.getOnlinePlayers().size())).replace("%pluginList%", getPluginsJson());
                plugin.runTaskAsync(() -> {
                    try {
                        sendData(json, BUKKIT_URL);
                    } catch (Exception e) {
                        plugin.debugKey("bStatsFailed");
                        plugin.debug(e);
                    }
                });
            } else if (Violet.SPONGE) {
                String json = SPONGE_JSON.replace("%playerAmount%", String.valueOf(Sponge.getServer().getOnlinePlayers().size())).replace("%pluginList%", getPluginsJson());
                plugin.runTaskAsync(() -> {
                    try {
                        sendData(json, SPONGE_URL);
                    } catch (Exception e) {
                        plugin.debugKey("bStatsFailed");
                        plugin.debug(e);
                    }
                });
            }
        }), 5, 35, TimeUnit.MINUTES);
        plugin.addDisableAction(service::shutdown);
    }

    private static void sendData(String json, String url) throws Exception {
        HttpsURLConnection https = (HttpsURLConnection) new URL(url).openConnection();
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