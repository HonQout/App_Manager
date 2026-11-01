package com.android.appmanager.ui.packageinfo.activity;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.android.appmanager.R;
import com.android.appmanager.adapter.viewpager2.AppInfoPagerAdapter;
import com.android.appmanager.databinding.ActivityAppInfoBinding;
import com.android.appmanager.utils.FileUtils;
import com.android.appmanager.utils.PackageUtils;
import com.android.appmanager.viewmodel.PackageInfoViewModel;
import com.android.appmanager.viewmodel.PackageInfoViewModelFactory;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.IOException;

public class PackageInfoActivity extends AppCompatActivity {
    private static final String TAG = "PackageInfoActivity";
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
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.app_detail);
        }
        binding.toolbar.setNavigationOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed());
        // Get intent extra
        PackageInfo packageInfo
                = IntentCompat.getParcelableExtra(getIntent(), "packageInfo", PackageInfo.class);
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

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_info_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.backup) {
            PackageInfo packageInfo = viewModel.getPackageInfo().getValue();
            if (packageInfo != null) {
                try {
                    PackageUtils.generateAPK(this, packageInfo.packageName,
                            "/Download", FileUtils.ConflictStrategy.OVERWRITE);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
}