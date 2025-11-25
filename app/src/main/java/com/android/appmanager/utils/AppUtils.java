package com.android.appmanager.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

public class AppUtils {
    private static final String TAG = "AppUtils";

    /**
     * Launch the Application Details Settings of the package with the given packageName.
     *
     * @return {@code True} if and only if the activity is successfully started, {@code False} otherwise.
     */
    public static boolean launchAppDetailsSettings(Context context, String packageName) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", packageName, null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Failed to launch application details settings. Cannot find an activity with the given name.", e);
            return false;
        }
    }

    /**
     * Install the package with the given packageName.
     *
     * @return {@code True} if and only if the install process is successfully started, {@code False} otherwise.
     */
    public static boolean uninstallApp(Context context, String packageName) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DELETE);
        intent.setData(Uri.fromParts("package", packageName, null));
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Failed to launch application details settings. Cannot find an activity with the given name.", e);
            return false;
        }
    }
}
