package com.android.appmanager.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.appmanager.R;
import com.android.appmanager.ui.packageinfo.adapter.AppListAdapter;
import com.android.appmanager.ui.packageinfo.activity.PackageInfoActivity;
import com.android.appmanager.data.model.PackageModel;
import com.android.appmanager.constants.Constants;
import com.android.appmanager.databinding.FragmentAppListBinding;
import com.android.appmanager.ui.recyclerview.adapter.OnItemClickListener;
import com.android.appmanager.viewmodel.AppViewModel;

import java.util.ArrayList;
import java.util.List;

public class AppListFragment extends Fragment {
    private static final String TAG = "AppListFragment";
    private static final String ARG_SCOPE = "SCOPE";
    private static final String IS_EXPANDED = "IS_EXPANDED";
    private Constants.AppScope appScope;
    private FragmentAppListBinding binding;
    private AppViewModel viewModel;

    public AppListFragment() {
        // Required empty public constructor
    }

    public static AppListFragment newInstance(Constants.AppScope appScope) {
        AppListFragment fragment = new AppListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SCOPE, appScope.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            int scopeInt = args.getInt(ARG_SCOPE);
            if (scopeInt >= 0 && scopeInt < Constants.AppScope.values().length) {
                appScope = Constants.AppScope.values()[scopeInt];
                Log.i(TAG, "AppsFragment #" + appScope.ordinal() + " onCreate");
            }
        }
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "AppsFragment #" + appScope.ordinal() + " onCreateView");
        // Get saved instance state
        boolean isExpanded = false;
        if (savedInstanceState != null) {
            isExpanded = savedInstanceState.getBoolean(IS_EXPANDED, false);
        }
        // Initialize view
        binding = FragmentAppListBinding.inflate(inflater, container, false);
        binding.fAllAppsRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        AppListAdapter appListAdapter = new AppListAdapter(new ArrayList<>());
        appListAdapter.setOnItemClickListener(new OnItemClickListener<>() {
            @Override
            public void onItemClick(PackageModel item, View view) {
                if (item != null) {
                    Intent intent = new Intent(requireActivity(), PackageInfoActivity.class);
                    intent.putExtra("packageInfo", item.getPackageInfo());
                    startActivity(intent);
                } else {
                    Toast.makeText(requireContext(), R.string.cannot_find_package, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public boolean onItemLongClick(PackageModel item, View view) {
                return false;
            }
        });
        binding.fAllAppsRv.setAdapter(appListAdapter);
        binding.fAllAppsTv.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
        binding.fAllAppsSv.setOnSearchClickListener(v -> binding.fAllAppsTv.setVisibility(View.GONE));
        binding.fAllAppsSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                AppListAdapter appListAdapter = (AppListAdapter) binding.fAllAppsRv.getAdapter();
                if (appListAdapter != null) {
                    appListAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });
        binding.fAllAppsSv.setOnCloseListener(() -> {
            binding.fAllAppsTv.setVisibility(View.VISIBLE);
            return false;
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "AppsFragment #" + appScope.ordinal() + " onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        AppListAdapter appListAdapter = (AppListAdapter) binding.fAllAppsRv.getAdapter();
        if (appListAdapter != null) {
            switch (appScope) {
                case All:
                    List<PackageModel> apbList = viewModel.getAllPackageBeanList().getValue();
                    if (apbList != null) {
                        appListAdapter.setList(apbList);
                        binding.fAllAppsTv.setText(getString(R.string.num_items, apbList.size()));
                    }
                    viewModel.getAllPackageBeanList().observe(getViewLifecycleOwner(), list -> {
                        Log.i(TAG, "AllAppRVAdapter observed list change. Size of new list: " + list.size());
                        appListAdapter.setList(list);
                        binding.fAllAppsTv.setText(getString(R.string.num_items, list.size()));
                    });
                    break;
                case System:
                    List<PackageModel> spbList = viewModel.getSystemPackageBeanList().getValue();
                    if (spbList != null) {
                        appListAdapter.setList(spbList);
                        binding.fAllAppsTv.setText(getString(R.string.num_items, spbList.size()));
                    }
                    viewModel.getSystemPackageBeanList().observe(getViewLifecycleOwner(), list -> {
                        appListAdapter.setList(list);
                        binding.fAllAppsTv.setText(getString(R.string.num_items, list.size()));
                    });
                    break;
                case User:
                    List<PackageModel> upbList = viewModel.getUserPackageBeanList().getValue();
                    if (upbList != null) {
                        appListAdapter.setList(upbList);
                        binding.fAllAppsTv.setText(getString(R.string.num_items, upbList.size()));
                    }
                    viewModel.getUserPackageBeanList().observe(getViewLifecycleOwner(), list -> {
                        appListAdapter.setList(list);
                        binding.fAllAppsTv.setText(getString(R.string.num_items, list.size()));
                    });
                    break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_EXPANDED, !binding.fAllAppsSv.isIconified());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}