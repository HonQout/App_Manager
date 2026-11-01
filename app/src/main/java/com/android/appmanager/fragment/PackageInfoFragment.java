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
import com.android.appmanager.ui.packageinfo.adapter.ReceiverListAdapter;
import com.android.appmanager.ui.packageinfo.adapter.ProviderListAdapter;
import com.android.appmanager.ui.packageinfo.adapter.PermissionListAdapter;
import com.android.appmanager.ui.packageinfo.adapter.ActivityListAdapter;
import com.android.appmanager.ui.packageinfo.adapter.ServiceListAdapter;
import com.android.appmanager.adapter.recyclerview.TCVListAdapter;
import com.android.appmanager.data.model.ActivityModel;
import com.android.appmanager.data.model.PermissionModel;
import com.android.appmanager.data.model.ProviderModel;
import com.android.appmanager.data.model.ReceiverModel;
import com.android.appmanager.data.model.ServiceModel;
import com.android.appmanager.constants.Constants;
import com.android.appmanager.databinding.FragmentPackageInfoBinding;
import com.android.appmanager.ui.recyclerview.adapter.OnItemClickListener;
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
            case Overview: {
                binding.fPackageInfoFl.setVisibility(View.GONE);
                binding.fPackageInfoRv.setAdapter(new TCVListAdapter(new ArrayList<>()));
                break;
            }
            case PermissionDefined:
            case PermissionRequested: {
                PermissionListAdapter adapter = new PermissionListAdapter(new ArrayList<>());
                adapter.setOnItemClickListener(new OnItemClickListener<>() {
                    @Override
                    public void onItemClick(PermissionModel item, View view) {
                        viewModel.showPermissionDetailDialog(requireActivity(), item);
                    }

                    @Override
                    public boolean onItemLongClick(PermissionModel item, View view) {
                        return false;
                    }
                });
                binding.fPackageInfoRv.setAdapter(adapter);
                break;
            }
            case Activity: {
                ActivityListAdapter adapter = new ActivityListAdapter(new ArrayList<>());
                adapter.setOnItemClickListener(new OnItemClickListener<>() {
                    @Override
                    public void onItemClick(ActivityModel item, View view) {
                        viewModel.showActivityDetailDialog(requireActivity(), item);
                    }

                    @Override
                    public boolean onItemLongClick(ActivityModel item, View view) {
                        return false;
                    }
                });
                binding.fPackageInfoRv.setAdapter(adapter);
                break;
            }
            case Service: {
                ServiceListAdapter adapter = new ServiceListAdapter(new ArrayList<>());
                adapter.setOnItemClickListener(new OnItemClickListener<>() {
                    @Override
                    public void onItemClick(ServiceModel item, View view) {
                        viewModel.showServiceDetailDialog(requireActivity(), item);
                    }

                    @Override
                    public boolean onItemLongClick(ServiceModel item, View view) {
                        return false;
                    }
                });
                binding.fPackageInfoRv.setAdapter(adapter);
                break;
            }
            case Provider: {
                ProviderListAdapter adapter = new ProviderListAdapter(new ArrayList<>());
                adapter.setOnItemClickListener(new OnItemClickListener<>() {
                    @Override
                    public void onItemClick(ProviderModel item, View view) {
                        viewModel.showProviderDetailDialog(requireActivity(), item);
                    }

                    @Override
                    public boolean onItemLongClick(ProviderModel item, View view) {
                        return false;
                    }
                });
                binding.fPackageInfoRv.setAdapter(adapter);
                break;
            }
            case Receiver: {
                ReceiverListAdapter adapter = new ReceiverListAdapter(new ArrayList<>());
                adapter.setOnItemClickListener(new OnItemClickListener<>() {
                    @Override
                    public void onItemClick(ReceiverModel item, View view) {
                        viewModel.showReceiverDetailDialog(requireActivity(), item);
                    }

                    @Override
                    public boolean onItemLongClick(ReceiverModel item, View view) {
                        return false;
                    }
                });
                binding.fPackageInfoRv.setAdapter(adapter);
                break;
            }
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
                        PermissionListAdapter orvAdapter = (PermissionListAdapter) binding.fPackageInfoRv.getAdapter();
                        if (orvAdapter != null) {
                            orvAdapter.getFilter().filter(newText);
                        }
                        break;
                    case Activity:
                        ActivityListAdapter arvAdapter = (ActivityListAdapter) binding.fPackageInfoRv.getAdapter();
                        if (arvAdapter != null) {
                            arvAdapter.getFilter().filter(newText);
                        }
                        break;
                    case Receiver:
                        ReceiverListAdapter rrvAdapter = (ReceiverListAdapter) binding.fPackageInfoRv.getAdapter();
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
            case Overview: {
                TCVListAdapter adapter = (TCVListAdapter) binding.fPackageInfoRv.getAdapter();
                if (adapter != null) {
                    viewModel.getOverviewList().observe(getViewLifecycleOwner(), adapter::setOverviewList);
                }
                break;
            }
            case PermissionDefined: {
                PermissionListAdapter adapter = (PermissionListAdapter) binding.fPackageInfoRv.getAdapter();
                if (adapter != null) {
                    List<PermissionModel> curList = viewModel.getDefinedPermissionBeanList().getValue();
                    if (curList != null) {
                        adapter.setList(curList);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, curList.size()));
                    }
                    viewModel.getDefinedPermissionBeanList().observe(getViewLifecycleOwner(), list -> {
                        adapter.setList(list);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, list.size()));
                    });
                }
                break;
            }
            case PermissionRequested: {
                PermissionListAdapter adapter = (PermissionListAdapter) binding.fPackageInfoRv.getAdapter();
                if (adapter != null) {
                    List<PermissionModel> curList = viewModel.getRequestedPermissionBeanList().getValue();
                    if (curList != null) {
                        adapter.setList(curList);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, curList.size()));
                    }
                    viewModel.getRequestedPermissionBeanList().observe(getViewLifecycleOwner(), list -> {
                        adapter.setList(list);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, list.size()));
                    });
                }
                break;
            }
            case Activity: {
                ActivityListAdapter adapter = (ActivityListAdapter) binding.fPackageInfoRv.getAdapter();
                if (adapter != null) {
                    List<ActivityModel> curList = viewModel.getActivityBeanList().getValue();
                    if (curList != null) {
                        adapter.setList(curList);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, curList.size()));
                    }
                    viewModel.getActivityBeanList().observe(getViewLifecycleOwner(), list -> {
                        adapter.setList(list);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, list.size()));
                    });
                }
                break;
            }
            case Service: {
                ServiceListAdapter adapter = (ServiceListAdapter) binding.fPackageInfoRv.getAdapter();
                if (adapter != null) {
                    List<ServiceModel> curList = viewModel.getServiceBeanList().getValue();
                    if (curList != null) {
                        adapter.setList(curList);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, curList.size()));
                    }
                    viewModel.getServiceBeanList().observe(getViewLifecycleOwner(), list -> {
                        adapter.setList(list);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, list.size()));
                    });
                }
                break;
            }
            case Provider: {
                ProviderListAdapter adapter = (ProviderListAdapter) binding.fPackageInfoRv.getAdapter();
                if (adapter != null) {
                    List<ProviderModel> curList = viewModel.getProviderBeanList().getValue();
                    if (curList != null) {
                        adapter.setList(curList);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, curList.size()));
                    }
                    viewModel.getProviderBeanList().observe(getViewLifecycleOwner(), list -> {
                        adapter.setList(list);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, list.size()));
                    });
                }
                break;
            }
            case Receiver: {
                ReceiverListAdapter adapter = (ReceiverListAdapter) binding.fPackageInfoRv.getAdapter();
                if (adapter != null) {
                    List<ReceiverModel> curList = viewModel.getReceiverBeanList().getValue();
                    if (curList != null) {
                        adapter.setList(curList);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, curList.size()));
                    }
                    viewModel.getReceiverBeanList().observe(getViewLifecycleOwner(), list -> {
                        adapter.setList(list);
                        binding.fPackageInfoTv.setText(getString(R.string.num_items, list.size()));
                    });
                }
                break;
            }
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