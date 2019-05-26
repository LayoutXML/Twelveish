package com.layoutxml.twelveish.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.layoutxml.twelveish.fragments.ComplicationSettingsFragment;
import com.layoutxml.twelveish.fragments.WatchSettingsFragment;

public class OptionsPagerAdapter extends FragmentStatePagerAdapter {

    public OptionsPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            default:
                return new WatchSettingsFragment();
            case 0:
                return new ComplicationSettingsFragment();
            case 1:
                return new WatchSettingsFragment();
            case 2:
                return new WatchSettingsFragment();
            case 3:
                return new WatchSettingsFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            default:
                return null;
            case 0:
                return "Complications";
            case 1:
                return "Top";
            case 2:
                return "Main text";
            case 3:
                return "Shared";
        }
    }
}
