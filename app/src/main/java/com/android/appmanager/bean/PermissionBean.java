package com.android.appmanager.bean;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;

public class PermissionBean {
    private PermissionInfo permissionInfo;
    private String name;
    private String label;
    private String description;

    public PermissionBean(Context context, PermissionInfo permissionInfo) {
        this.permissionInfo = permissionInfo;
        this.name = permissionInfo.name;
        PackageManager pm = context.getPackageManager();
        this.label = permissionInfo.loadLabel(pm).toString();
        CharSequence descriptionCS = permissionInfo.loadDescription(pm);
        this.description = descriptionCS == null ? "" : descriptionCS.toString();
    }

    public void setPermissionInfo(PermissionInfo permissionInfo) {
        this.permissionInfo = permissionInfo;
    }

    public PermissionInfo getPermissionInfo() {
        return permissionInfo;
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
}