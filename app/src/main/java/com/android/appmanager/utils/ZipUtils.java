package com.android.appmanager.utils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    private static final String TAG = "ZipUtils";
    public static final String extension = ".zip";

    public static boolean zipFile(List<File> sourceFiles, String destDir, String zipName, int strategy) {
        File destFile = new File(destDir + File.separator + zipName + extension);
        if (destFile.exists()) {
            switch (strategy) {
                case FileUtils.CANCEL:
                    Log.i(TAG, "Zip process ended because strategy is set to CANCEL.");
                    return false;
                case FileUtils.OVERWRITE:
                    if (!destFile.delete()) {
                        throw new RuntimeException("Cannot delete existing file with the same name.");
                    }
                    break;
                case FileUtils.RENAME_THIS:
                    zipName = zipName + "(1)";
                    destFile = new File(destDir + File.separator + zipName + extension);
                    break;
                default:
                    throw new IllegalArgumentException("Illegal strategy.");
            }
        }
        try {
            ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(Paths.get(destFile.getAbsolutePath())));
            for (File oneFile : sourceFiles) {
                if (!addFileToZip(oneFile, oneFile.getName(), zos)) {
                    throw new RuntimeException("Failed to add file " + oneFile.getAbsolutePath() + " to zip.");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public static boolean addFileToZip(File sourceFile, String parentName, ZipOutputStream zos) throws IOException {
        if (sourceFile.exists()) {
            List<File> toAdd = new ArrayList<File>();
            if (sourceFile.isFile()) {
                toAdd.add(sourceFile);
            } else if (sourceFile.isDirectory()) {
                toAdd.addAll(Arrays.asList(sourceFile.listFiles()));
            }
            for (File oneFile : toAdd) {
                byte[] buffer = new byte[10240];
                String entryName = parentName + File.separator + oneFile.getName();
                zos.putNextEntry(new ZipEntry(entryName));
                try (InputStream inputStream = Files.newInputStream(oneFile.toPath());) {
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                } finally {
                    zos.closeEntry();
                }
            }
            return true;
        }
        return false;
    }

    public static boolean unZipFile(File sourceFile, String destDir) throws IOException {
        ZipFile zipFile = new ZipFile(sourceFile);
        Enumeration<? extends ZipEntry> zipList = zipFile.entries();
        if (zipList == null) {
            Log.i(TAG, "ZipList is null.");
            return false;
        }
        while (zipList.hasMoreElements()) {
            ZipEntry zipEntry = zipList.nextElement();
            File file = new File(destDir + File.separator + zipEntry.getName());
            if (zipEntry.isDirectory()) {
                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        throw new RuntimeException("Failed to create directory: " + zipEntry.getName());
                    }
                }
            } else {
                if (!file.exists()) {
                    if (!Objects.requireNonNull(file.getParentFile()).exists()) {
                        if (!file.getParentFile().mkdirs()) {
                            throw new RuntimeException("Failed to create directory: " + zipEntry.getName());
                        }
                    } else {
                        if (!file.createNewFile()) {
                            throw new RuntimeException("Failed to create file: " + zipEntry.getName());
                        }
                    }
                }
                InputStream inputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                OutputStream outputStream = new BufferedOutputStream(fileOutputStream);
                int len;
                byte[] bytes = new byte[10240];
                while ((len = inputStream.read(bytes, 0, bytes.length)) != -1) {
                    outputStream.write(bytes, 0, len);
                    outputStream.flush();
                }
                inputStream.close();
                outputStream.close();
            }
        }
        return true;
    }
}
