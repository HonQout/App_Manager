package com.android.appmanager.adapter.viewpager2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.android.appmanager.constants.Constants;
import com.android.appmanager.fragment.PackageInfoFragment;

public class AppInfoPagerAdapter extends FragmentStateAdapter {
    public AppInfoPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return PackageInfoFragment.newInstance(Constants.PackageInfoScope.Overview);
            case 1:
                return PackageInfoFragment.newInstance(Constants.PackageInfoScope.PermissionDefined);
            case 2:
                return PackageInfoFragment.newInstance(Constants.PackageInfoScope.PermissionRequested);
            case 3:
                return PackageInfoFragment.newInstance(Constants.PackageInfoScope.Activity);
            case 4:
                return PackageInfoFragment.newInstance(Constants.PackageInfoScope.Service);
            case 5:
                return PackageInfoFragment.newInstance(Constants.PackageInfoScope.Provider);
            case 6:
                return PackageInfoFragment.newInstance(Constants.PackageInfoScope.Receiver);
            default:
                return PackageInfoFragment.newInstance(Constants.PackageInfoScope.Overview);
        }
    }

    @Override
    public int getItemCount() {
        return 7;
    }
}
