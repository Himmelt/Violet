package org.soraworld.violet.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
}
