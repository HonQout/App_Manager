package com.android.appmanager.glide.datafetcher;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.android.appmanager.utils.ApplicationUtils;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

public class ActivityIconDataFetcher implements DataFetcher<Drawable> {
    private final Context context;
    private final String packageName;
    private final String activityName;

    public ActivityIconDataFetcher(@NonNull Context context, String packageName, String activityName) {
        this.context = context;
        this.packageName = packageName;
        this.activityName = activityName;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super Drawable> callback) {
        try {
            Drawable icon = ApplicationUtils.getActivityIcon(context, packageName, activityName);
            callback.onDataReady(icon);
        } catch (Exception e) {
            callback.onLoadFailed(e);
        }
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void cancel() {

    }

    @NonNull
    @Override
    public Class<Drawable> getDataClass() {
        return Drawable.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }
}
