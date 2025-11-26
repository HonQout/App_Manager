package com.android.appmanager.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.android.appmanager.R;

public class ActivityUtils {
    private static final String TAG = "ActivityUtils";

    public enum LaunchMode {
        MULTIPLE, SINGLE_TOP, SINGLE_TASK, SINGLE_INSTANCE, SINGLE_INSTANCE_PER_TASK
    }

    public enum LaunchActivityResult {
        SUCCESS, NOT_EXPORTED, REQUIRE_PERMISSION, NOT_FOUND
    }

    @StringRes
    public static int getLocaleLaunchModeRes(ActivityInfo activityInfo) {
        int launchMode = activityInfo.launchMode;
        switch (launchMode) {
            case ActivityInfo.LAUNCH_MULTIPLE:
                return R.string.multiple;
            case ActivityInfo.LAUNCH_SINGLE_TOP:
                return R.string.single_top;
            case ActivityInfo.LAUNCH_SINGLE_TASK:
                return R.string.single_task;
            case ActivityInfo.LAUNCH_SINGLE_INSTANCE:
                return R.string.single_instance;
            case ActivityInfo.LAUNCH_SINGLE_INSTANCE_PER_TASK:
                return R.string.single_instance_per_task;
            default:
                return R.string.unknown;
        }
    }

    public static LaunchActivityResult LaunchActivity(@NonNull Context context, @NonNull ActivityInfo activityInfo) {
        if (!activityInfo.exported) {
            Log.e(TAG, "Cannot launch activity. Requested activity is not exported.");
            return LaunchActivityResult.NOT_EXPORTED;
        } else if (!TextUtils.isEmpty(activityInfo.permission)) {
            Log.e(TAG, "Cannot launch activity. Requested activity requires extra permission to start." +
                    "\nPermission: " + activityInfo.permission);
            return LaunchActivityResult.REQUIRE_PERMISSION;
        } else {
            String packageName = activityInfo.packageName;
            String activityName = activityInfo.name;
            Intent intent = new Intent();
            intent.setClassName(packageName, activityName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(intent);
                return LaunchActivityResult.SUCCESS;
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "Cannot find requested activity.", e);
            }
        }
        return LaunchActivityResult.NOT_FOUND;
    }
}
