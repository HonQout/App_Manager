package com.android.appmanager.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.appmanager.R;
import com.android.appmanager.adapter.recyclerview.ActivityRVAdapter;
import com.android.appmanager.adapter.recyclerview.FilterableListAdapter;
import com.android.appmanager.adapter.recyclerview.PermissionRVAdapter;
import com.android.appmanager.adapter.recyclerview.ProviderRVAdapter;
import com.android.appmanager.adapter.recyclerview.ReceiverRVAdapter;
import com.android.appmanager.adapter.recyclerview.ServiceRVAdapter;
import com.android.appmanager.adapter.recyclerview.TCVListAdapter;
import com.android.appmanager.bean.ActivityBean;
import com.android.appmanager.bean.PermissionBean;
import com.android.appmanager.bean.ProviderBean;
import com.android.appmanager.bean.ReceiverBean;
import com.android.appmanager.bean.ServiceBean;
import com.android.appmanager.constants.Constants;
import com.android.appmanager.databinding.FragmentPackageInfoBinding;
import com.android.appmanager.viewmodel.PackageInfoViewModel;

import java.util.ArrayList;
import java.util.List;

public class PackageInfoFragment extends Fragment {
    private static final String TAG = "PackageInfoFragment";
    private static final String KEY_SCOPE = "SCOPE";
    private static final String IS_EXPANDED = "IS_EXPANDED";
    private Constants.PackageInfoScope packageInfoScope;
    private FragmentPackageInfoBinding binding;
    private PackageInfoViewModel viewModel;

    public PackageInfoFragment() {
        // Required empty public constructor
    }

