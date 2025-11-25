package com.android.appmanager.utils;

import android.content.Context;

import java.util.Locale;

public class LocaleUtils {
    public static Locale getPrimaryLocale(Context context) {
        return context.getResources().getConfiguration().getLocales().get(0);
    }
}