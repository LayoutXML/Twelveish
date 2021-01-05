package com.layoutxml.twelveish;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

import javax.inject.Inject;


public class CustomizationScreen extends AppCompatActivity {

    @Inject
    SettingsManagerComponent settingsManagerComponent;
    private Communicator communicator;
    private static final String TAG = "CustomizationScreen";
    private boolean isInAmoledMode = false;
    private PreviewFragment previewFragment;

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

        previewFragment = new PreviewFragment();
        fragmentTransaction.replace(R.id.fragmentPreview,previewFragment);

        ViewPager viewPager = findViewById(R.id.pagerOptions);
        OptionsPagerAdapter optionsPagerAdapter = new OptionsPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(optionsPagerAdapter);
        viewPager.setOffscreenPageLimit(4);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        fragmentTransaction.commit();

        final ImageButton changeAmbientMode = findViewById(R.id.changeModeButton);
        changeAmbientMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ambient change button. Old value: "+isInAmoledMode);
                isInAmoledMode = !isInAmoledMode;
                if (isInAmoledMode) {
                    changeAmbientMode.setImageDrawable(getDrawable(R.drawable.ic_inactive));
                } else {
                    changeAmbientMode.setImageDrawable(getDrawable(R.drawable.ic_active));
                }
                previewFragment.ambientModeChange(isInAmoledMode);
                invalidatePreview();
            }
        });
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

    public interface AmoledChange {
        public void ambientModeChange(boolean value);
    }

    public void invalidatePreview() {
        previewFragment.invalidate();
    }
}
