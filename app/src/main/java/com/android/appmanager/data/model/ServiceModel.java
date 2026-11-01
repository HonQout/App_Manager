package com.android.appmanager.data.model;

import android.content.Context;
import android.content.pm.ServiceInfo;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.android.appmanager.R;
import com.android.appmanager.utils.ServiceUtils;

public class ServiceModel {
    private final String name;
    private final String label;
    private final String permission;
    private final int flags;
    private final int foregroundServiceType;

    public ServiceModel(@NonNull Context context, @NonNull ServiceInfo serviceInfo) {
        name = serviceInfo.name;
        label = ServiceUtils.loadLabel(context, serviceInfo);
        permission = serviceInfo.permission;
        flags = serviceInfo.flags;
        foregroundServiceType = ServiceUtils.getForegroundServiceType(serviceInfo);
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public String getPermission() {
        return permission;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public int getForegroundServiceType() {
        return foregroundServiceType;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public String getLocalizedForegroundServiceType(@NonNull Context context) {
        return context.getString(switch (foregroundServiceType) {
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC -> R.string.data_sync;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK -> R.string.media_playback;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL -> R.string.phone_call;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION -> R.string.location;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE -> R.string.connected_device;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION -> R.string.media_projection;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA -> R.string.camera;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE -> R.string.microphone;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH -> R.string.health;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_REMOTE_MESSAGING -> R.string.remote_messaging;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED -> R.string.system_exempted;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE -> R.string.short_service;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROCESSING -> R.string.media_processing;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE -> R.string.special_use;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST -> R.string.manifest;
            default -> R.string.unknown;
        });
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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public boolean doesUseAppZygote() {
        return (flags & ServiceInfo.FLAG_USE_APP_ZYGOTE) != 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public boolean doesAllowSharedIsolatedProcess() {
        return (flags & ServiceInfo.FLAG_ALLOW_SHARED_ISOLATED_PROCESS) != 0;
    }

    public boolean isSingleUser() {
        return (flags & ServiceInfo.FLAG_SINGLE_USER) != 0;
    }
}