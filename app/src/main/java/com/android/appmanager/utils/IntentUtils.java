package com.android.appmanager.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

public class IntentUtils {
    private static final String TAG = "IntentUtils";

    enum LaunchIntentResult {
        SUCCESS, URI_IS_EMPTY, NO_MATCHING_ACTIVITY
    }

    enum LaunchActivityResult {
        SUCCESS, NOT_EXPORTED, REQUIRE_PERMISSION, NOT_FOUND
    }

    public static LaunchIntentResult launchApplicationDetailsSettings(@NonNull Context context, String packageName) {
        if (packageName == null || TextUtils.isEmpty(packageName)) {
            Log.e(TAG, "Cannot launch Settings because the given packageName is null or empty.");
            return LaunchIntentResult.URI_IS_EMPTY;
        }
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.fromParts("package", packageName, null))
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
            return LaunchIntentResult.SUCCESS;
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Cannot launch Settings because requested package cannot be found.");
            return LaunchIntentResult.NO_MATCHING_ACTIVITY;
        }
    }

}
