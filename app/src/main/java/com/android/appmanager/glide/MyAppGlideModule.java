package com.android.appmanager.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.android.appmanager.data.model.ActivityModel;
import com.android.appmanager.data.model.PackageModel;
import com.android.appmanager.glide.modelloader.ActivityIconModelLoader;
import com.android.appmanager.glide.modelloader.AppIconModelLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

@GlideModule
public class MyAppGlideModule extends AppGlideModule {
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide,
                                   @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        registry.append(PackageModel.class, Drawable.class, new AppIconModelLoader.Factory(context));
        registry.append(ActivityModel.class, Drawable.class, new ActivityIconModelLoader.Factory(context));
    }
}