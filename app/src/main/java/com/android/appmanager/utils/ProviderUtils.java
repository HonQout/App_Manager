package com.android.appmanager.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.os.Build;

import androidx.annotation.NonNull;

public class ProviderUtils {
    public static String loadLabel(@NonNull Context context, @NonNull ProviderInfo providerInfo) {
        PackageManager pm = context.getPackageManager();
        return providerInfo.loadLabel(pm).toString();
    }

    public static boolean isForceUriPermissions(@NonNull ProviderInfo providerInfo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return providerInfo.forceUriPermissions;
        } else {
            return false;
        }
    }
}
