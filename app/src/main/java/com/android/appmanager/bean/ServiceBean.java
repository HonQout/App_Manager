package com.android.appmanager.bean;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;

public class ServiceBean {
    private ServiceInfo serviceInfo;
    private String label;
    private String name;

    public ServiceBean(Context context, ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
        PackageManager pm = context.getPackageManager();
        this.label = serviceInfo.loadLabel(pm).toString();
        this.name = serviceInfo.name;
    }

    public void setServiceInfo(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }
}