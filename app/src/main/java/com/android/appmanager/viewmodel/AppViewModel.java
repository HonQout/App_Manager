package com.android.appmanager.viewmodel;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.appmanager.data.model.PackageModel;
import com.android.appmanager.utils.PackageUtils;
import com.android.appmanager.utils.PermissionUtils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class AppViewModel extends AndroidViewModel {
    private final String TAG = "AppViewModel";

    public enum PackageListOp {
        INIT, ADD, REMOVE, REPLACE
    }

    // multithread
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final AtomicBoolean isUpdatingAllPackageBeanMap = new AtomicBoolean(false);
    private final Object allPackageBeanMapLock = new Object();
    private final AtomicBoolean isUpdatingAllPackageBeanList = new AtomicBoolean(false);
    private final Object allPackageBeanListLock = new Object();
    private final AtomicBoolean isUpdatingSystemPackageBeanList = new AtomicBoolean(false);
    private final Object systemPackageBeanListLock = new Object();
    private final AtomicBoolean isUpdatingUserPackageBeanList = new AtomicBoolean(false);
    private final Object userPackageBeanListLock = new Object();

    // data
    private final MutableLiveData<Map<String, PackageModel>> mAllPackageBeanMap = new MutableLiveData<>();
    private final MutableLiveData<List<PackageModel>> mAllPackageBeanList = new MutableLiveData<>();
    private final MutableLiveData<List<PackageModel>> mSystemPackageBeanList = new MutableLiveData<>();
    private final MutableLiveData<List<PackageModel>> mUserPackageBeanList = new MutableLiveData<>();

    // broadcast receiver
    private BroadcastReceiver localeBroadcastReceiver = null;
    private BroadcastReceiver packageBroadcastReceiver = null;

    public AppViewModel(@NonNull Application application) {
        super(application);
        registerLocaleBR(getApplication());
        registerPackageBR(getApplication());
        init();
    }

    @Override
    protected void onCleared() {
        executorService.shutdown();
        unregisterPackageBR(getApplication());
        unregisterLocaleBR(getApplication());
        super.onCleared();
    }

    private void registerLocaleBR(Context context) {
        if (localeBroadcastReceiver != null) {
            return;
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_LOCALE_CHANGED);

        int receiverFlags = ContextCompat.RECEIVER_NOT_EXPORTED;

        localeBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                init();
            }
        };

        ContextCompat.registerReceiver(context, localeBroadcastReceiver, intentFilter, receiverFlags);
    }

    private void unregisterLocaleBR(Context context) {
        if (localeBroadcastReceiver != null) {
            context.unregisterReceiver(localeBroadcastReceiver);
            localeBroadcastReceiver = null;
        }
    }

    private void registerPackageBR(Context context) {
        if (packageBroadcastReceiver != null) {
            return;
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addDataScheme("package");

        int receiverFlags = ContextCompat.RECEIVER_NOT_EXPORTED;

        packageBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null) {
                    Log.i(TAG, "Received intent is null.");
                    return;
                }
                String action = intent.getAction();
                if (action == null) {
                    Log.e(TAG, "Action of intent is null.");
                    return;
                }
                Log.i(TAG, "Received intent action: " + action);
                switch (action) {
                    case Intent.ACTION_PACKAGE_ADDED: {
                        boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
                        Log.i(TAG, "Extra: replacing = " + replacing);
                        if (!replacing) {
                            String packageName;
                            Uri data = intent.getData();
                            if (data != null) {
                                packageName = data.getSchemeSpecificPart();
                                if (packageName != null) {
                                    addPackageBean(packageName);
                                }
                            }
                        }
                        break;
                    }

                    case Intent.ACTION_PACKAGE_REMOVED: {
                        boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
                        boolean dataRemoved = intent.getBooleanExtra(Intent.EXTRA_DATA_REMOVED, false);
                        Log.i(TAG, "Extra: replacing = " + replacing + ", data_removed = " + dataRemoved);
                        if (!replacing) {
                            String packageName;
                            Uri data = intent.getData();
                            if (data != null) {
                                packageName = data.getSchemeSpecificPart();
                                if (packageName != null) {
                                    removePackageBean(packageName);
                                }
                            }
                        }
                        break;
                    }

                    case Intent.ACTION_PACKAGE_REPLACED: {
                        Log.i(TAG, "Replace");
                        String packageName;
                        Uri data = intent.getData();
                        if (data != null) {
                            packageName = data.getSchemeSpecificPart();
                            if (packageName != null) {
                                replacePackageBean(packageName);
                            }
                        }
                        break;
                    }
                }
            }
        };

        ContextCompat.registerReceiver(context, packageBroadcastReceiver, intentFilter, receiverFlags);
    }

    private void unregisterPackageBR(Context context) {
        if (packageBroadcastReceiver != null) {
            context.unregisterReceiver(packageBroadcastReceiver);
            packageBroadcastReceiver = null;
        }
    }

    public void sortPackageBeanList(List<PackageModel> list) {
        Collator collator = Collator.getInstance();
        if (list != null) {
            list.sort((o1, o2) -> {
                String label1 = o1.getLabel();
                String label2 = o2.getLabel();
                return collator.compare(label1, label2);
            });
        }
    }

    public void init() {
        Context context = getApplication();
        executorService.execute(() -> {
            if (PermissionUtils.checkQueryAllPackagesPermission(context)) {
                List<PackageInfo> packageInfoList = PackageUtils.getPackageInfoList(context);
                Map<String, PackageModel> allPackageBeanMap = new HashMap<>();
                List<PackageModel> allPackageModelList = new ArrayList<>();
                List<PackageModel> systemPackageModelList = new ArrayList<>();
                List<PackageModel> userPackageModelList = new ArrayList<>();
                for (PackageInfo packageInfo : packageInfoList) {
                    PackageModel packageModel = new PackageModel(context, packageInfo);
                    allPackageBeanMap.put(packageInfo.packageName, packageModel);
                    allPackageModelList.add(packageModel);
                }
                mAllPackageBeanMap.postValue(allPackageBeanMap);
                sortPackageBeanList(allPackageModelList);
                mAllPackageBeanList.postValue(allPackageModelList);
                for (PackageModel packageModel : allPackageModelList) {
                    if (packageModel.isSystemApp()) {
                        systemPackageModelList.add(packageModel);
                    } else {
                        userPackageModelList.add(packageModel);
                    }
                }
                mSystemPackageBeanList.postValue(systemPackageModelList);
                mUserPackageBeanList.postValue(userPackageModelList);
            } else {
                Log.e(TAG, "Permission to query all packages is denied.");
                mAllPackageBeanMap.postValue(new HashMap<>());
                mAllPackageBeanList.postValue(new ArrayList<>());
                mSystemPackageBeanList.postValue(new ArrayList<>());
                mUserPackageBeanList.postValue(new ArrayList<>());
            }
        });
    }

    public void addPackageBean(@NonNull String packageName) {
        Context context = getApplication();
        executorService.execute(() -> {
            Map<String, PackageModel> allPackageBeanMap = mAllPackageBeanMap.getValue();
            List<PackageModel> allPackageModelList = mAllPackageBeanList.getValue();
            List<PackageModel> systemPackageModelList = mSystemPackageBeanList.getValue();
            List<PackageModel> userPackageModelList = mUserPackageBeanList.getValue();
            // Get the new PackageModel
            PackageInfo newPackageInfo = PackageUtils.getPackageInfo(context, packageName);
            if (newPackageInfo == null) {
                Log.e(TAG, "Failed to get the PackageInfo of new app.");
                return;
            }
            PackageModel newPackageModel = new PackageModel(context, newPackageInfo);
            // No matter which list should we put the new PackageModel in, update the allPackageBeanMap first
            if (allPackageBeanMap != null) {
                try {
                    synchronized (allPackageBeanMapLock) {
                        allPackageBeanMap.put(packageName, newPackageModel);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Failed to put new PackageModel into allPackageBeanMap.", e);
                }
            }
            // No matter which list should we put the new PackageModel in, update the allPackageModelList first
            if (allPackageModelList != null) {
                try {
                    synchronized (allPackageBeanListLock) {
                        allPackageModelList.add(newPackageModel);
                        sortPackageBeanList(allPackageModelList);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Failed to update PackageModel to allPackageModelList.", e);
                }
            }
            // Decide which list to put the new one in
            if (newPackageModel.isSystemApp()) {
                if (systemPackageModelList != null) {
                    try {
                        synchronized (systemPackageBeanListLock) {
                            systemPackageModelList.add(newPackageModel);
                            sortPackageBeanList(systemPackageModelList);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to update package bean to systemPackageModelList.", e);
                    }
                }
            } else {
                if (userPackageModelList != null) {
                    try {
                        synchronized (userPackageBeanListLock) {
                            userPackageModelList.add(newPackageModel);
                            sortPackageBeanList(userPackageModelList);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to update package bean to userPackageModelList.", e);
                    }
                }
            }
            // post
            if (allPackageBeanMap != null) {
                mAllPackageBeanMap.postValue(new HashMap<>(allPackageBeanMap));
            }
            if (allPackageModelList != null) {
                mAllPackageBeanList.postValue(new ArrayList<>(allPackageModelList));
            }
            if (systemPackageModelList != null) {
                mSystemPackageBeanList.postValue(new ArrayList<>(systemPackageModelList));
            }
            if (userPackageModelList != null) {
                mUserPackageBeanList.postValue(new ArrayList<>(userPackageModelList));
            }
        });
    }

    public void removePackageBean(@NonNull String packageName) {
        Context context = getApplication();
        executorService.execute(() -> {
            Map<String, PackageModel> allPackageBeanMap = mAllPackageBeanMap.getValue();
            List<PackageModel> allPackageModelList = mAllPackageBeanList.getValue();
            List<PackageModel> systemPackageModelList = mSystemPackageBeanList.getValue();
            List<PackageModel> userPackageModelList = mUserPackageBeanList.getValue();
            PackageModel oldPackageModel = null;
            if (allPackageBeanMap != null) {
                // Get the old PackageModel
                try {
                    synchronized (allPackageBeanMapLock) {
                        oldPackageModel = allPackageBeanMap.get(packageName);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Failed to get old package bean from allPackageBeanMap.", e);
                }
                if (oldPackageModel != null) {
                    // If old PackageModel is not null, remove it from allPackageBeanMap first
                    try {
                        synchronized (allPackageBeanMapLock) {
                            allPackageBeanMap.remove(packageName);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to remove old package bean from allPackageBeanMap.", e);
                    }
                    // If old PackageModel is system app
                    if (oldPackageModel.isSystemApp()) {
                        // Get its new PackageInfo
                        PackageInfo newPackageInfo = PackageUtils.getPackageInfo(context, packageName);
                        if (newPackageInfo == null) {
                            // If its new PackageInfo is null, which indicates that it is uninstalled
                            // (some system allow this to happen), remove it from allPackageModelList
                            // and systemPackageModelList
                            if (allPackageModelList != null) {
                                try {
                                    synchronized (allPackageBeanListLock) {
                                        allPackageModelList.remove(oldPackageModel);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Failed to remove package bean from allPackageModelList.", e);
                                }
                            }
                            if (systemPackageModelList != null) {
                                try {
                                    synchronized (systemPackageBeanListLock) {
                                        systemPackageModelList.remove(oldPackageModel);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Failed to remove package bean from systemPackageModelList.", e);
                                }
                            }
                        } else {
                            PackageModel newPackageModel = new PackageModel(context, newPackageInfo);
                            allPackageBeanMap.put(packageName, newPackageModel);
                            String oldLabel = oldPackageModel.getLabel();
                            String newLabel = newPackageModel.getLabel();
                            if (oldLabel.equals(newLabel)) {
                                if (allPackageModelList != null) {
                                    try {
                                        int index = allPackageModelList.indexOf(oldPackageModel);
                                        allPackageModelList.set(index, newPackageModel);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Failed to update package bean to allPackageModelList.", e);
                                    }
                                }
                                if (systemPackageModelList != null) {
                                    try {
                                        int index = systemPackageModelList.indexOf(oldPackageModel);
                                        systemPackageModelList.set(index, newPackageModel);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Failed to update package bean to systemPackageModelList.", e);
                                    }
                                }
                            } else {
                                if (allPackageModelList != null) {
                                    try {
                                        allPackageModelList.remove(oldPackageModel);
                                        allPackageModelList.add(newPackageModel);
                                        sortPackageBeanList(allPackageModelList);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Failed to update package bean to allPackageModelList.", e);
                                    }
                                }
                                if (systemPackageModelList != null) {
                                    try {
                                        systemPackageModelList.remove(oldPackageModel);
                                        systemPackageModelList.add(newPackageModel);
                                        sortPackageBeanList(systemPackageModelList);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Failed to update package bean to systemPackageModelList.", e);
                                    }
                                }
                            }
                        }
                    } else {
                        if (allPackageModelList != null) {
                            try {
                                allPackageModelList.remove(oldPackageModel);
                            } catch (Exception e) {
                                Log.e(TAG, "Failed to remove package bean from allPackageModelList.", e);
                            }
                        }
                        if (userPackageModelList != null) {
                            try {
                                userPackageModelList.remove(oldPackageModel);
                            } catch (Exception e) {
                                Log.e(TAG, "Failed to remove package bean from userPackageModelList.", e);
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "Required package bean doesn't exist in allPackageBeanMap.");
                }
            }
            // post
            if (allPackageBeanMap != null) {
                mAllPackageBeanMap.postValue(new HashMap<>(allPackageBeanMap));
            }
            if (allPackageModelList != null) {
                mAllPackageBeanList.postValue(new ArrayList<>(allPackageModelList));
            }
            if (systemPackageModelList != null) {
                mSystemPackageBeanList.postValue(new ArrayList<>(systemPackageModelList));
            }
            if (userPackageModelList != null) {
                mUserPackageBeanList.postValue(new ArrayList<>(userPackageModelList));
            }
        });
    }

    public void replacePackageBean(@NonNull String packageName) {
        Context context = getApplication();
        executorService.execute(() -> {
            Map<String, PackageModel> allPackageBeanMap = mAllPackageBeanMap.getValue();
            List<PackageModel> allPackageModelList = mAllPackageBeanList.getValue();
            List<PackageModel> systemPackageModelList = mSystemPackageBeanList.getValue();
            List<PackageModel> userPackageModelList = mUserPackageBeanList.getValue();
            // Get the new PackageModel
            PackageInfo newPackageInfo = PackageUtils.getPackageInfo(context, packageName);
            if (newPackageInfo == null) {
                Log.e(TAG, "Failed to get the packageInfo of new version.");
                return;
            }
            PackageModel newPackageModel = new PackageModel(context, newPackageInfo);
            // Get the old PackageModel and replace it by the new one
            PackageModel oldPackageModel = null;
            if (allPackageBeanMap != null) {
                try {
                    oldPackageModel = allPackageBeanMap.get(packageName);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to get old package bean from allPackageBeanMap.", e);
                }
                try {
                    allPackageBeanMap.put(packageName, newPackageModel);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to put new package bean into allPackageBeanMap.", e);
                }
            }
            // Decide whether the old packageBean is null
            if (oldPackageModel != null) {
                // If the old packageBean isn't null, we should replace the old one by the new one
                String oldLabel = oldPackageModel.getLabel();
                String newLabel = newPackageModel.getLabel();
                // Decide whether the oldLabel equals the newLabel
                if (oldLabel.equals(newLabel)) {
                    // If oldLabel equals newLabel, we can simply set the old one to the new one without re-sorting the lists
                    // No matter which list should we put the new one in, update the allPackageModelList first
                    if (allPackageModelList != null) {
                        try {
                            int index = allPackageModelList.indexOf(oldPackageModel);
                            allPackageModelList.set(index, newPackageModel);
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to update package bean to allPackageModelList.", e);
                        }
                    }
                    // Decide which list to put the new one in
                    if (newPackageModel.isSystemApp()) {
                        if (systemPackageModelList != null) {
                            try {
                                int index = systemPackageModelList.indexOf(oldPackageModel);
                                systemPackageModelList.set(index, newPackageModel);
                            } catch (Exception e) {
                                Log.e(TAG, "Failed to update package bean to systemPackageModelList.", e);
                            }
                        }
                    } else {
                        if (userPackageModelList != null) {
                            try {
                                int index = userPackageModelList.indexOf(oldPackageModel);
                                userPackageModelList.set(index, newPackageModel);
                            } catch (Exception e) {
                                Log.e(TAG, "Failed to update package bean to userPackageModelList.", e);
                            }
                        }
                    }
                } else {
                    // If the oldLabel is inconsistent with the newLabel, we should re-sort the lists after adding the new one into them
                    // No matter which list should we put the new one in, update the allPackageModelList first
                    if (allPackageModelList != null) {
                        try {
                            allPackageModelList.remove(oldPackageModel);
                            allPackageModelList.add(newPackageModel);
                            sortPackageBeanList(allPackageModelList);
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to update package bean to allPackageModelList.", e);
                        }
                    }
                    // Decide which list to put the new one in
                    if (newPackageModel.isSystemApp()) {
                        if (systemPackageModelList != null) {
                            try {
                                systemPackageModelList.remove(oldPackageModel);
                                systemPackageModelList.add(newPackageModel);
                                sortPackageBeanList(systemPackageModelList);
                            } catch (Exception e) {
                                Log.e(TAG, "Failed to update package bean to systemPackageModelList.", e);
                            }
                        }
                    } else {
                        if (userPackageModelList != null) {
                            try {
                                userPackageModelList.remove(oldPackageModel);
                                userPackageModelList.add(newPackageModel);
                                sortPackageBeanList(userPackageModelList);
                            } catch (Exception e) {
                                Log.e(TAG, "Failed to update package bean to userPackageModelList.", e);
                            }
                        }
                    }
                }
            } else {
                Log.e(TAG, "Required package bean doesn't exist in allPackageBeanMap.");
            }
            // post
            if (allPackageBeanMap != null) {
                mAllPackageBeanMap.postValue(new HashMap<>(allPackageBeanMap));
            }
            if (allPackageModelList != null) {
                mAllPackageBeanList.postValue(new ArrayList<>(allPackageModelList));
            }
            if (systemPackageModelList != null) {
                mSystemPackageBeanList.postValue(new ArrayList<>(systemPackageModelList));
            }
            if (userPackageModelList != null) {
                mUserPackageBeanList.postValue(new ArrayList<>(userPackageModelList));
            }
        });
    }

    public LiveData<List<PackageModel>> getAllPackageBeanList() {
        return mAllPackageBeanList;
    }

    public LiveData<List<PackageModel>> getSystemPackageBeanList() {
        return mSystemPackageBeanList;
    }

    public LiveData<List<PackageModel>> getUserPackageBeanList() {
        return mUserPackageBeanList;
    }
}