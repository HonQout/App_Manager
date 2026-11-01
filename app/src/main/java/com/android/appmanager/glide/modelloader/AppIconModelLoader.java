package com.android.appmanager.glide.modelloader;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.appmanager.data.model.PackageModel;
import com.android.appmanager.glide.datafetcher.AppIconDataFetcher;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

public class AppIconModelLoader implements ModelLoader<PackageModel, Drawable> {
    private final Context context;

    public AppIconModelLoader(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    @Nullable
    @Override
    public LoadData<Drawable> buildLoadData(@NonNull PackageModel packageModel,
                                            int width,
                                            int height,
                                            @NonNull Options options) {
        return new LoadData<>(new GlideUrl(packageModel.getPackageName()),
                new AppIconDataFetcher(context, packageModel));
    }

    @Override
    public boolean handles(@NonNull PackageModel packageModel) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<PackageModel, Drawable> {
        private final Context context;

        public Factory(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public ModelLoader<PackageModel, Drawable> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new AppIconModelLoader(context);
        }

        @Override
        public void teardown() {

        }
    }
}