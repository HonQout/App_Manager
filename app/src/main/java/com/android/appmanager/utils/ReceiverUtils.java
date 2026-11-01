package com.android.appmanager.utils;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

public class ReceiverUtils {
    public static String loadLabel(@NonNull Context context, @NonNull ActivityInfo receiverInfo) {
        PackageManager pm = context.getPackageManager();
        return receiverInfo.loadLabel(pm).toString();
    }
}
