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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    private static final String TAG = "ZipUtils";
    public static final String EXTENSION = ".zip";

    public static boolean zipFile(List<File> sourceFiles,
                                  String destDir,
                                  String zipName,
                                  FileUtils.ConflictStrategy strategy) {
        File destFile = new File(destDir, zipName + EXTENSION);

        if (destFile.exists()) {
            switch (strategy) {
                case CANCEL: {
                    Log.i(TAG, "Zip process ended. Conflict strategy is set to CANCEL.");
                    return false;
                }
                case OVERWRITE: {
                    if (!destFile.delete()) {
                        throw new RuntimeException("Zip process ended. Failed to delete existing file with the same name.");
                    }
                    break;
                }
                case RENAME_NEW: {
                    zipName = zipName + "(1)";
                    destFile = new File(destDir, zipName + EXTENSION);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Zip process ended. Illegal strategy.");
                }
            }
        }
        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destFile));
            for (File oneFile : sourceFiles) {
                if (!addFileToZip(oneFile, oneFile.getName(), zos)) {
                    throw new RuntimeException("Failed to add file " + oneFile.getAbsolutePath() + " to zip.");
                }
            }
            zos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public static boolean addFileToZip(File sourceFile,
                                       String parentName,
                                       ZipOutputStream zos) throws IOException {
        if (!sourceFile.exists()) {
            return false;
        }

        List<File> toAdd = new ArrayList<>();
        if (sourceFile.isFile()) {
            toAdd.add(sourceFile);
        } else if (sourceFile.isDirectory()) {
            File[] files = sourceFile.listFiles();
            if (files != null) {
                toAdd.addAll(Arrays.asList(files));
            }
        }

        for (File oneFile : toAdd) {
            byte[] buffer = new byte[10240];
            // Inspiration: "/" is used directly here because File.separator can be "\" sometime,
            //              which violates the standard of ZIP file
            String entryName = parentName + "/" + oneFile.getName();
            zos.putNextEntry(new ZipEntry(entryName));
            try (InputStream inputStream = new FileInputStream(oneFile)) {
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

    public static boolean unZipFile(File sourceFile, String destDir) throws IOException {
        ZipFile zipFile = new ZipFile(sourceFile);
        Enumeration<? extends ZipEntry> zipList = zipFile.entries();

        if (zipList == null) {
            Log.i(TAG, "Failed to unzip file. ZipList is null.");
            return false;
        }

        while (zipList.hasMoreElements()) {
            ZipEntry zipEntry = zipList.nextElement();
            File file = new File(destDir, zipEntry.getName());

            if (zipEntry.isDirectory()) {
                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        throw new RuntimeException("Failed to create directory: " + zipEntry.getName());
                    }
                }
            } else {
                if (!file.exists()) {
                    File parentFile = file.getParentFile();
                    if (parentFile != null && !parentFile.exists()) {
                        if (!parentFile.mkdirs()) {
                            throw new RuntimeException("Failed to create directory: " + zipEntry.getName());
                        }
                    } else {
                        if (!file.createNewFile()) {
                            throw new RuntimeException("Failed to create file: " + zipEntry.getName());
                        }
                    }
                }
                try (
                        InputStream inputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))
                ) {
                    int len;
                    byte[] bytes = new byte[10240];
                    while ((len = inputStream.read(bytes, 0, bytes.length)) != -1) {
                        outputStream.write(bytes, 0, len);
                    }
                }
            }
        }
        zipFile.close();
        return true;
    }
}
