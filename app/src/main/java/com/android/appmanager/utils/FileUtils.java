package com.android.appmanager.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtils {
    private static final String TAG = "FileUtils";
    public static final int CANCEL = 0;
    public static final int OVERWRITE = 1;
    public static final int RENAME_THIS = 2;

    @Nullable
    public static String getFileNameWithoutExtension(@NonNull String path) throws FileNotFoundException {
        File file = new File(path);
        if (file.exists()) {
            String fileName = file.getName();
            int pos = fileName.lastIndexOf(".");
            if (pos > 0) {
                return fileName.substring(0, pos);
            }
        } else {
            throw new FileNotFoundException();
        }
        return null;
    }

    @Nullable
    public static String getFileNameExtension(@NonNull String path) throws FileNotFoundException {
        File file = new File(path);
        if (file.exists()) {
            String fileName = file.getName();
            int pos = fileName.lastIndexOf(".");
            if (pos > 0) {
                return fileName.substring(pos);
            }
        } else {
            throw new FileNotFoundException();
        }
        return null;
    }

    public static void copy(String sourceAddress, String destAddress, int strategy) throws FileNotFoundException {
        File sourceFile = new File(sourceAddress);
        String sourceFileName;
        if (!sourceFile.exists()) {
            throw new RuntimeException("Source file does not exist.");
        } else {
            sourceFileName = sourceFile.getName();
        }
        File destFile = new File(destAddress + File.separator + sourceFileName);
        if (destFile.exists()) {
            switch (strategy) {
                case CANCEL:
                    Log.i(TAG, "Copy process ended because strategy is set to CANCEL.");
                    return;
                case OVERWRITE:
                    if (!destFile.delete()) {
                        throw new RuntimeException("Cannot delete existing file with the same name.");
                    }
                    break;
                case RENAME_THIS:
                    String newName = FileUtils.getFileNameWithoutExtension(sourceAddress) + "(1)";
                    String sourceFileExtension = FileUtils.getFileNameExtension(sourceAddress);
                    destFile = new File(destAddress + File.separator + newName + sourceFileExtension);
                    break;
                default:
                    throw new IllegalArgumentException("Illegal strategy.");
            }
        }
        try {
            if (!destFile.createNewFile()) {
                throw new RuntimeException("Cannot create new file.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FileInputStream fis;
        FileOutputStream fos;
        try {
            fis = new FileInputStream(sourceAddress);
            fos = new FileOutputStream(destFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Cannot find file.");
        }
        FileChannel inChannel = fis.getChannel();
        FileChannel outChannel = fos.getChannel();
        int length;
        while (true) {
            try {
                if (inChannel.position() == inChannel.size()) {
                    inChannel.close();
                    outChannel.close();
                    break;
                }
                if (inChannel.size() - inChannel.position() < 1024 * 1024) {
                    length = (int) (inChannel.size() - inChannel.position());
                } else {
                    length = 1024 * 1024;
                }
                inChannel.transferTo(inChannel.position(), length, outChannel);
                inChannel.position(inChannel.position() + length);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }

    public static boolean deleteDirectory(String path) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            boolean result;
            File[] toDelete = file.listFiles();
            if (toDelete != null) {
                for (File oneFile : toDelete) {
                    if (file.exists()) {
                        if (file.isFile()) {
                            result = deleteFile(oneFile.getAbsolutePath());
                        } else {
                            result = deleteDirectory(oneFile.getAbsolutePath());
                        }
                        if (!result) {
                            return result;
                        }
                    }
                }
                return file.delete();
            }
        }
        return false;
    }

    public static boolean delete(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isFile()) {
                return deleteFile(path);
            } else if (file.isDirectory()) {
                return deleteDirectory(path);
            }
        }
        return false;
    }
}
