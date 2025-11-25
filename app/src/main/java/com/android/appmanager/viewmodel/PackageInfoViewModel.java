package com.android.appmanager.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.android.appmanager.R;
import com.android.appmanager.adapter.recyclerview.TCHListAdapter;
import com.android.appmanager.bean.ActivityBean;
import com.android.appmanager.bean.PackageBean;
import com.android.appmanager.bean.PermissionBean;
import com.android.appmanager.bean.ProviderBean;
import com.android.appmanager.bean.ReceiverBean;
import com.android.appmanager.bean.ServiceBean;
import com.android.appmanager.bean.item.TCItem;
import com.android.appmanager.bean.permission.PermissionSorted;
import com.android.appmanager.utils.ActivityUtils;
import com.android.appmanager.utils.LocaleUtils;
import com.android.appmanager.utils.PackageUtils;
import com.android.appmanager.utils.StringUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PackageInfoViewModel extends AndroidViewModel {
    private final String TAG = "PackageInfoViewModel";
    public final int[] tabTitleResources = new int[]{R.string.overview, R.string.permission_defined,
            R.string.permission_requested, R.string.activity, R.string.service,
            R.string.content_provider, R.string.broadcast_receiver};
    private final MutableLiveData<PackageInfo> mPackageInfo = new MutableLiveData<>();
    private final MutableLiveData<List<TCItem>> mOverviewList = new MutableLiveData<>();
    private final MutableLiveData<List<PermissionBean>> mDefinedPermissionBeanList = new MutableLiveData<>();
    private final MutableLiveData<List<PermissionBean>> mRequestedPermissionBeanList = new MutableLiveData<>();
    private final MutableLiveData<List<String>> mPermissionNotFoundList = new MutableLiveData<>();
    private final MutableLiveData<List<ActivityBean>> mActivityBeanList = new MutableLiveData<>();
    private final MutableLiveData<List<ServiceBean>> mServiceBeanList = new MutableLiveData<>();
    private final MutableLiveData<List<ProviderBean>> mProviderBeanList = new MutableLiveData<>();
    private final MutableLiveData<List<ReceiverBean>> mReceiverBeanList = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    // Broadcast Receiver
    private BroadcastReceiver localeBroadcastReceiver = null;

    public PackageInfoViewModel(@NonNull Application application) {
        super(application);
    }

    public PackageInfoViewModel(@NonNull Application application, @NonNull PackageInfo packageInfo) {
        super(application);
        initPackageInfo(packageInfo);
        registerLocaleBR(getApplication());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
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
                initPackageInfo(mPackageInfo.getValue());
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

    public void initPackageInfo(PackageInfo packageInfo) {
        executorService.execute(() -> {
            Context context = getApplication();
            mPackageInfo.postValue(packageInfo);
            PackageBean packageBean = new PackageBean(context, packageInfo);
            String packageName = packageInfo.packageName;
            // Overview
            List<TCItem> overviewList = new ArrayList<>();
            overviewList.add(new TCItem(context.getString(R.string.name), packageBean.getLabel()));
            overviewList.add(new TCItem(context.getString(R.string.package_name), packageName));
            overviewList.add(new TCItem(context.getString(R.string.version), packageBean.getVersion()));
            overviewList.add(new TCItem(context.getString(R.string.target_sdk_version), packageBean.getTargetSdkVersion()));
            overviewList.add(new TCItem(context.getString(R.string.min_sdk_version), packageBean.getMinSdkVersion()));
            overviewList.add(new TCItem(context.getString(R.string.compile_sdk_version), packageBean.getCompileSdkVersion()));
            overviewList.add(new TCItem(context.getString(R.string.shared_user_id), packageBean.getSharedUserId()));
            overviewList.add(new TCItem(context.getString(R.string.shared_user_label), packageBean.getSharedUserLabel()));
            overviewList.add(new TCItem(context.getString(R.string.uid), packageBean.getUid()));
            overviewList.add(new TCItem(context.getString(R.string.system_app), StringUtils.getLocaleJudgement(context, packageBean.isSystemApp())));
            overviewList.add(new TCItem(context.getString(R.string.allow_clear_user_data), StringUtils.getLocaleJudgement(context, packageBean.doesAllowClearUserData())));
            overviewList.add(new TCItem(context.getString(R.string.allow_backup), StringUtils.getLocaleJudgement(context, packageBean.doesAllowBackup())));
            overviewList.add(new TCItem(context.getString(R.string.enabled), StringUtils.getLocaleJudgement(context, packageBean.isEnabled())));
            overviewList.add(new TCItem(context.getString(R.string.first_install_time), packageBean.getFirstInstallTime()));
            overviewList.add(new TCItem(context.getString(R.string.last_update_time), packageBean.getLastUpdateTime()));
            overviewList.add(new TCItem(context.getString(R.string.install_source), packageBean.getInstallSource(context)));
            mOverviewList.postValue(overviewList);
            // Others
            PermissionSorted permissionSorted = PackageUtils.getPermissionSorted(context, packageName);
            List<PermissionBean> definedPermissionBeanList = new ArrayList<>();
            for (PermissionInfo permissionInfo : permissionSorted.permissionDefinedList) {
                definedPermissionBeanList.add(new PermissionBean(context, permissionInfo));
            }
            sortPermissionBeanList(definedPermissionBeanList);
            mDefinedPermissionBeanList.postValue(definedPermissionBeanList);
            List<PermissionBean> requestedPermissionBeanList = new ArrayList<>();
            for (PermissionInfo permissionInfo : permissionSorted.permissionRequestedList) {
                requestedPermissionBeanList.add(new PermissionBean(context, permissionInfo));
            }
            sortPermissionBeanList(requestedPermissionBeanList);
            mRequestedPermissionBeanList.postValue(requestedPermissionBeanList);
            mPermissionNotFoundList.postValue(permissionSorted.permissionNotFoundList);
            List<ActivityInfo> activityInfoList = PackageUtils.getActivityInfoList(context, packageName);
            List<ActivityBean> activityBeanList = new ArrayList<>();
            for (ActivityInfo activityInfo : activityInfoList) {
                activityBeanList.add(new ActivityBean(context, activityInfo));
            }
            sortActivityBeanList(activityBeanList);
            mActivityBeanList.postValue(activityBeanList);
            List<ServiceInfo> serviceInfoList = PackageUtils.getServiceInfoList(context, packageName);
            List<ServiceBean> serviceBeanList = new ArrayList<>();
            for (ServiceInfo serviceInfo : serviceInfoList) {
                serviceBeanList.add(new ServiceBean(context, serviceInfo));
            }
            sortServiceBeanList(serviceBeanList);
            mServiceBeanList.postValue(serviceBeanList);
            List<ProviderInfo> providerInfoList = PackageUtils.getProviderInfoList(context, packageName);
            List<ProviderBean> providerBeanList = new ArrayList<>();
            for (ProviderInfo providerInfo : providerInfoList) {
                providerBeanList.add(new ProviderBean(context, providerInfo));
            }
            sortProviderBeanList(providerBeanList);
            mProviderBeanList.postValue(providerBeanList);
            List<ActivityInfo> receiverInfoList = PackageUtils.getReceiverInfoList(context, packageName);
            List<ReceiverBean> receiverBeanList = new ArrayList<>();
            for (ActivityInfo receiverInfo : receiverInfoList) {
                receiverBeanList.add(new ReceiverBean(context, receiverInfo));
            }
            sortReceiverBeanList(receiverBeanList);
            mReceiverBeanList.postValue(receiverBeanList);
        });
    }

    public void sortPermissionBeanList(List<PermissionBean> permissionBeanList) {
        Context context = getApplication();
        Collator collator = Collator.getInstance(LocaleUtils.getPrimaryLocale(context));
        if (permissionBeanList != null) {
            permissionBeanList.sort((o1, o2) -> {
                String name1 = o1.getName();
                String name2 = o2.getName();
                return collator.compare(name1, name2);
            });
        }
    }

    public void sortActivityBeanList(List<ActivityBean> activityBeanList) {
        Context context = getApplication();
        Collator collator = Collator.getInstance(LocaleUtils.getPrimaryLocale(context));
        if (activityBeanList != null) {
            activityBeanList.sort((o1, o2) -> {
                String name1 = o1.getActivityName();
                String name2 = o2.getActivityName();
                return collator.compare(name1, name2);
            });
        }
    }

    public void sortServiceBeanList(List<ServiceBean> serviceBeanList) {
        Context context = getApplication();
        Collator collator = Collator.getInstance(LocaleUtils.getPrimaryLocale(context));
        if (serviceBeanList != null) {
            serviceBeanList.sort((o1, o2) -> {
                String name1 = o1.getName();
                String name2 = o2.getName();
                return collator.compare(name1, name2);
            });
        }
    }

    public void sortProviderBeanList(List<ProviderBean> providerBeanList) {
        Context context = getApplication();
        Collator collator = Collator.getInstance(LocaleUtils.getPrimaryLocale(context));
        if (providerBeanList != null) {
            providerBeanList.sort((o1, o2) -> {
                String name1 = o1.getName();
                String name2 = o2.getName();
                return collator.compare(name1, name2);
            });
        }
    }

    public void sortReceiverBeanList(List<ReceiverBean> receiverBeanList) {
        Context context = getApplication();
        Collator collator = Collator.getInstance(LocaleUtils.getPrimaryLocale(context));
        if (receiverBeanList != null) {
            receiverBeanList.sort((o1, o2) -> {
                String name1 = o1.getName();
                String name2 = o2.getName();
                return collator.compare(name1, name2);
            });
        }
    }

    public MutableLiveData<List<TCItem>> getOverviewList() {
        return mOverviewList;
    }

    public MutableLiveData<List<PermissionBean>> getDefinedPermissionBeanList() {
        return mDefinedPermissionBeanList;
    }

    public MutableLiveData<List<PermissionBean>> getRequestedPermissionBeanList() {
        return mRequestedPermissionBeanList;
    }

    public List<String> getPermissionNotFoundList() {
        return mPermissionNotFoundList.getValue();
    }

    public MutableLiveData<List<ActivityBean>> getActivityBeanList() {
        return mActivityBeanList;
    }

    public MutableLiveData<List<ServiceBean>> getServiceBeanList() {
        return mServiceBeanList;
    }

    public MutableLiveData<List<ProviderBean>> getProviderBeanList() {
        return mProviderBeanList;
    }

    public MutableLiveData<List<ReceiverBean>> getReceiverBeanList() {
        return mReceiverBeanList;
    }

    public List<TCItem> getPermissionDetailList(PermissionBean permissionBean) {
        Context context = getApplication();
        List<TCItem> list = new ArrayList<>();
        list.add(new TCItem(context.getString(R.string.name), permissionBean.getName()));
        list.add(new TCItem(context.getString(R.string.label), permissionBean.getLabel()));
        list.add(new TCItem(context.getString(R.string.description), permissionBean.getDescription()));
        return list;
    }

    public List<TCItem> getActivityDetailList(ActivityBean activityBean) {
        Context context = getApplication();
        List<TCItem> list = new ArrayList<>();
        list.add(new TCItem(context.getString(R.string.name), activityBean.getActivityName()));
        list.add(new TCItem(context.getString(R.string.label), activityBean.getLabel()));
        list.add(new TCItem(context.getString(R.string.enabled), context.getString(StringUtils.getLocaleJudgementRes(activityBean.getActivityInfo().isEnabled()))));
        list.add(new TCItem(context.getString(R.string.exported), context.getString(StringUtils.getLocaleJudgementRes(activityBean.getActivityInfo().exported))));
        list.add(new TCItem(context.getString(R.string.permission), activityBean.getActivityInfo().permission));
        return list;
    }

    public void showPermissionDetailDialog(@NonNull Activity activity, PermissionBean permissionBean) {
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_tc_detail, null, false);
        RecyclerView recyclerView = view.findViewById(R.id.d_tc_detail_rv);
        TCHListAdapter tchListAdapter = new TCHListAdapter(getPermissionDetailList(permissionBean));
        recyclerView.setAdapter(tchListAdapter);
        new MaterialAlertDialogBuilder(activity)
                .setTitle(permissionBean.getName())
                .setView(view)
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.confirm, (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void showActivityDetailDialog(@NonNull Activity activity, ActivityBean activityBean) {
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_tc_detail, null, false);
        RecyclerView recyclerView = view.findViewById(R.id.d_tc_detail_rv);
        TCHListAdapter tchListAdapter = new TCHListAdapter(getActivityDetailList(activityBean));
        recyclerView.setAdapter(tchListAdapter);
        new MaterialAlertDialogBuilder(activity)
                .setIcon(activityBean.getIcon())
                .setTitle(activityBean.getActivityName())
                .setView(view)
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.launch, (dialog, which) -> {
                    ActivityUtils.LaunchActivityResult result
                            = ActivityUtils.LaunchActivity(activity, activityBean.getActivityInfo());
                    switch (result) {
                        case NOT_EXPORTED:
                            Toast.makeText(activity, R.string.cannot_access_unexported_activity, Toast.LENGTH_SHORT).show();
                            break;
                        case REQUIRE_PERMISSION:
                            Toast.makeText(activity, R.string.activity_requires_extra_permission, Toast.LENGTH_SHORT).show();
                            break;
                        case NOT_FOUND:
                            Toast.makeText(activity, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                })
                .show();
    }
}