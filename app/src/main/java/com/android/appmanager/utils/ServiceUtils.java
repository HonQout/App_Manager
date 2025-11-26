package com.android.appmanager.utils;

import android.content.pm.ServiceInfo;

import androidx.annotation.StringRes;

import com.android.appmanager.R;

public class ServiceUtils {
    @StringRes
    public static int getLocaleForegroundServiceType(ServiceInfo serviceInfo) {
        int type = serviceInfo.getForegroundServiceType();
        switch (type) {
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC:
                return R.string.data_sync;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK:
                return R.string.media_playback;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL:
                return R.string.phone_call;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION:
                return R.string.location;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE:
                return R.string.connected_device;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION:
                return R.string.media_projection;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA:
                return R.string.camera;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE:
                return R.string.microphone;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH:
                return R.string.health;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_REMOTE_MESSAGING:
                return R.string.remote_messaging;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED:
                return R.string.system_exempted;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE:
                return R.string.short_service;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROCESSING:
                return R.string.media_processing;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE:
                return R.string.special_use;
            case ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST:
                return R.string.manifest;
            default:
                return R.string.unknown;
        }
    }
}
