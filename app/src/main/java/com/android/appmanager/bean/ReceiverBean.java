package com.android.appmanager.bean;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

public class ReceiverBean {
    private ActivityInfo receiverInfo;
    private String label;
    private String name;

    public ReceiverBean(Context context, ActivityInfo receiverInfo) {
        this.receiverInfo = receiverInfo;
        PackageManager pm = context.getPackageManager();
        this.label = receiverInfo.loadLabel(pm).toString();
        this.name = receiverInfo.name;
    }

    public void setReceiverInfo(ActivityInfo receiverInfo) {
        this.receiverInfo = receiverInfo;
    }

    public ActivityInfo getReceiverInfo() {
        return receiverInfo;
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }
}