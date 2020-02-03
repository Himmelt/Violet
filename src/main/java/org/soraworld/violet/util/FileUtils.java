package org.soraworld.violet.util;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Himmelt
 */
public class FileUtils {

    public static boolean deletePath(File path, boolean debug) {
        if (path.isFile()) {
            boolean flag = path.delete();
            if (debug && !flag) {
                System.out.println("File " + path + " delete failed !!");
            }
            return flag;
        }
        File[] files = path.listFiles();
        boolean flag = true;
        if (files != null && files.length > 0) {
            for (File file : files) {
                flag = flag && deletePath(file, debug);
            }
        } else {
            flag = path.delete();
        }
        return flag;
    }

    public static boolean zipArchivePath(Path source, Path target, Predicate<Path> filter) {
        if (Files.exists(source) && Files.isDirectory(source)) {
            try {
                Files.createDirectories(target.getParent());
                Files.deleteIfExists(target);
                Files.createFile(target);
                ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(target));
                Files.walk(source)
                        .filter(filter)
                        .filter(path -> !Files.isDirectory(path))
                        .forEach(path -> {
                            ZipEntry entry = new ZipEntry(source.relativize(path).toString());
                            try {
                                zipOut.putNextEntry(entry);
                                Files.copy(path, zipOut);
                                zipOut.closeEntry();
                            } catch (IOException e) {
                                System.out.println("!!!!! IOException: " + e.getLocalizedMessage());
                            }
                        });
                zipOut.flush();
                zipOut.close();
                return true;
            } catch (Throwable e) {
                System.out.println("!!!!! Exception: " + e.getLocalizedMessage());
            }
        }
        return false;
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static Path getJarPath(@NotNull Class<?> clazz) {
        String path = clazz.getProtectionDomain().getCodeSource().getLocation().getFile();
        if (path.startsWith("file:")) {
            path = path.substring(5);
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        int index = path.lastIndexOf(".jar!");
        if (index > 0) {
            path = path.substring(0, index + 4);
        }
        path = path.replaceAll("/\\./", "/");
        return Paths.get(path);
    }
}
