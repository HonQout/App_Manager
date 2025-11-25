package com.android.appmanager.activity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;

import com.android.appmanager.R;
import com.android.appmanager.adapter.viewpager2.AppListPagerAdapter;
import com.android.appmanager.utils.AppUtils;
import com.android.appmanager.utils.PermissionUtils;
import com.android.appmanager.viewmodel.AppListViewModel;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.android.appmanager.databinding.ActivityMainBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private AppListViewModel viewModel;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Immersive system bars
        EdgeToEdge.enable(this);
        // Set view
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Initialize Toolbar
        setSupportActionBar(binding.toolbar);
        // Initialize ViewPager2
        binding.aMainViewpager2.setAdapter(new AppListPagerAdapter(this));
        binding.navView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_all_apps) {
                binding.aMainViewpager2.setCurrentItem(0);
            } else if (id == R.id.navigation_system_apps) {
                binding.aMainViewpager2.setCurrentItem(1);
            } else if (id == R.id.navigation_user_apps) {
                binding.aMainViewpager2.setCurrentItem(2);
            } else {
                return false;
            }
            return true;
        });
        binding.aMainViewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int menuId = R.id.navigation_all_apps;
                if (position == 1) {
                    menuId = R.id.navigation_system_apps;
                } else if (position == 2) {
                    menuId = R.id.navigation_user_apps;
                }
                binding.navView.getMenu().findItem(menuId).setChecked(true);
            }
        });
        binding.aMainViewpager2.setUserInputEnabled(false);
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AppListViewModel.class);
        // Register permission callback
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                viewModel.initPackageBean();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionUtils.checkQueryAllPackagesPermission(this)) {
            viewModel.initPackageBean();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.QUERY_ALL_PACKAGES)) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.request_permission)
                    .setMessage(R.string.reason_permission_query_all_packages)
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    })
                    .setPositiveButton(R.string.settings, (dialog, which) ->
                            AppUtils.launchAppDetailsSettings(MainActivity.this, "com.android.appmanager"))
                    .show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                requestPermissionLauncher.launch(Manifest.permission.QUERY_ALL_PACKAGES);
            }
        }
    }
}