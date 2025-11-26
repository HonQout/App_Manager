package com.android.appmanager.bean;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;

import com.android.appmanager.utils.ServiceUtils;

public class ServiceBean {
    private final ServiceInfo serviceInfo;
    private final String label;
    private final String name;
    private final int flags;

    public ServiceBean(Context context, ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
        PackageManager pm = context.getPackageManager();
        label = serviceInfo.loadLabel(pm).toString();
        name = serviceInfo.name;
        flags = serviceInfo.flags;
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

    public String getLocaleForegroundServiceType(Context context) {
        return context.getString(ServiceUtils.getLocaleForegroundServiceType(serviceInfo));
    }

    public boolean doesStopWithTask() {
        return (flags & ServiceInfo.FLAG_STOP_WITH_TASK) != 0;
    }

    public boolean isIsolatedProcess() {
        return (flags & ServiceInfo.FLAG_ISOLATED_PROCESS) != 0;
    }

    public boolean isExternalService() {
        return (flags & ServiceInfo.FLAG_EXTERNAL_SERVICE) != 0;
    }

    public boolean doesUseAppZygote() {
        return (flags & ServiceInfo.FLAG_USE_APP_ZYGOTE) != 0;
    }

    public boolean doesAllowSharedIsolatedProcess() {
        return (flags & ServiceInfo.FLAG_ALLOW_SHARED_ISOLATED_PROCESS) != 0;
    }

    public boolean isSingleUser() {
        return (flags & ServiceInfo.FLAG_SINGLE_USER) != 0;
    }
}