package com.android.appmanager.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Build;

import androidx.annotation.NonNull;

public class ServiceUtils {
    private static final String TAG = "ServiceUtils";

    public static String loadLabel(@NonNull Context context, ServiceInfo serviceInfo) {
        PackageManager pm = context.getPackageManager();
        return serviceInfo.loadLabel(pm).toString();
    }

    public static int getForegroundServiceType(ServiceInfo serviceInfo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return serviceInfo.getForegroundServiceType();
        } else {
            return 0;
        }
    }
}