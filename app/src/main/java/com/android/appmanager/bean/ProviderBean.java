package com.android.appmanager.bean;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;

public class ProviderBean {
    private final ProviderInfo providerInfo;
    private final String label;
    private final String name;

    public ProviderBean(Context context, ProviderInfo providerInfo) {
        this.providerInfo = providerInfo;
        PackageManager pm = context.getPackageManager();
        this.label = providerInfo.loadLabel(pm).toString();
        this.name = providerInfo.name;
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