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

import com.android.appmanager.bean.PackageBean;
import com.android.appmanager.utils.LocaleUtils;
import com.android.appmanager.utils.PackageUtils;
import com.android.appmanager.utils.PermissionUtils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppListViewModel extends AndroidViewModel {
    private final String TAG = "AppListViewModel";
    private final MutableLiveData<Map<String, PackageBean>> mAllPackageBeanMap = new MutableLiveData<>();
    private final MutableLiveData<List<PackageBean>> mAllPackageBeanList = new MutableLiveData<>();
    private final MutableLiveData<List<PackageBean>> mSystemPackageBeanList = new MutableLiveData<>();
    private final MutableLiveData<List<PackageBean>> mUserPackageBeanList = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    // Broadcast Receiver
    private BroadcastReceiver localeBroadcastReceiver = null;
    private BroadcastReceiver packageBroadcastReceiver = null;

    public AppListViewModel(@NonNull Application application) {
        super(application);
        registerLocaleBR(getApplication());
        registerPackageBR(getApplication());
        initPackageBean();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
        unregisterPackageBR(getApplication());
        unregisterLocaleBR(getApplication());
    }

    private void registerLocaleBR(Context context) {
        if (localeBroadcastReceiver != null) {
            return;
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_LOCALE_CHANGED);

        localeBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initPackageBean();
            }
        };

        context.registerReceiver(localeBroadcastReceiver, intentFilter);
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

        int receiverFlags = ContextCompat.RECEIVER_EXPORTED;

        packageBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null) {
                    Log.i(TAG, "Received intent is null.");
                } else {
                    String action = intent.getAction();
                    if (action == null) {
                        action = "null";
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

                        default: {
                            Log.e(TAG, "Received irrelevant intent.");
                            break;
                        }
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

    public void sortPackageBeanList(List<PackageBean> packageBeanList) {
        Context context = getApplication();
        Collator collator = Collator.getInstance(LocaleUtils.getPrimaryLocale(context));
        if (packageBeanList != null) {
            packageBeanList.sort((o1, o2) -> {
                String label1 = o1.getLabel();
                String label2 = o2.getLabel();
                return collator.compare(label1, label2);
            });
        }
    }

    public void initPackageBean() {
        Context context = getApplication();
        executorService.execute(() -> {
            if (PermissionUtils.checkQueryAllPackagesPermission(context)) {
                List<PackageInfo> packageInfoList = PackageUtils.getPackageInfoList(context);
                Map<String, PackageBean> allPackageBeanMap = new HashMap<>();
                List<PackageBean> allPackageBeanList = new ArrayList<>();
                List<PackageBean> systemPackageBeanList = new ArrayList<>();
                List<PackageBean> userPackageBeanList = new ArrayList<>();
                for (PackageInfo packageInfo : packageInfoList) {
                    PackageBean packageBean = new PackageBean(context, packageInfo);
                    allPackageBeanMap.put(packageInfo.packageName, packageBean);
                    allPackageBeanList.add(packageBean);
                }
                mAllPackageBeanMap.postValue(allPackageBeanMap);
                sortPackageBeanList(allPackageBeanList);
                mAllPackageBeanList.postValue(allPackageBeanList);
                for (PackageBean packageBean : allPackageBeanList) {
                    if (packageBean.isSystemApp()) {
                        systemPackageBeanList.add(packageBean);
                    } else {
                        userPackageBeanList.add(packageBean);
                    }
                }
                mSystemPackageBeanList.postValue(systemPackageBeanList);
                mUserPackageBeanList.postValue(userPackageBeanList);
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
            Map<String, PackageBean> allPackageBeanMap = mAllPackageBeanMap.getValue();
            List<PackageBean> allPackageBeanList = mAllPackageBeanList.getValue();
            List<PackageBean> systemPackageBeanList = mSystemPackageBeanList.getValue();
            List<PackageBean> userPackageBeanList = mUserPackageBeanList.getValue();
            // Get the new PackageBean
            PackageInfo newPackageInfo = PackageUtils.getPackageInfo(context, packageName);
            if (newPackageInfo == null) {
                Log.e(TAG, "Failed to get the PackageInfo of new app.");
                return;
            }
            PackageBean newPackageBean = new PackageBean(context, newPackageInfo);
            // No matter which list should we put the new PackageBean in, update the allPackageBeanMap first
            if (allPackageBeanMap != null) {
                try {
                    allPackageBeanMap.put(packageName, newPackageBean);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to put new PackageBean into allPackageBeanMap.", e);
                }
            }
            // No matter which list should we put the new PackageBean in, update the allPackageBeanList first
            if (allPackageBeanList != null) {
                try {
                    allPackageBeanList.add(newPackageBean);
                    sortPackageBeanList(allPackageBeanList);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to update PackageBean to allPackageBeanList.", e);
                }
            }
            // Decide which list to put the new one in
            if (newPackageBean.isSystemApp()) {
                if (systemPackageBeanList != null) {
                    try {
                        systemPackageBeanList.add(newPackageBean);
                        sortPackageBeanList(systemPackageBeanList);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to update package bean to systemPackageBeanList.", e);
                    }
                }
            } else {
                if (userPackageBeanList != null) {
                    try {
                        userPackageBeanList.add(newPackageBean);
                        sortPackageBeanList(userPackageBeanList);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to update package bean to userPackageBeanList.", e);
                    }
                }
            }
            // post
            if (allPackageBeanMap != null) {
                mAllPackageBeanMap.postValue(new HashMap<>(allPackageBeanMap));
            }
            if (allPackageBeanList != null) {
                mAllPackageBeanList.postValue(new ArrayList<>(allPackageBeanList));
            }
            if (systemPackageBeanList != null) {
                mSystemPackageBeanList.postValue(new ArrayList<>(systemPackageBeanList));
            }
            if (userPackageBeanList != null) {
                mUserPackageBeanList.postValue(new ArrayList<>(userPackageBeanList));
            }
        });
    }

    public void removePackageBean(@NonNull String packageName) {
        Context context = getApplication();
        executorService.execute(() -> {
            Map<String, PackageBean> allPackageBeanMap = mAllPackageBeanMap.getValue();
            List<PackageBean> allPackageBeanList = mAllPackageBeanList.getValue();
            List<PackageBean> systemPackageBeanList = mSystemPackageBeanList.getValue();
            List<PackageBean> userPackageBeanList = mUserPackageBeanList.getValue();
            // remove
            PackageBean oldPackageBean = null;
            if (allPackageBeanMap != null) {
                try {
                    oldPackageBean = allPackageBeanMap.get(packageName);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to get old package bean from allPackageBeanMap.", e);
                }
                if (oldPackageBean != null) {
                    try {
                        allPackageBeanMap.remove(packageName);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to remove old package bean from allPackageBeanMap.", e);
                    }
                    if (oldPackageBean.isSystemApp()) {
                        PackageInfo newPackageInfo = PackageUtils.getPackageInfo(context, packageName);
                        if (newPackageInfo == null) {
                            if (allPackageBeanList != null) {
                                try {
                                    allPackageBeanList.remove(oldPackageBean);
                                } catch (Exception e) {
                                    Log.e(TAG, "Failed to remove package bean from allPackageBeanList.", e);
                                }
                            }
                            if (systemPackageBeanList != null) {
                                try {
                                    systemPackageBeanList.remove(oldPackageBean);
                                } catch (Exception e) {
                                    Log.e(TAG, "Failed to remove package bean from systemPackageBeanList.", e);
                                }
                            }
                        } else {
                            PackageBean newPackageBean = new PackageBean(context, newPackageInfo);
                            allPackageBeanMap.put(packageName, newPackageBean);
                            String oldLabel = oldPackageBean.getLabel();
                            String newLabel = newPackageBean.getLabel();
                            if (oldLabel.equals(newLabel)) {
                                if (allPackageBeanList != null) {
                                    try {
                                        int index = allPackageBeanList.indexOf(oldPackageBean);
                                        allPackageBeanList.set(index, newPackageBean);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Failed to update package bean to allPackageBeanList.", e);
                                    }
                                }
                                if (systemPackageBeanList != null) {
                                    try {
                                        int index = systemPackageBeanList.indexOf(oldPackageBean);
                                        systemPackageBeanList.set(index, newPackageBean);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Failed to update package bean to systemPackageBeanList.", e);
                                    }
                                }
                            } else {
                                if (allPackageBeanList != null) {
                                    try {
                                        allPackageBeanList.remove(oldPackageBean);
                                        allPackageBeanList.add(newPackageBean);
                                        sortPackageBeanList(allPackageBeanList);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Failed to update package bean to allPackageBeanList.", e);
                                    }
                                }
                                if (systemPackageBeanList != null) {
                                    try {
                                        systemPackageBeanList.remove(oldPackageBean);
                                        systemPackageBeanList.add(newPackageBean);
                                        sortPackageBeanList(systemPackageBeanList);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Failed to update package bean to systemPackageBeanList.", e);
                                    }
                                }
                            }
                        }
                    } else {
                        if (allPackageBeanList != null) {
                            try {
                                allPackageBeanList.remove(oldPackageBean);
                            } catch (Exception e) {
                                Log.e(TAG, "Failed to remove package bean from allPackageBeanList.", e);
                            }
                        }
                        if (userPackageBeanList != null) {
                            try {
                                userPackageBeanList.remove(oldPackageBean);
                            } catch (Exception e) {
                                Log.e(TAG, "Failed to remove package bean from userPackageBeanList.", e);
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
            if (allPackageBeanList != null) {
                mAllPackageBeanList.postValue(new ArrayList<>(allPackageBeanList));
            }
            if (systemPackageBeanList != null) {
                mSystemPackageBeanList.postValue(new ArrayList<>(systemPackageBeanList));
            }
            if (userPackageBeanList != null) {
                mUserPackageBeanList.postValue(new ArrayList<>(userPackageBeanList));
            }
        });
    }

    public void replacePackageBean(@NonNull String packageName) {
        Context context = getApplication();
        executorService.execute(() -> {
            Map<String, PackageBean> allPackageBeanMap = mAllPackageBeanMap.getValue();
            List<PackageBean> allPackageBeanList = mAllPackageBeanList.getValue();
            List<PackageBean> systemPackageBeanList = mSystemPackageBeanList.getValue();
            List<PackageBean> userPackageBeanList = mUserPackageBeanList.getValue();
            // Get the new PackageBean
            PackageInfo newPackageInfo = PackageUtils.getPackageInfo(context, packageName);
            if (newPackageInfo == null) {
                Log.e(TAG, "Failed to get the packageInfo of new version.");
                return;
            }
            PackageBean newPackageBean = new PackageBean(context, newPackageInfo);
            // Get the old PackageBean and replace it by the new one
            PackageBean oldPackageBean = null;
            if (allPackageBeanMap != null) {
                try {
                    oldPackageBean = allPackageBeanMap.get(packageName);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to get old package bean from allPackageBeanMap.", e);
                }
                try {
                    allPackageBeanMap.put(packageName, newPackageBean);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to put new package bean into allPackageBeanMap.", e);
                }
            }
            // Decide whether the old packageBean is null
            if (oldPackageBean != null) {
                // If the old packageBean isn't null, we should replace the old one by the new one
                String oldLabel = oldPackageBean.getLabel();
                String newLabel = newPackageBean.getLabel();
                // Decide whether the oldLabel equals the newLabel
                if (oldLabel.equals(newLabel)) {
                    // If oldLabel equals newLabel, we can simply set the old one to the new one without re-sorting the lists
                    // No matter which list should we put the new one in, update the allPackageBeanList first
                    if (allPackageBeanList != null) {
                        try {
                            int index = allPackageBeanList.indexOf(oldPackageBean);
                            allPackageBeanList.set(index, newPackageBean);
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to update package bean to allPackageBeanList.", e);
                        }
                    }
                    // Decide which list to put the new one in
                    if (newPackageBean.isSystemApp()) {
                        if (systemPackageBeanList != null) {
                            try {
                                int index = systemPackageBeanList.indexOf(oldPackageBean);
                                systemPackageBeanList.set(index, newPackageBean);
                            } catch (Exception e) {
                                Log.e(TAG, "Failed to update package bean to systemPackageBeanList.", e);
                            }
                        }
                    } else {
                        if (userPackageBeanList != null) {
                            try {
                                int index = userPackageBeanList.indexOf(oldPackageBean);
                                userPackageBeanList.set(index, newPackageBean);
                            } catch (Exception e) {
                                Log.e(TAG, "Failed to update package bean to userPackageBeanList.", e);
                            }
                        }
                    }
                } else {
                    // If the oldLabel is inconsistent with the newLabel, we should re-sort the lists after adding the new one into them
                    // No matter which list should we put the new one in, update the allPackageBeanList first
                    if (allPackageBeanList != null) {
                        try {
                            allPackageBeanList.remove(oldPackageBean);
                            allPackageBeanList.add(newPackageBean);
                            sortPackageBeanList(allPackageBeanList);
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to update package bean to allPackageBeanList.", e);
                        }
                    }
                    // Decide which list to put the new one in
                    if (newPackageBean.isSystemApp()) {
                        if (systemPackageBeanList != null) {
                            try {
                                systemPackageBeanList.remove(oldPackageBean);
                                systemPackageBeanList.add(newPackageBean);
                                sortPackageBeanList(systemPackageBeanList);
                            } catch (Exception e) {
                                Log.e(TAG, "Failed to update package bean to systemPackageBeanList.", e);
                            }
                        }
                    } else {
                        if (userPackageBeanList != null) {
                            try {
                                userPackageBeanList.remove(oldPackageBean);
                                userPackageBeanList.add(newPackageBean);
                                sortPackageBeanList(userPackageBeanList);
                            } catch (Exception e) {
                                Log.e(TAG, "Failed to update package bean to userPackageBeanList.", e);
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
            if (allPackageBeanList != null) {
                mAllPackageBeanList.postValue(new ArrayList<>(allPackageBeanList));
            }
            if (systemPackageBeanList != null) {
                mSystemPackageBeanList.postValue(new ArrayList<>(systemPackageBeanList));
            }
            if (userPackageBeanList != null) {
                mUserPackageBeanList.postValue(new ArrayList<>(userPackageBeanList));
            }
        });
    }

    public LiveData<List<PackageBean>> getAllPackageBeanList() {
        return mAllPackageBeanList;
    }

    public LiveData<List<PackageBean>> getSystemPackageBeanList() {
        return mSystemPackageBeanList;
    }

    public LiveData<List<PackageBean>> getUserPackageBeanList() {
        return mUserPackageBeanList;
    }
}