package com.android.appmanager.bean;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.InstallSourceInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PackageBean {
    private static final String TAG = "PackageBean";
    private PackageInfo packageInfo;
    private Drawable icon;
    private String label;

    public PackageBean(Context context, PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
        update(context);
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }

    public void update(Context context) {
        PackageManager pm = context.getPackageManager();
        if (packageInfo.applicationInfo == null) {
            this.icon = pm.getDefaultActivityIcon();
            this.label = "";
        } else {
            this.icon = packageInfo.applicationInfo.loadIcon(pm);
            this.label = packageInfo.applicationInfo.loadLabel(pm).toString();
        }
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public String getPackageName() {
        return packageInfo.packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getLabel() {
        return label;
    }

    public int getVersionCode() {
        return packageInfo.versionCode;
    }

    public String getVersionName() {
        return packageInfo.versionName;
    }

    public String getVersion() {
        return packageInfo.versionName + " (" + packageInfo.versionCode + ")";
    }

    public boolean isSystemApp() {
        if (packageInfo.applicationInfo != null) {
            return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0;
        } else {
            return false;
        }
    }

    public boolean doesAllowClearUserData() {
        if (packageInfo.applicationInfo != null) {
            return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_ALLOW_CLEAR_USER_DATA) > 0;
        } else {
            return false;
        }
    }

    public boolean doesAllowBackup() {
        if (packageInfo.applicationInfo != null) {
            return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_ALLOW_BACKUP) > 0;
        } else {
            return false;
        }
    }

    public boolean isEnabled() {
        if (packageInfo.applicationInfo != null) {
            return packageInfo.applicationInfo.enabled;
        } else {
            return false;
        }
    }

    public String getTargetSdkVersion() {
        if (packageInfo.applicationInfo != null) {
            return String.valueOf(packageInfo.applicationInfo.targetSdkVersion);
        } else {
            return "";
        }
    }

    public String getMinSdkVersion() {
        if (packageInfo.applicationInfo != null) {
            return String.valueOf(packageInfo.applicationInfo.minSdkVersion);
        } else {
            return "";
        }
    }

    public String getCompileSdkVersion() {
        if (packageInfo.applicationInfo != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return String.valueOf(packageInfo.applicationInfo.compileSdkVersion);
        }
        return "";
    }

    public String getSharedUserId() {
        return packageInfo.sharedUserId;
    }

    public String getSharedUserLabel() {
        return String.valueOf(packageInfo.sharedUserLabel);
    }

    public String getUid() {
        if (packageInfo.applicationInfo != null) {
            return String.valueOf(packageInfo.applicationInfo.uid);
        } else {
            return "";
        }
    }

    public String getFirstInstallTime() {
        long firstInstallTime = packageInfo.firstInstallTime;
        SimpleDateFormat sdf = new SimpleDateFormat();
        return sdf.format(new Date(firstInstallTime));
    }

    public String getLastUpdateTime() {
        long lastUpdateTime = packageInfo.lastUpdateTime;
        SimpleDateFormat sdf = new SimpleDateFormat();
        return sdf.format(new Date(lastUpdateTime));
    }

    public String getInstallSource(Context context) {
        PackageManager pm = context.getPackageManager();
        String packageName = packageInfo.packageName;
        String installerPackageName = "";
        String installerPackageLabel = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                InstallSourceInfo isInfo = pm.getInstallSourceInfo(packageName);
                installerPackageName = isInfo.getInstallingPackageName();
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Failed to get install source info. Cannot find a package with the given packageName.", e);
            }
        } else {
            installerPackageName = pm.getInstallerPackageName(packageName);
        }
        if (installerPackageName != null) {
            try {
                installerPackageLabel = pm.getPackageInfo(installerPackageName, 0).applicationInfo.loadLabel(pm).toString();
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Failed to get package info. Cannot find a package with the given packageName.", e);
            }
        }
        if (installerPackageLabel.isEmpty()) {
            return installerPackageName;
        } else {
            return installerPackageLabel;
        }
    }
}