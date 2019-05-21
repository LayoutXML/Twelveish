package com.layoutxml.twelveish;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.wearable.Wearable;
import com.google.android.material.tabs.TabLayout;
import com.layoutxml.twelveish.adapters.OptionsPagerAdapter;
import com.layoutxml.twelveish.dagger.App;
import com.layoutxml.twelveish.dagger.DaggerSettingsManagerComponent;
import com.layoutxml.twelveish.dagger.SettingsManagerComponent;
import com.layoutxml.twelveish.fragments.PreviewFragment;

public class CustomizationScreen extends AppCompatActivity {

    private SettingsManagerComponent settingsManagerComponent;
    private Communicator communicator;
    private static final String TAG = "CustomizationScreen";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customize_screen);

        communicator = ((App) getApplication()).getCommunicatorComponent().getCommunicator();
        communicator.initiateHandshake();
        Log.d(TAG, "communicatorID" + communicator);

        settingsManagerComponent = DaggerSettingsManagerComponent.factory().create(getApplicationContext());

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment previewFragment = new PreviewFragment();
        fragmentTransaction.replace(R.id.fragmentPreview,previewFragment);

        ViewPager viewPager = findViewById(R.id.pagerOptions);
        OptionsPagerAdapter optionsPagerAdapter = new OptionsPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(optionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        fragmentTransaction.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.getDataClient(getApplicationContext()).removeListener(communicator); //unregistering and registering in every activity separately
        //because we want to unregister when application closes and there's no onPause/onDestroy for classes like App.java that extend Application
    }

    @Override
    protected void onResume() {
        super.onResume();
        Wearable.getDataClient(getApplicationContext()).addListener(communicator);
        communicator.initiateHandshake();
    }

    public SettingsManagerComponent getSettingsManagerComponent() {
        return settingsManagerComponent;
    }
}
