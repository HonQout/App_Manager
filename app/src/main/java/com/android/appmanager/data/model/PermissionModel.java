package com.android.appmanager.data.model;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Build;

import androidx.annotation.NonNull;

public class PermissionModel {
    private final String name;
    private final String label;
    private final String description;
    private final String group;
    private final int protectionFlags;

    public PermissionModel(@NonNull Context context, @NonNull PermissionInfo permissionInfo) {
        this.name = permissionInfo.name;
        PackageManager pm = context.getPackageManager();
        this.label = permissionInfo.loadLabel(pm).toString();
        CharSequence descriptionCS = permissionInfo.loadDescription(pm);
        this.description = descriptionCS == null ? "" : descriptionCS.toString();
        this.group = permissionInfo.group;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            this.protectionFlags = permissionInfo.getProtectionFlags();
        } else {
            this.protectionFlags = 0;
        }
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public String getGroup() {
        return group;
    }

    public int getProtectionFlags() {
        return protectionFlags;
    }
}