package com.android.appmanager.data.model;

import android.content.Context;
import android.content.pm.ProviderInfo;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.android.appmanager.utils.ProviderUtils;

public class ProviderModel {
    private final String name;
    private final String label;
    private final String authority;
    private final String readPermission;
    private final String writePermission;
    private final boolean grantUriPermissions;
    private final boolean forceUriPermissions;
    private final boolean multiProcess;
    private final int initOrder;

    public ProviderModel(@NonNull Context context, @NonNull ProviderInfo providerInfo) {
        this.label = ProviderUtils.loadLabel(context, providerInfo);
        this.name = providerInfo.name;
        this.authority = providerInfo.authority;
        this.readPermission = providerInfo.readPermission;
        this.writePermission = providerInfo.writePermission;
        this.grantUriPermissions = providerInfo.grantUriPermissions;
        this.forceUriPermissions = ProviderUtils.isForceUriPermissions(providerInfo);
        this.multiProcess = providerInfo.multiprocess;
        this.initOrder = providerInfo.initOrder;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public String getAuthority() {
        return authority;
    }

    public String getReadPermission() {
        return readPermission;
    }

    public String getWritePermission() {
        return writePermission;
    }

    public boolean isGrantUriPermissions() {
        return grantUriPermissions;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public boolean isForceUriPermissions() {
        return forceUriPermissions;
    }

    public boolean isMultiProcess() {
        return multiProcess;
    }

    public int getInitOrder() {
        return initOrder;
    }
}