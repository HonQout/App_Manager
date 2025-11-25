package com.android.appmanager.bean;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;

public class ProviderBean {
    private ProviderInfo providerInfo;
    private String label;
    private String name;

    public ProviderBean(Context context, ProviderInfo providerInfo) {
        this.providerInfo = providerInfo;
        PackageManager pm = context.getPackageManager();
        this.label = providerInfo.loadLabel(pm).toString();
        this.name = providerInfo.name;
    }

    public void setProviderInfo(ProviderInfo providerInfo) {
        this.providerInfo = providerInfo;
    }

    public ProviderInfo getProviderInfo() {
        return providerInfo;
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }
}