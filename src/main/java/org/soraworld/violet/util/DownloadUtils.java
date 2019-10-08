package org.soraworld.violet.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.soraworld.violet.util.FileUtils.readInputStream;

/**
 * @author Himmelt
 */
public class DownloadUtils {

    public static void download(String source, String target) throws IOException {
        download(new URL(source), Paths.get(target));
    }

    public static void download(String source, Path target) throws IOException {
        download(new URL(source), target);
    }

    public static void download(URL source, Path target) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) source.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");
        while (conn.getResponseCode() == 302) {
            String location = conn.getHeaderField("Location");
            conn.disconnect();
            conn = (HttpURLConnection) new URL(location).openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");
            conn.setConnectTimeout(5000);
        }
        InputStream stream = conn.getInputStream();
        byte[] bytes = readInputStream(stream);
        Files.createDirectories(target.getParent());
        FileOutputStream output = new FileOutputStream(target.toFile());
        output.write(bytes);
        output.flush();
        output.close();
        stream.close();
    }


}
