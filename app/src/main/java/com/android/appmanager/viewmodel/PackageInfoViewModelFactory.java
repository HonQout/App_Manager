package com.android.appmanager.viewmodel;

import android.app.Application;
import android.content.pm.PackageInfo;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PackageInfoViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final PackageInfo packageInfo;

    public PackageInfoViewModelFactory(Application application, PackageInfo packageInfo) {
        this.application = application;
        this.packageInfo = packageInfo;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PackageInfoViewModel.class)) {
            return (T) new PackageInfoViewModel(application, packageInfo);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
