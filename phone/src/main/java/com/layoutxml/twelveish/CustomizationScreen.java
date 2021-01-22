package com.layoutxml.twelveish;

import android.os.Bundle;
import android.provider.Settings;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.wearable.Wearable;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.layoutxml.twelveish.adapters.OptionsPagerAdapter;
import com.layoutxml.twelveish.dagger.App;
import com.layoutxml.twelveish.dagger.DaggerSettingsManagerComponent;
import com.layoutxml.twelveish.dagger.SettingsManagerComponent;
import com.layoutxml.twelveish.fragments.PreviewFragment;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;


public class CustomizationScreen extends AppCompatActivity implements View.OnClickListener {

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

        //private static final Type REVIEW_TYPE = new TypeToken<List<Revi>>()

        settingsManagerComponent = DaggerSettingsManagerComponent.factory().create(getApplicationContext());


        final SettingsManager testSettings = settingsManagerComponent.getSettingsManager();
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader(this.getFilesDir().toString() + "/test.json"));
            reader.beginObject();
            while(reader.hasNext()){
                String nameToRead = reader.nextName();
                if(nameToRead.equals("stringHashMap")){
                    reader.beginObject();
                    while(reader.hasNext()){
                        String firstString = reader.nextName();
                        String secondString = reader.nextString();
                        testSettings.stringHashmap.put(firstString, secondString);
                    }
                    reader.endObject();
                } else if (nameToRead.equals("booleanHashMap")){
                    reader.beginObject();
                    while (reader.hasNext()){
                        testSettings.booleanHashmap.put(reader.nextName(), reader.nextBoolean());
                    }
                    reader.endObject();
                } else if(nameToRead.equals("integerHashMap")){
                    reader.beginObject();
                    while(reader.hasNext()){
                        testSettings.integerHashmap.put(reader.nextName(), reader.nextInt());
                    }
                    reader.endObject();
                }
            }

            reader.endObject();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


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

        ImageButton saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        ImageButton sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        SettingsManager settingsManager = settingsManagerComponent.getSettingsManager();
        switch (view.getId()){
            case R.id.saveButton:

                Gson gson = new Gson();
                HashMap<String, HashMap> settingMap = new HashMap<>();
                settingMap.put("stringHashMap", settingsManager.stringHashmap);
                settingMap.put("booleanHashMap", settingsManager.booleanHashmap);
                settingMap.put("integerHashMap", settingsManager.integerHashmap);

                String mapString = gson.toJson(settingMap);

                try {
                    String fileName = this.getFilesDir().toString() + "/test.json";
                    FileWriter writer = new FileWriter(fileName);
                    gson.toJson(settingMap, writer);
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.sendButton:
                Toast.makeText(getApplicationContext(), "Applying watchface", Toast.LENGTH_LONG).show();
                communicator.sendWatchFace(settingsManager, getApplicationContext());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }

    }

    public interface AmoledChange {
        void ambientModeChange(boolean value);
    }

    public void invalidatePreview() {
        previewFragment.invalidate();
    }
}
