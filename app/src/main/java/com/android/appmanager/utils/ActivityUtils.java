package com.android.appmanager.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

public class ActivityUtils {
    private static final String TAG = "ActivityUtils";

    public enum LaunchActivityResult {
        SUCCESS, NOT_EXPORTED, REQUIRE_PERMISSION, NOT_FOUND
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
