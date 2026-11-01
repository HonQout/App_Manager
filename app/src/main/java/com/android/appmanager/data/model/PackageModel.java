package com.android.appmanager.data.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.InstallSourceInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.appmanager.utils.ApplicationUtils;
import com.android.appmanager.utils.ApplicationUtils.ApplicationType;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PackageModel {
    private static final String TAG = "PackageModel";
    private PackageInfo packageInfo;
    private String packageName;
    private String label;
    private String versionName;
    private String versionCode;
    private ApplicationType type;

    public PackageModel(@NonNull Context context, @NonNull PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
        this.packageName = packageInfo.packageName;
        this.label = ApplicationUtils.getApplicationLabel(context, packageInfo);
        this.versionName = ApplicationUtils.getVersionName(packageInfo);
        this.versionCode = String.valueOf(ApplicationUtils.getVersionCode(packageInfo));
        this.type = ApplicationUtils.getApplicationType(packageInfo);
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getLabel() {
        return label;
    }

    public String getCombinedVersion() {
        return versionName + " (" + versionCode + ")";
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
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
        return sdf.format(new Date(firstInstallTime));
    }

    public String getLastUpdateTime() {
        long lastUpdateTime = packageInfo.lastUpdateTime;
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
        return sdf.format(new Date(lastUpdateTime));
    }

    @SuppressWarnings("deprecation")
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
                ApplicationInfo applicationInfo = pm.getPackageInfo(installerPackageName, 0).applicationInfo;
                if (applicationInfo != null) {
                    installerPackageLabel = applicationInfo.loadLabel(pm).toString();
                }
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