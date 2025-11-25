package com.android.appmanager.activity;

import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.android.appmanager.R;
import com.android.appmanager.adapter.viewpager2.AppInfoPagerAdapter;
import com.android.appmanager.databinding.ActivityAppInfoBinding;
import com.android.appmanager.viewmodel.PackageInfoViewModel;
import com.android.appmanager.viewmodel.PackageInfoViewModelFactory;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AppInfo extends AppCompatActivity {
    private static final String TAG = "AppInfo";
    private ActivityAppInfoBinding binding;
    private PackageInfoViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Immersive system bars
        EdgeToEdge.enable(this);
        // Set content view
        binding = ActivityAppInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Set Toolbar
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        binding.toolbar.setTitle(R.string.app_detail);
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        // Get intent extra
        PackageInfo packageInfo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageInfo = (PackageInfo) getIntent().getParcelableExtra("packageInfo", PackageInfo.class);
        } else {
            packageInfo = (PackageInfo) getIntent().getParcelableExtra("packageInfo");
        }
        // Initialize ViewModel
        if (packageInfo == null) {
            Toast.makeText(getApplicationContext(), R.string.cannot_find_package, Toast.LENGTH_SHORT).show();
            finish();
        }
        PackageInfoViewModelFactory factory = new PackageInfoViewModelFactory(getApplication(), packageInfo);
        viewModel = new ViewModelProvider(this, factory).get(PackageInfoViewModel.class);
        // Initialize ViewPager2
        ViewPager2 viewPager2 = binding.viewPager;
        FragmentStateAdapter pagerAdapter = new AppInfoPagerAdapter(this);
        viewPager2.setAdapter(pagerAdapter);
        // Initialize TabLayout
        int[] tabTitles = viewModel.tabTitleResources;
        TabLayout tabLayout = binding.tabLayout;
        for (int tabTitle : tabTitles) {
            tabLayout.addTab(tabLayout.newTab().setText(tabTitle));
        }
        new TabLayoutMediator(tabLayout, viewPager2, true, true, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(getString(tabTitles[0]));
                    break;
                case 1:
                    tab.setText(getString(tabTitles[1]));
                    break;
                case 2:
                    tab.setText(getString(tabTitles[2]));
                    break;
                case 3:
                    tab.setText(getString(tabTitles[3]));
                    break;
                case 4:
                    tab.setText(getString(tabTitles[4]));
                    break;
                case 5:
                    tab.setText(getString(tabTitles[5]));
                    break;
                case 6:
                    tab.setText(getString(tabTitles[6]));
                    break;
                default:
                    tab.setText("Tab");
                    break;
            }
        }).attach();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}