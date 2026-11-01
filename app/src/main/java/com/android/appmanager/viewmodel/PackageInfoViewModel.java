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
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.android.appmanager.R;
import com.android.appmanager.adapter.recyclerview.TCHListAdapter;
import com.android.appmanager.adapter.recyclerview.TCVListAdapter;
import com.android.appmanager.data.model.ActivityModel;
import com.android.appmanager.data.model.PackageModel;
import com.android.appmanager.data.model.PermissionModel;
import com.android.appmanager.data.model.ProviderModel;
import com.android.appmanager.data.model.ReceiverModel;
import com.android.appmanager.data.model.ServiceModel;
import com.android.appmanager.data.model.item.TCItem;
import com.android.appmanager.data.model.permission.PermissionSorted;
import com.android.appmanager.utils.ActivityUtils;
import com.android.appmanager.utils.PackageUtils;
import com.android.appmanager.utils.StringUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PackageInfoViewModel extends AndroidViewModel {
    private final String TAG = "PackageInfoViewModel";
    public final int[] tabTitleResources = new int[]{R.string.overview, R.string.permission_defined,
            R.string.permission_requested, R.string.activity, R.string.service,
            R.string.content_provider, R.string.broadcast_receiver};
    private final MutableLiveData<PackageInfo> mPackageInfo = new MutableLiveData<>();
    private final MutableLiveData<List<TCItem>> mOverviewList = new MutableLiveData<>();
    private final MutableLiveData<List<PermissionModel>> mDefinedPermissionBeanList = new MutableLiveData<>();
    private final MutableLiveData<List<PermissionModel>> mRequestedPermissionBeanList = new MutableLiveData<>();
    private final MutableLiveData<List<String>> mPermissionNotFoundList = new MutableLiveData<>();
    private final MutableLiveData<List<ActivityModel>> mActivityBeanList = new MutableLiveData<>();
    private final MutableLiveData<List<ServiceModel>> mServiceBeanList = new MutableLiveData<>();
    private final MutableLiveData<List<ProviderModel>> mProviderBeanList = new MutableLiveData<>();
    private final MutableLiveData<List<ReceiverModel>> mReceiverBeanList = new MutableLiveData<>();
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
            PackageModel packageModel = new PackageModel(context, packageInfo);
            String packageName = packageInfo.packageName;
            // Overview
            List<TCItem> overviewList = new ArrayList<>();
            overviewList.add(new TCItem(context.getString(R.string.name), packageModel.getLabel()));
            overviewList.add(new TCItem(context.getString(R.string.package_name), packageName));
            overviewList.add(new TCItem(context.getString(R.string.version), packageModel.getCombinedVersion()));
            overviewList.add(new TCItem(context.getString(R.string.target_sdk_version), packageModel.getTargetSdkVersion()));
            overviewList.add(new TCItem(context.getString(R.string.min_sdk_version), packageModel.getMinSdkVersion()));
            overviewList.add(new TCItem(context.getString(R.string.compile_sdk_version), packageModel.getCompileSdkVersion()));
            overviewList.add(new TCItem(context.getString(R.string.shared_user_id), packageModel.getSharedUserId()));
            overviewList.add(new TCItem(context.getString(R.string.shared_user_label), packageModel.getSharedUserLabel()));
            overviewList.add(new TCItem(context.getString(R.string.uid), packageModel.getUid()));
            overviewList.add(new TCItem(context.getString(R.string.system_app), StringUtils.getLocaleJudgement(context, packageModel.isSystemApp())));
            overviewList.add(new TCItem(context.getString(R.string.allow_clear_user_data), StringUtils.getLocaleJudgement(context, packageModel.doesAllowClearUserData())));
            overviewList.add(new TCItem(context.getString(R.string.allow_backup), StringUtils.getLocaleJudgement(context, packageModel.doesAllowBackup())));
            overviewList.add(new TCItem(context.getString(R.string.enabled), StringUtils.getLocaleJudgement(context, packageModel.isEnabled())));
            overviewList.add(new TCItem(context.getString(R.string.first_install_time), packageModel.getFirstInstallTime()));
            overviewList.add(new TCItem(context.getString(R.string.last_update_time), packageModel.getLastUpdateTime()));
            overviewList.add(new TCItem(context.getString(R.string.install_source), packageModel.getInstallSource(context)));
            mOverviewList.postValue(overviewList);
            // Others
            PermissionSorted permissionSorted = PackageUtils.getPermissionSorted(context, packageName);
            List<PermissionModel> definedPermissionModelList = new ArrayList<>();
            for (PermissionInfo permissionInfo : permissionSorted.permissionDefinedList) {
                definedPermissionModelList.add(new PermissionModel(context, permissionInfo));
            }
            sortPermissionBeanList(definedPermissionModelList);
            mDefinedPermissionBeanList.postValue(definedPermissionModelList);
            List<PermissionModel> requestedPermissionModelList = new ArrayList<>();
            for (PermissionInfo permissionInfo : permissionSorted.permissionRequestedList) {
                requestedPermissionModelList.add(new PermissionModel(context, permissionInfo));
            }
            sortPermissionBeanList(requestedPermissionModelList);
            mRequestedPermissionBeanList.postValue(requestedPermissionModelList);
            mPermissionNotFoundList.postValue(permissionSorted.permissionNotFoundList);
            List<ActivityInfo> activityInfoList = PackageUtils.getActivityInfoList(context, packageName);
            List<ActivityModel> activityModelList = new ArrayList<>();
            for (ActivityInfo activityInfo : activityInfoList) {
                activityModelList.add(new ActivityModel(context, activityInfo));
            }
            sortActivityBeanList(activityModelList);
            mActivityBeanList.postValue(activityModelList);
            List<ServiceInfo> serviceInfoList = PackageUtils.getServiceInfoList(context, packageName);
            List<ServiceModel> serviceModelList = new ArrayList<>();
            for (ServiceInfo serviceInfo : serviceInfoList) {
                serviceModelList.add(new ServiceModel(context, serviceInfo));
            }
            sortServiceBeanList(serviceModelList);
            mServiceBeanList.postValue(serviceModelList);
            List<ProviderInfo> providerInfoList = PackageUtils.getProviderInfoList(context, packageName);
            List<ProviderModel> providerModelList = new ArrayList<>();
            for (ProviderInfo providerInfo : providerInfoList) {
                providerModelList.add(new ProviderModel(context, providerInfo));
            }
            sortProviderBeanList(providerModelList);
            mProviderBeanList.postValue(providerModelList);
            List<ActivityInfo> receiverInfoList = PackageUtils.getReceiverInfoList(context, packageName);
            List<ReceiverModel> receiverModelList = new ArrayList<>();
            for (ActivityInfo receiverInfo : receiverInfoList) {
                receiverModelList.add(new ReceiverModel(context, receiverInfo));
            }
            sortReceiverBeanList(receiverModelList);
            mReceiverBeanList.postValue(receiverModelList);
        });
    }

    public void sortPermissionBeanList(List<PermissionModel> permissionModelList) {
        Collator collator = Collator.getInstance(Locale.getDefault());
        if (permissionModelList != null) {
            permissionModelList.sort((o1, o2) -> {
                String name1 = o1.getName();
                String name2 = o2.getName();
                return collator.compare(name1, name2);
            });
        }
    }

    public void sortActivityBeanList(List<ActivityModel> activityModelList) {
        Collator collator = Collator.getInstance(Locale.getDefault());
        if (activityModelList != null) {
            activityModelList.sort((o1, o2) -> {
                String name1 = o1.getActivityName();
                String name2 = o2.getActivityName();
                return collator.compare(name1, name2);
            });
        }
    }

    public void sortServiceBeanList(List<ServiceModel> serviceModelList) {
        Collator collator = Collator.getInstance(Locale.getDefault());
        if (serviceModelList != null) {
            serviceModelList.sort((o1, o2) -> {
                String name1 = o1.getName();
                String name2 = o2.getName();
                return collator.compare(name1, name2);
            });
        }
    }

    public void sortProviderBeanList(List<ProviderModel> providerModelList) {
        Collator collator = Collator.getInstance(Locale.getDefault());
        if (providerModelList != null) {
            providerModelList.sort((o1, o2) -> {
                String name1 = o1.getName();
                String name2 = o2.getName();
                return collator.compare(name1, name2);
            });
        }
    }

    public void sortReceiverBeanList(List<ReceiverModel> receiverModelList) {
        Collator collator = Collator.getInstance(Locale.getDefault());
        if (receiverModelList != null) {
            receiverModelList.sort((o1, o2) -> {
                String name1 = o1.getName();
                String name2 = o2.getName();
                return collator.compare(name1, name2);
            });
        }
    }

    public LiveData<PackageInfo> getPackageInfo() {
        return mPackageInfo;
    }

    public LiveData<List<TCItem>> getOverviewList() {
        return mOverviewList;
    }

    public LiveData<List<PermissionModel>> getDefinedPermissionBeanList() {
        return mDefinedPermissionBeanList;
    }

    public LiveData<List<PermissionModel>> getRequestedPermissionBeanList() {
        return mRequestedPermissionBeanList;
    }

    public LiveData<List<String>> getPermissionNotFoundList() {
        return mPermissionNotFoundList;
    }

    public LiveData<List<ActivityModel>> getActivityBeanList() {
        return mActivityBeanList;
    }

    public LiveData<List<ServiceModel>> getServiceBeanList() {
        return mServiceBeanList;
    }

    public LiveData<List<ProviderModel>> getProviderBeanList() {
        return mProviderBeanList;
    }

    public LiveData<List<ReceiverModel>> getReceiverBeanList() {
        return mReceiverBeanList;
    }

    public List<TCItem> getPermissionDetailList(PermissionModel permissionModel) {
        Context context = getApplication();
        List<TCItem> list = new ArrayList<>();
        list.add(new TCItem(context.getString(R.string.name), permissionModel.getName()));
        list.add(new TCItem(context.getString(R.string.label), permissionModel.getLabel()));
        list.add(new TCItem(context.getString(R.string.permission_group), permissionModel.getGroup()));
        list.add(new TCItem(context.getString(R.string.protection_level), String.valueOf(permissionModel.getProtectionFlags())));
        list.add(new TCItem(context.getString(R.string.description), permissionModel.getDescription()));
        return list;
    }

    public List<TCItem> getActivityDetailList(ActivityModel activityModel) {
        Context context = getApplication();
        List<TCItem> list = new ArrayList<>();
        list.add(new TCItem(context.getString(R.string.name), activityModel.getActivityName()));
        list.add(new TCItem(context.getString(R.string.label), activityModel.getLabel()));
        list.add(new TCItem(context.getString(R.string.theme), activityModel.getThemeName(context)));
        list.add(new TCItem(context.getString(R.string.launch_mode), activityModel.getLocalizedLaunchMode(context)));
        list.add(new TCItem(context.getString(R.string.max_recents), String.valueOf(activityModel.getMaxRecents())));
        list.add(new TCItem(context.getString(R.string.permission), activityModel.getPermission()));
        list.add(new TCItem(context.getString(R.string.task_affinity), activityModel.getTaskAffinity()));
        list.add(new TCItem(context.getString(R.string.target_activity), activityModel.getTargetActivity()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            list.add(new TCItem(context.getString(R.string.required_display_category), activityModel.getRequiredDisplayCategory()));
        }
        list.add(new TCItem(context.getString(R.string.parent_activity_name), activityModel.getParentActivityName()));
        list.add(new TCItem(context.getString(R.string.enabled), context.getString(StringUtils.getLocaleJudgementRes(activityModel.isEnabled()))));
        list.add(new TCItem(context.getString(R.string.exported), context.getString(StringUtils.getLocaleJudgementRes(activityModel.isExported()))));
        return list;
    }

    public List<TCItem> getServiceDetailList(ServiceModel serviceModel) {
        Context context = getApplication();
        List<TCItem> list = new ArrayList<>();
        list.add(new TCItem(context.getString(R.string.name), serviceModel.getName()));
        list.add(new TCItem(context.getString(R.string.label), serviceModel.getLabel()));
        list.add(new TCItem(context.getString(R.string.permission), serviceModel.getPermission()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            list.add(new TCItem(context.getString(R.string.type), serviceModel.getLocalizedForegroundServiceType(context)));
        }
        list.add(new TCItem(context.getString(R.string.stop_with_task), StringUtils.getLocaleJudgement(context, serviceModel.doesStopWithTask())));
        list.add(new TCItem(context.getString(R.string.isolated_process), StringUtils.getLocaleJudgement(context, serviceModel.isIsolatedProcess())));
        list.add(new TCItem(context.getString(R.string.external_service), StringUtils.getLocaleJudgement(context, serviceModel.isExternalService())));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            list.add(new TCItem(context.getString(R.string.use_app_zygote), StringUtils.getLocaleJudgement(context, serviceModel.doesUseAppZygote())));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            list.add(new TCItem(context.getString(R.string.allow_shared_isolated_process), StringUtils.getLocaleJudgement(context, serviceModel.doesAllowSharedIsolatedProcess())));
        }
        list.add(new TCItem(context.getString(R.string.single_user), StringUtils.getLocaleJudgement(context, serviceModel.isSingleUser())));
        return list;
    }

    public List<TCItem> getProviderDetailList(ProviderModel providerModel) {
        Context context = getApplication();
        List<TCItem> list = new ArrayList<>();
        list.add(new TCItem(context.getString(R.string.name), providerModel.getName()));
        list.add(new TCItem(context.getString(R.string.label), providerModel.getLabel()));
        list.add(new TCItem(context.getString(R.string.provider_authority), providerModel.getAuthority()));
        list.add(new TCItem(context.getString(R.string.read_permission), providerModel.getReadPermission()));
        list.add(new TCItem(context.getString(R.string.write_permission), providerModel.getWritePermission()));
        list.add(new TCItem(context.getString(R.string.grant_uri_permissions), StringUtils.getLocaleJudgement(context, providerModel.isGrantUriPermissions())));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            list.add(new TCItem(context.getString(R.string.force_uri_permissions), StringUtils.getLocaleJudgement(context, providerModel.isForceUriPermissions())));
        }
        list.add(new TCItem(context.getString(R.string.multiprocess), StringUtils.getLocaleJudgement(context, providerModel.isMultiProcess())));
        list.add(new TCItem(context.getString(R.string.init_order), String.valueOf(providerModel.getInitOrder())));
        return list;
    }

    public List<TCItem> getReceiverDetailList(ReceiverModel receiverModel) {
        Context context = getApplication();
        List<TCItem> list = new ArrayList<>();
        list.add(new TCItem(context.getString(R.string.name), receiverModel.getName()));
        list.add(new TCItem(context.getString(R.string.label), receiverModel.getLabel()));
        list.add(new TCItem(context.getString(R.string.process_name), receiverModel.getProcessName()));
        list.add(new TCItem(context.getString(R.string.permission), receiverModel.getPermission()));
        list.add(new TCItem(context.getString(R.string.enabled), StringUtils.getLocaleJudgement(context, receiverModel.isEnabled())));
        list.add(new TCItem(context.getString(R.string.exported), StringUtils.getLocaleJudgement(context, receiverModel.isExported())));
        return list;
    }

    public void showPermissionDetailDialog(@NonNull Activity activity, PermissionModel permissionModel) {
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_tc_detail, null, false);
        RecyclerView recyclerView = view.findViewById(R.id.d_tc_detail_rv);
        TCVListAdapter tcvListAdapter = new TCVListAdapter(getPermissionDetailList(permissionModel));
        recyclerView.setAdapter(tcvListAdapter);
        new MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.detail)
                .setView(view)
                .setPositiveButton(R.string.confirm, (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void showActivityDetailDialog(@NonNull Activity activity, ActivityModel activityModel) {
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_tc_detail, null, false);
        RecyclerView recyclerView = view.findViewById(R.id.d_tc_detail_rv);
        TCHListAdapter tchListAdapter = new TCHListAdapter(getActivityDetailList(activityModel));
        recyclerView.setAdapter(tchListAdapter);
        new MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.detail)
                .setView(view)
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.launch, (dialog, which) -> {
                    ActivityUtils.LaunchActivityResult result
                            = ActivityUtils.LaunchActivity(activity, activityModel);
                    switch (result) {
                        case NOT_EXPORTED: {
                            Toast.makeText(activity, R.string.cannot_access_unexported_activity, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case REQUIRE_PERMISSION: {
                            Toast.makeText(activity, R.string.activity_requires_extra_permission, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case NOT_FOUND: {
                            Toast.makeText(activity, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        default:
                            break;
                    }
                })
                .show();
    }

    public void showServiceDetailDialog(@NonNull Activity activity, ServiceModel serviceModel) {
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_tc_detail, null, false);
        RecyclerView recyclerView = view.findViewById(R.id.d_tc_detail_rv);
        TCVListAdapter tcvListAdapter = new TCVListAdapter(getServiceDetailList(serviceModel));
        recyclerView.setAdapter(tcvListAdapter);
        new MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.detail)
                .setView(view)
                .setPositiveButton(R.string.confirm, (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void showProviderDetailDialog(@NonNull Activity activity, ProviderModel providerModel) {
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_tc_detail, null, false);
        RecyclerView recyclerView = view.findViewById(R.id.d_tc_detail_rv);
        TCVListAdapter tcvListAdapter = new TCVListAdapter(getProviderDetailList(providerModel));
        recyclerView.setAdapter(tcvListAdapter);
        new MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.detail)
                .setView(view)
                .setPositiveButton(R.string.confirm, (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void showReceiverDetailDialog(@NonNull Activity activity, ReceiverModel receiverModel) {
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_tc_detail, null, false);
        RecyclerView recyclerView = view.findViewById(R.id.d_tc_detail_rv);
        TCVListAdapter tcvListAdapter = new TCVListAdapter(getReceiverDetailList(receiverModel));
        recyclerView.setAdapter(tcvListAdapter);
        new MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.detail)
                .setView(view)
                .setPositiveButton(R.string.confirm, (dialog, which) -> dialog.dismiss())
                .show();
    }
}