package com.android.appmanager.utils;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.android.appmanager.R;

public class StringUtils {
    @StringRes
    public static int getLocaleJudgementRes(boolean condition) {
        return condition ? R.string.yes : R.string.no;
    }

    public static String getLocaleJudgement(Context context, boolean condition) {
        return ContextCompat.getString(context, getLocaleJudgementRes(condition));
    }

    public static String getLocaleEmpty(Context context, String string) {
        return TextUtils.isEmpty(string) ? context.getString(R.string.empty) : string;
    }
}