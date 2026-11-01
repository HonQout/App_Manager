package com.android.appmanager.data.model;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.android.appmanager.utils.ActivityUtils;
import com.android.appmanager.utils.ApplicationUtils;
import com.android.appmanager.utils.PackageUtils;

import java.io.Serializable;

public class ActivityModel implements Serializable {
    private final String packageName;
    private final String activityName;
    private final String label;
    @DrawableRes
    private final int iconRes;
    private final boolean enabled;
    private final boolean exported;
    private final int maxRecents;
    private final String permission;
    private final String taskAffinity;
    private final String targetActivity;
    private final String requiredDisplayCategory;
    private final String parentActivityName;
    private final int themeResource;
    private final int launchMode;

    public ActivityModel(@NonNull Context context, @NonNull ActivityInfo activityInfo) {
        this.packageName = activityInfo.packageName;
        this.activityName = activityInfo.name;
        this.label = ApplicationUtils.getActivityLabel(context, activityInfo);
        this.iconRes = ApplicationUtils.getActivityIconId(activityInfo);
        this.enabled = activityInfo.isEnabled();
        this.exported = activityInfo.exported;
        this.maxRecents = activityInfo.maxRecents;
        this.permission = activityInfo.permission;
        this.taskAffinity = activityInfo.taskAffinity;
        this.targetActivity = activityInfo.targetActivity;
        this.requiredDisplayCategory = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ?
                activityInfo.requiredDisplayCategory : "";
        this.parentActivityName = activityInfo.parentActivityName;
        this.themeResource = activityInfo.getThemeResource();
        this.launchMode = activityInfo.launchMode;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getActivityName() {
        return activityName;
    }

    public String getKey() {
        return packageName + ":" + activityName;
    }

    public String getLabel() {
        return label;
    }

    @DrawableRes
    public int getIconRes() {
        return iconRes;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isExported() {
        return exported;
    }

    public int getMaxRecents() {
        return maxRecents;
    }

    public String getPermission() {
        return permission;
    }

    public String getTaskAffinity() {
        return taskAffinity;
    }

    public String getTargetActivity() {
        return targetActivity;
    }

    public String getRequiredDisplayCategory() {
        return requiredDisplayCategory;
    }

    public String getParentActivityName() {
        return parentActivityName;
    }

    public String getThemeName(Context context) {
        return PackageUtils.getThemeName(context, packageName, themeResource);
    }

    public String getLocalizedLaunchMode(Context context) {
        return context.getString(ActivityUtils.getLocalizedLaunchModeRes(launchMode));
    }
}