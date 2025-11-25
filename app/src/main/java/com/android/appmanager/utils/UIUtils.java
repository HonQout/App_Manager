package com.android.appmanager.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.WindowManager;

public class UIUtils {
    public static void setImmersiveBars(Activity activity) {
        int apiVersion = Build.VERSION.SDK_INT;
        if (apiVersion >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        } else if (apiVersion >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
