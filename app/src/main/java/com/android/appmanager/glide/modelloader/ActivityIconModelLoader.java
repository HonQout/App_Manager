package com.android.appmanager.glide.modelloader;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.appmanager.data.model.ActivityModel;
import com.android.appmanager.glide.datafetcher.ActivityIconDataFetcher;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

public class ActivityIconModelLoader implements ModelLoader<ActivityModel, Drawable> {
    private final Context context;

    public ActivityIconModelLoader(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    @Nullable
    @Override
    public LoadData<Drawable> buildLoadData(@NonNull ActivityModel activityModel,
                                            int width,
                                            int height,
                                            @NonNull Options options) {
        return new LoadData<>(new GlideUrl(
                activityModel.getKey()),
                new ActivityIconDataFetcher(context, activityModel.getPackageName(), activityModel.getActivityName())
        );
    }

    @Override
    public boolean handles(@NonNull ActivityModel activityModel) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<ActivityModel, Drawable> {
        private final Context context;

        public Factory(@NonNull Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public ModelLoader<ActivityModel, Drawable> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new ActivityIconModelLoader(context);
        }

        @Override
        public void teardown() {

        }
    }
}
