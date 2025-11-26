package com.android.appmanager.bean;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.android.appmanager.utils.ActivityUtils;
import com.android.appmanager.utils.PackageUtils;

public class ActivityBean {
    private final ActivityInfo activityInfo;
    private final String packageName;
    private final String activityName;
    private final Drawable icon;
    private final String label;

    public ActivityBean(Context context, ActivityInfo activityInfo) {
        this.activityInfo = activityInfo;
        this.packageName = activityInfo.packageName;
        this.activityName = activityInfo.name;
        PackageManager pm = context.getPackageManager();
        this.icon = activityInfo.loadIcon(pm);
        this.label = activityInfo.loadLabel(pm).toString();
    }

    public ActivityInfo getActivityInfo() {
        return activityInfo;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getActivityName() {
        return activityName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getLabel() {
        return label;
    }

    public String getThemeName(Context context) {
        return PackageUtils.getThemeName(context, packageName, activityInfo.getThemeResource());
    }

    public String getLocaleLaunchMode(Context context) {
        return context.getString(ActivityUtils.getLocaleLaunchModeRes(activityInfo));
    }


}