    public static PackageInfoFragment newInstance(Constants.PackageInfoScope packageInfoScope) {
        PackageInfoFragment fragment = new PackageInfoFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_SCOPE, packageInfoScope.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            int scopeInt = args.getInt(KEY_SCOPE);
            if (scopeInt >= 0 && scopeInt < Constants.PackageInfoScope.values().length) {
                packageInfoScope = Constants.PackageInfoScope.values()[scopeInt];
                Log.i(TAG, "PackageInfoFragment #" + packageInfoScope.ordinal() + " onCreate");
            }
        }
        viewModel = new ViewModelProvider(requireActivity()).get(PackageInfoViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "PackageInfoFragment #" + packageInfoScope.ordinal() + " onCreateView");
        // Get saved instance state
        boolean isExpanded = false;
        if (savedInstanceState != null) {
            isExpanded = savedInstanceState.getBoolean(IS_EXPANDED, false);
        }
        // Initialize view
        binding = FragmentPackageInfoBinding.inflate(inflater, container, false);
        binding.fPackageInfoRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        switch (packageInfoScope) {
            case Overview:
                binding.fPackageInfoFl.setVisibility(View.GONE);
                binding.fPackageInfoRv.setAdapter(new TCVListAdapter(new ArrayList<>()));
                break;
            case PermissionDefined:
            case PermissionRequested:
                PermissionRVAdapter permissionRVAdapter = new PermissionRVAdapter(new ArrayList<>());
                permissionRVAdapter.setOnItemClickListener(new FilterableListAdapter.OnItemClickListener<>() {
                    @Override
                    public void onItemClick(PermissionBean item) {
                        viewModel.showPermissionDetailDialog(requireActivity(), item);
                    }

                    @Override
                    public boolean onItemLongClick(PermissionBean item) {
                        return false;
                    }
                });
                binding.fPackageInfoRv.setAdapter(permissionRVAdapter);
                break;
            case Activity:
                ActivityRVAdapter activityRVAdapter = new ActivityRVAdapter(new ArrayList<>());
                activityRVAdapter.setOnItemClickListener(new FilterableListAdapter.OnItemClickListener<>() {
                    @Override
                    public void onItemClick(ActivityBean item) {
                        viewModel.showActivityDetailDialog(requireActivity(), item);
                    }

                    @Override
                    public boolean onItemLongClick(ActivityBean item) {
                        return false;
                    }
                });
                binding.fPackageInfoRv.setAdapter(activityRVAdapter);
                break;
            case Service:
                ServiceRVAdapter serviceRVAdapter = new ServiceRVAdapter(new ArrayList<>());
                serviceRVAdapter.setOnItemClickListener(new FilterableListAdapter.OnItemClickListener<>() {
                    @Override
                    public void onItemClick(ServiceBean item) {
                        viewModel.showServiceDetailDialog(requireActivity(), item);
                    }

                    @Override
                    public boolean onItemLongClick(ServiceBean item) {
                        return false;
                    }
                });
                binding.fPackageInfoRv.setAdapter(serviceRVAdapter);
                break;
            case Provider:
                ProviderRVAdapter providerRVAdapter = new ProviderRVAdapter(new ArrayList<>());
                providerRVAdapter.setOnItemClickListener(new FilterableListAdapter.OnItemClickListener<>() {
                    @Override
                    public void onItemClick(ProviderBean item) {
                        viewModel.showProviderDetailDialog(requireActivity(), item);
                    }

                    @Override
                    public boolean onItemLongClick(ProviderBean item) {
                        return false;
                    }
                });
                binding.fPackageInfoRv.setAdapter(providerRVAdapter);
                break;
            case Receiver:
                ReceiverRVAdapter receiverRVAdapter = new ReceiverRVAdapter(new ArrayList<>());
                receiverRVAdapter.setOnItemClickListener(new FilterableListAdapter.OnItemClickListener<>() {
                    @Override
                    public void onItemClick(ReceiverBean item) {
                        viewModel.showReceiverDetailDialog(requireActivity(), item);
                    }

                    @Override
                    public boolean onItemLongClick(ReceiverBean item) {
                        return false;
                    }
                });
                binding.fPackageInfoRv.setAdapter(receiverRVAdapter);
                break;
        }
        binding.fPackageInfoTv.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
        binding.fPackageInfoSv.setOnSearchClickListener(v -> binding.fPackageInfoTv.setVisibility(View.GONE));
        binding.fPackageInfoSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                switch (packageInfoScope) {
                    case PermissionDefined:
                    case PermissionRequested:
                        PermissionRVAdapter orvAdapter = (PermissionRVAdapter) binding.fPackageInfoRv.getAdapter();
                        if (orvAdapter != null) {
                            orvAdapter.getFilter().filter(newText);
                        }
                        break;
                    case Activity:
                        ActivityRVAdapter arvAdapter = (ActivityRVAdapter) binding.fPackageInfoRv.getAdapter();
                        if (arvAdapter != null) {
                            arvAdapter.getFilter().filter(newText);
                        }
                        break;
                    case Receiver:
                        ReceiverRVAdapter rrvAdapter = (ReceiverRVAdapter) binding.fPackageInfoRv.getAdapter();
                        if (rrvAdapter != null) {
                            rrvAdapter.getFilter().filter(newText);
                        }
                    default:
                        break;
                }
                return true;
            }
        });
        binding.fPackageInfoSv.setOnCloseListener(() -> {
            binding.fPackageInfoTv.setVisibility(View.VISIBLE);
            return false;
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "PackageInfoFragment #" + packageInfoScope.ordinal() + " onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        switch (packageInfoScope) {
            case Overview:
                TCVListAdapter tclAdapter = (TCVListAdapter) binding.fPackageInfoRv.getAdapter();
                if (tclAdapter != null) {
                    viewModel.getOverviewList().observe(getViewLifecycleOwner(), tclAdapter::setOverviewList);
                }
                break;
            case PermissionDefined:
                PermissionRVAdapter prvAdapter1 = (PermissionRVAdapter) binding.fPackageInfoRv.getAdapter();
                if (prvAdapter1 != null) {
                    List<PermissionBean> curList = viewModel.getDefinedPermissionBeanList().getValue();
                    if (curList != null) {
                        prvAdapter1.setList(curList);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, curList.size()));
                    }
                    viewModel.getDefinedPermissionBeanList().observe(getViewLifecycleOwner(), list -> {
                        prvAdapter1.setList(list);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, list.size()));
                    });
                }
                break;
            case PermissionRequested:
                PermissionRVAdapter prvAdapter2 = (PermissionRVAdapter) binding.fPackageInfoRv.getAdapter();
                if (prvAdapter2 != null) {
                    List<PermissionBean> curList = viewModel.getRequestedPermissionBeanList().getValue();
                    if (curList != null) {
                        prvAdapter2.setList(curList);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, curList.size()));
                    }
                    viewModel.getRequestedPermissionBeanList().observe(getViewLifecycleOwner(), list -> {
                        prvAdapter2.setList(list);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, list.size()));
                    });
                }
                break;
            case Activity:
                ActivityRVAdapter arvAdapter = (ActivityRVAdapter) binding.fPackageInfoRv.getAdapter();
                if (arvAdapter != null) {
                    List<ActivityBean> curList = viewModel.getActivityBeanList().getValue();
                    if (curList != null) {
                        arvAdapter.setList(curList);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, curList.size()));
                    }
                    viewModel.getActivityBeanList().observe(getViewLifecycleOwner(), list -> {
                        arvAdapter.setList(list);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, list.size()));
                    });
                }
                break;
            case Service:
                ServiceRVAdapter srvAdapter = (ServiceRVAdapter) binding.fPackageInfoRv.getAdapter();
                if (srvAdapter != null) {
                    List<ServiceBean> curList = viewModel.getServiceBeanList().getValue();
                    if (curList != null) {
                        srvAdapter.setList(curList);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, curList.size()));
                    }
                    viewModel.getServiceBeanList().observe(getViewLifecycleOwner(), list -> {
                        srvAdapter.setList(list);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, list.size()));
                    });
                }
                break;
            case Provider:
                ProviderRVAdapter prvAdapter3 = (ProviderRVAdapter) binding.fPackageInfoRv.getAdapter();
                if (prvAdapter3 != null) {
                    List<ProviderBean> curList = viewModel.getProviderBeanList().getValue();
                    if (curList != null) {
                        prvAdapter3.setList(curList);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, curList.size()));
                    }
                    viewModel.getProviderBeanList().observe(getViewLifecycleOwner(), list -> {
                        prvAdapter3.setList(list);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, list.size()));
                    });
                }
                break;
            case Receiver:
                ReceiverRVAdapter rrvAdapter = (ReceiverRVAdapter) binding.fPackageInfoRv.getAdapter();
                if (rrvAdapter != null) {
                    List<ReceiverBean> curList = viewModel.getReceiverBeanList().getValue();
                    if (curList != null) {
                        rrvAdapter.setList(curList);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, curList.size()));
                    }
                    viewModel.getReceiverBeanList().observe(getViewLifecycleOwner(), list -> {
                        rrvAdapter.setList(list);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, list.size()));
                    });
                }
                break;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_EXPANDED, !binding.fPackageInfoSv.isIconified());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}