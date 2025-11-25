package com.android.appmanager.adapter.viewpager2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.android.appmanager.constants.Constants;
import com.android.appmanager.fragment.AppListFragment;

public class AppListPagerAdapter extends FragmentStateAdapter {
    public AppListPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return AppListFragment.newInstance(Constants.AppScope.All);
            case 1:
                return AppListFragment.newInstance(Constants.AppScope.System);
            case 2:
                return AppListFragment.newInstance(Constants.AppScope.User);
            default:
                return AppListFragment.newInstance(Constants.AppScope.All);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}