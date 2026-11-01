package com.android.appmanager.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.pm.PackageInfoCompat;

import java.util.List;

public class ApplicationUtils {
    private static final String TAG = "ApplicationUtils";

    public enum ApplicationType {
        /**
         * Unknown application type.
         */
        UNKNOWN,
        /**
         * System application.
         */
        SYSTEM,
        /**
         * Updated system application.
         */
        UPDATED_SYSTEM,
        /**
         * User application.
         */
        USER
    }

    @Nullable
    public static PackageInfo getPackageInfo(@NonNull Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            Log.e(TAG, "Cannot get PackageInfo. The given packageName is null or empty.");
            return null;
        }
        PackageManager pm = context.getPackageManager();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                return pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0));
            } else {
                return pm.getPackageInfo(packageName, 0);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to get PackageInfo. Package " + packageName + " doesn't exist.", e);
            return null;
        }
    }

    @Nullable
    public static ApplicationInfo getApplicationInfo(@NonNull Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            Log.e(TAG, "Cannot get applicationInfo. The given packageName is null or empty.");
            return null;
        }
        PackageManager pm = context.getPackageManager();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                return pm.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0));
            } else {
                return pm.getApplicationInfo(packageName, 0);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Cannot get applicationInfo. Package " + packageName + " doesn't exist.", e);
            return null;
        }
    }

    @NonNull
    public static Drawable getApplicationIcon(@NonNull Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        if (TextUtils.isEmpty(packageName)) {
            Log.e(TAG, "Cannot get application icon. The given packageName is null or empty.");
            return pm.getDefaultActivityIcon();
        } else {
            try {
                return pm.getApplicationIcon(packageName);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Cannot get application icon. Package " + packageName + " doesn't exist.", e);
                return pm.getDefaultActivityIcon();
            }
        }
    }

    @DrawableRes
    public static int getApplicationIconId(@NonNull Context context, String packageName) {
        ApplicationInfo applicationInfo = getApplicationInfo(context, packageName);
        if (applicationInfo != null) {
            return applicationInfo.icon;
        }
        return 0;
    }

    /**
     * Get the label of the application corresponding to the given {@code packageInfo}.
     * <p>This method can be used if the package is not installed on the device.
     */
    @Nullable
    public static String getApplicationLabel(@NonNull Context context, PackageInfo packageInfo) {
        if (packageInfo != null) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            if (applicationInfo != null) {
                PackageManager pm = context.getPackageManager();
                return applicationInfo.loadLabel(pm).toString();
            }
        }
        return null;
    }

    /**
     * Get the label of the application corresponding to the given {@code packageName}.
     * <p>This method should be used only if the package corresponding to the given
     * {@code packageName} is installed on the device.
     */
    @Nullable
    public static String getApplicationLabel(@NonNull Context context, String packageName) {
        PackageInfo packageInfo = getPackageInfo(context, packageName);
        return getApplicationLabel(context, packageInfo);
    }

    /**
     * Get the version name of the package corresponding to the given packageInfo.
     */
    @Nullable
    public static String getVersionName(PackageInfo packageInfo) {
        return packageInfo == null ? null : packageInfo.versionName;
    }

    /**
     * Get the version name of the package corresponding to the given {@code packageName}.
     * <p>This method should be used only if the package corresponding to the given
     * {@code packageName} is installed on the device.
     */
    @Nullable
    public static String getVersionName(@NonNull Context context, String packageName) {
        PackageInfo packageInfo = getPackageInfo(context, packageName);
        return getVersionName(packageInfo);
    }

    public static ApplicationType getApplicationType(ApplicationInfo applicationInfo) {
        if (applicationInfo != null) {
            int flags = applicationInfo.flags;
            if ((flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                return ApplicationType.UPDATED_SYSTEM;
            } else if (((flags & ApplicationInfo.FLAG_SYSTEM) != 0)) {
                return ApplicationType.SYSTEM;
            } else {
                return ApplicationType.USER;
            }
        }
        return ApplicationType.UNKNOWN;
    }

    public static ApplicationType getApplicationType(PackageInfo packageInfo) {
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        return getApplicationType(applicationInfo);
    }

    public static ApplicationType getApplicationType(@NonNull Context context, String packageName) {
        ApplicationInfo applicationInfo = getApplicationInfo(context, packageName);
        return getApplicationType(applicationInfo);
    }

    @Nullable
    public static ActivityInfo getActivityInfo(@NonNull Context context,
                                               @Nullable String packageName,
                                               @Nullable String activityName) {
        if (TextUtils.isEmpty(packageName)) {
            Log.e(TAG, "Cannot get activityInfo. The given packageName is empty.");
            return null;
        }
        if (TextUtils.isEmpty(activityName)) {
            Log.e(TAG, "Cannot get activityInfo. The given activityName is empty.");
            return null;
        }
        PackageManager pm = context.getPackageManager();
        try {
            return pm.getActivityInfo(new ComponentName(packageName, activityName), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to get activityInfo. Activity " + activityName + " of package "
                    + packageName + " doesn't exist.", e);
        }
        return null;
    }

    @NonNull
    public static Drawable getActivityIcon(@NonNull Context context,
                                           @Nullable String packageName,
                                           @Nullable String activityName) {
        PackageManager pm = context.getPackageManager();
        if (TextUtils.isEmpty(packageName)) {
            Log.e(TAG, "Cannot get activity icon. The given packageName is empty.");
            return pm.getDefaultActivityIcon();
        }
        if (TextUtils.isEmpty(activityName)) {
            Log.e(TAG, "Cannot get activity icon. The given activityName is empty.");
            return pm.getDefaultActivityIcon();
        }
        try {
            return pm.getActivityIcon(new ComponentName(packageName, activityName));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to get activity icon. Activity " + activityName + " of package "
                    + packageName + " doesn't exist.", e);
        }
        return pm.getDefaultActivityIcon();
    }

    @NonNull
    public static Drawable getActivityIcon(@NonNull Context context,
                                           @Nullable ActivityInfo activityInfo) {
        if (activityInfo != null) {
            return getActivityIcon(context, activityInfo.packageName, activityInfo.name);
        }
        return context.getPackageManager().getDefaultActivityIcon();
    }

    /**
     * Get the id of icon of the activity specified by the given {@code activityInfo}.
     *
     * @return The id of icon of the activity specified by the given {@code activityInfo}, or
     * {@code 0} if the given {@code activityInfo} is null.
     */
    @DrawableRes
    public static int getActivityIconId(@Nullable ActivityInfo activityInfo) {
        return activityInfo == null ? 0 : activityInfo.icon;
    }

    /**
     * Get the id of icon of the activity specified by the ActivityInfo of the activity with the
     * given {@code packageName} and {@code activityName}.
     *
     * @return The id of icon of the activity specified by the ActivityInfo of the activity with the
     * given {@code packageName} and {@code activityName}, or {@code 0} if the given
     * {@code packageName} or {@code activityName} is null.
     */
    @DrawableRes
    public static int getActivityIconId(@NonNull Context context,
                                        @Nullable String packageName,
                                        @Nullable String activityName) {
        ActivityInfo activityInfo = getActivityInfo(context, packageName, activityName);
        return getActivityIconId(activityInfo);
    }

    @Nullable
    public static String getActivityLabel(@NonNull Context context,
                                          @Nullable ActivityInfo activityInfo) {
        PackageManager pm = context.getPackageManager();
        return activityInfo == null ? null : activityInfo.loadLabel(pm).toString();
    }

    @Nullable
    public static ComponentName getLauncherActivity(@NonNull Context context,
                                                    @Nullable String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            Log.e(TAG, "Cannot get launcher activity. The given packageName is empty.");
            return null;
        }
        PackageManager pm = context.getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(packageName);
        return launchIntent == null ? null : launchIntent.getComponent();
    }

    /**
     * Get a list of launcher activities.
     *
     * @param packageName Specify which package should these activities belong to. Pass null or
     *                    empty string ("") to get all activities of all installed packages.
     */
    public static List<ResolveInfo> getLauncherActivityList(@NonNull Context context,
                                                            @Nullable String packageName) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        if (!TextUtils.isEmpty(packageName)) {
            intent.setPackage(packageName);
        }
        return pm.queryIntentActivities(intent, 0);
    }

    /**
     * Get the long version code of the package corresponding to the given packageInfo. This method
     * can be used if the package is not installed on the device.
     */
    public static Long getLongVersionCode(PackageInfo packageInfo) {
        return packageInfo != null ? PackageInfoCompat.getLongVersionCode(packageInfo) : null;
    }

    /**
     * Get the long version code of the package corresponding to the given packageName. This method
     * should be used only if the package is installed on the device.
     */
    public static Long getLongVersionCode(@NonNull Context context, String packageName) {
        PackageInfo packageInfo = getPackageInfo(context, packageName);
        return getLongVersionCode(packageInfo);
    }

    /**
     * Get the version code of the package corresponding to the given packageInfo. This method
     * can be used if the package is not installed on the device.
     */
    public static Integer getVersionCode(PackageInfo packageInfo) {
        if (packageInfo != null) {
            Long longVersionCode = getLongVersionCode(packageInfo);
            return longVersionCode != null ? longVersionCode.intValue() : null;
        } else {
            return null;
        }
    }

    /**
     * Get the version code of the package corresponding to the given packageName. This method
     * should be used only if the package is installed on the device.
     */
    public static Integer getVersionCode(@NonNull Context context, String packageName) {
        Long longVersionCode = getLongVersionCode(context, packageName);
        return longVersionCode != null ? longVersionCode.intValue() : null;
    }
}