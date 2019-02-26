package org.soraworld.violet.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {

    public static boolean deletePath(File path, boolean debug) {
        if (path.isFile()) {
            boolean flag = path.delete();
            if (debug && !flag) System.out.println("File " + path + " delete failed !!");
            return flag;
        }
        File[] files = path.listFiles();
        boolean flag = true;
        if (files != null && files.length > 0) {
            for (File file : files) flag = flag && deletePath(file, debug);
        } else {
            flag = path.delete();
        }
        return flag;
    }

    public static boolean zipArchivePath(Path source, Path target, Predicate<Path> filter) {
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(target));
            Files.walk(source)
                    .filter(filter)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry entry = new ZipEntry(source.relativize(path).toString());
                        try {
                            zipOutputStream.putNextEntry(entry);
                            Files.copy(path, zipOutputStream);
                            zipOutputStream.closeEntry();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
