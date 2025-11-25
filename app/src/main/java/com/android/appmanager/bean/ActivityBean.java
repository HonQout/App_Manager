package com.android.appmanager.bean;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

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
}