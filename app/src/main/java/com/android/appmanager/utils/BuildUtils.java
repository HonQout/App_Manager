package com.android.appmanager.utils;

import android.os.Build;

import java.util.Arrays;
import java.util.Locale;

public class BuildUtils {
    public static final String[] CHINESE_BRAND = new String[]{"huawei", "xiaomi", "oppo", "vivo", "honor", "meizu"};

    public static boolean isChineseBrand() {
        return Arrays.stream(BuildUtils.CHINESE_BRAND).toList().contains(Build.BRAND.toLowerCase(Locale.ROOT));
    }
}
