package com.android.appmanager.utils;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.appmanager.bean.permission.PermissionSorted;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PackageUtils {
    private static final String TAG = "PackageUtils";

    /**
     * Get the PackageInfo of the package specified by the given packageName.
     */
    @Nullable
    public static PackageInfo getPackageInfo(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                return pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0));
            } else {
                return pm.getPackageInfo(packageName, 0);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to get packageInfo of package " + packageName + ".\nPackageName not found.", e);
        }
        return null;
    }

    /**
     * Get a list of PackageInfo of this device.
     */
    public static List<PackageInfo> getPackageInfoList(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packageInfoList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageInfoList = pm.getInstalledPackages(PackageManager.PackageInfoFlags.of(0));
        } else {
            packageInfoList = pm.getInstalledPackages(0);
        }
        Log.i(TAG, "Length of packageInfoList is " + packageInfoList.size());
        return packageInfoList;
    }

    /**
     * Get a list of ActivityInfo of the package specified by the given packageName.
     */
    public static List<ActivityInfo> getActivityInfoList(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = null;
        List<ActivityInfo> activityInfoList = new ArrayList<>();
        try {
            packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to get activity info list. Cannot find a package with the given packageName.", e);
        }
        if (packageInfo != null) {
            if (packageInfo.activities != null) {
                activityInfoList.addAll(Arrays.asList(packageInfo.activities));
            }
        }
        return activityInfoList;
    }

    /**
     * Get a list of ServiceInfo of the package specified by the given packageName.
     */
    public static List<ServiceInfo> getServiceInfoList(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = null;
        List<ServiceInfo> serviceInfoList = new ArrayList<>();
        try {
            packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SERVICES);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to get service info list. Cannot find a package with the given packageName.", e);
        }
        if (packageInfo != null) {
            if (packageInfo.services != null) {
                serviceInfoList.addAll(Arrays.asList(packageInfo.services));
            }
        }
        return serviceInfoList;
    }

    /**
     * Get a list of ProviderInfo of the package specified by the given packageName.
     */
    public static List<ProviderInfo> getProviderInfoList(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = null;
        List<ProviderInfo> providerInfoList = new ArrayList<>();
        try {
            packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_PROVIDERS);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to get provider info list. Cannot find a package with the given packageName.", e);
        }
        if (packageInfo != null) {
            if (packageInfo.providers != null) {
                providerInfoList.addAll(Arrays.asList(packageInfo.providers));
            }
        }
        return providerInfoList;
    }

    /**
     * Get a list of receivers of the package specified by the given packageName.
     */
    public static List<ActivityInfo> getReceiverInfoList(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = null;
        List<ActivityInfo> receiverInfoList = new ArrayList<>();
        try {
            packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_RECEIVERS);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to get receiver info list. Cannot find a package with the given packageName.", e);
        }
        if (packageInfo != null) {
            if (packageInfo.receivers != null) {
                receiverInfoList.addAll(Arrays.asList(packageInfo.receivers));
            }
        }
        return receiverInfoList;
    }

    /**
     * Get sorted permissions (defined, requested and those don't exist in the system) of the package
     * specified by the given packageName.
     */
    public static PermissionSorted getPermissionSorted(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = null;
        PermissionSorted permissionSorted = new PermissionSorted();
        try {
            packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to get permission info list. Cannot find a package with the given packageName.", e);
        }
        if (packageInfo != null) {
            if (packageInfo.permissions != null) {
                permissionSorted.permissionDefinedList.addAll(Arrays.asList(packageInfo.permissions));
            }
            if (packageInfo.requestedPermissions != null) {
                for (String permission : packageInfo.requestedPermissions) {
                    PermissionInfo permissionInfo = null;
                    try {
                        permissionInfo = pm.getPermissionInfo(permission, 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.e(TAG, "Failed to get permission info. Cannot find a permission with the given name.", e);
                    }
                    if (permissionInfo == null) {
                        permissionSorted.permissionNotFoundList.add(permission);
                    } else {
                        permissionSorted.permissionRequestedList.add(permissionInfo);
                    }
                }
            }
        }
        return permissionSorted;
    }

    /**
     * Get a list of ConfigurationInfo of the package specified by the given packageName.
     */
    public static List<ConfigurationInfo> getConfigurationInfoList(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = null;
        List<ConfigurationInfo> configurationInfoList = new ArrayList<>();
        try {
            packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to get configuration info list. Cannot find a package with the given packageName.", e);
        }
        if (packageInfo != null) {
            if (packageInfo.configPreferences != null) {
                configurationInfoList.addAll(Arrays.asList(packageInfo.configPreferences));
            }
        }
        return configurationInfoList;
    }

    public static void generateAPK(Context context, String packageName, String destDir, int strategy) throws FileNotFoundException {
        PackageManager pm = context.getPackageManager();
        String sourceDir;
        try {
            sourceDir = pm.getApplicationInfo(packageName, 0).sourceDir;

        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (sourceDir != null) {
            FileUtils.copy(sourceDir, destDir, strategy);
        } else {
            throw new RuntimeException("SourceDir is null.");
        }
    }

    public static void generateAPKS(Context context, String packageName, String destDir, int strategy) {
        PackageManager pm = context.getPackageManager();
        String zipName;
        String sourceDir;
        String[] splitSourceDirs;
        try {
            String packageLabel = pm.getApplicationInfo(packageName, 0).loadLabel(pm).toString();
            String versionName = pm.getPackageInfo(packageName, 0).versionName;
            zipName = packageLabel + versionName;
            sourceDir = pm.getApplicationInfo(packageName, 0).sourceDir;
            splitSourceDirs = pm.getApplicationInfo(packageName, 0).splitSourceDirs;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<File> toExtract = new ArrayList<>();
        toExtract.add(new File(sourceDir));
        if (splitSourceDirs != null) {
            for (String splitSourceDir : splitSourceDirs) {
                File oneFile = new File(splitSourceDir);
                toExtract.add(oneFile);
            }
        }
        ZipUtils.zipFile(toExtract, destDir, zipName, strategy);
    }
}
