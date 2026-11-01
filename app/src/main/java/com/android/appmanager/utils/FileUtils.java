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

    public enum ConflictStrategy {
        CANCEL, OVERWRITE, RENAME_NEW
    }

    /**
     * Get the name of the file with the specified path.
     *
     * @param path
     * @return The name without extension of the file with the specified path. Returns the name of
     * the file if it does not have an extension. Throws FileNotFoundException if the file does not
     * exist.
     * @throws FileNotFoundException
     */
    @NonNull
    public static String getFileNameWithoutExtension(@NonNull String path) throws FileNotFoundException {
        File file = new File(path);
        if (file.exists()) {
            String fileName = file.getName();
            int pos = fileName.lastIndexOf(".");
            if (pos > 0) {
                return fileName.substring(0, pos);
            } else {
                return fileName;
            }
        } else {
            throw new FileNotFoundException("Required file does not exist.");
        }
    }

    /**
     * Get the extension of the file with the specified path.
     *
     * @param path
     * @return The extension of the file with the specified path. Returns null if the file does not
     * have an extension. Throws FileNotFoundException if the file does not exist.
     * @throws FileNotFoundException
     */
    @Nullable
    public static String getFileNameExtension(@NonNull String path) throws FileNotFoundException {
        File file = new File(path);
        if (file.exists()) {
            String fileName = file.getName();
            int pos = fileName.lastIndexOf(".");
            if (pos > 0) {
                return fileName.substring(pos);
            } else {
                return null;
            }
        } else {
            throw new FileNotFoundException("Required file does not exist.");
        }
    }

    public static void copy(String srcAddress, String dstAddress, ConflictStrategy strategy) throws IOException {
        // Get the name of source file
        File srcFile = new File(srcAddress);
        String srcFileName;
        if (!srcFile.exists()) {
            throw new FileNotFoundException("Source file does not exist.");
        } else {
            srcFileName = srcFile.getName();
        }
        // Generate destination file and handle confliction
        File dstFile = new File(dstAddress + File.separator + srcFileName);
        if (dstFile.exists()) {
            switch (strategy) {
                case CANCEL:
                    Log.i(TAG, "Copy process ended because conflict strategy is set to CANCEL.");
                    return;
                case OVERWRITE:
                    if (!dstFile.delete()) {
                        Log.i(TAG, "Copy process ended because conflict strategy is set to OVERWRITE but existing file with the same name cannot be deleted.");
                        throw new IOException("Cannot delete existing file with the same name.");
                    }
                    break;
                case RENAME_NEW:
                    String newName = FileUtils.getFileNameWithoutExtension(srcAddress) + "(1)";
                    String srcFileExtension = FileUtils.getFileNameExtension(srcAddress);
                    dstFile = new File(dstAddress + File.separator + newName + srcFileExtension);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown conflict strategy.");
            }
        }
        try {
            if (!dstFile.createNewFile()) {
                throw new IOException("Cannot create new file.");
            }
        } catch (IOException e) {
            throw new IOException(e);
        }
        FileInputStream fis;
        FileOutputStream fos;
        try {
            fis = new FileInputStream(srcAddress);
            fos = new FileOutputStream(dstFile.getAbsolutePath());
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