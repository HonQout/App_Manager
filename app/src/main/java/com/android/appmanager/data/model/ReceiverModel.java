package com.android.appmanager.data.model;

import android.content.Context;
import android.content.pm.ActivityInfo;

import androidx.annotation.NonNull;

import com.android.appmanager.utils.ReceiverUtils;

public class ReceiverModel {
    private final String name;
    private final String label;
    private final String processName;
    private final String permission;
    private boolean enabled;
    private boolean exported;


    public ReceiverModel(@NonNull Context context, @NonNull ActivityInfo receiverInfo) {
        this.label = ReceiverUtils.loadLabel(context, receiverInfo);
        this.name = receiverInfo.name;
        this.processName = receiverInfo.processName;
        this.permission = receiverInfo.permission;
        this.enabled = receiverInfo.isEnabled();
        this.exported = receiverInfo.exported;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public String getProcessName() {
        return processName;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isExported() {
        return exported;
    }
}