package com.layoutxml.twelveish.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.layoutxml.twelveish.CustomizationScreen;
import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.SettingsManager;
import com.layoutxml.twelveish.activities.ColorSelectionActivity;
import com.layoutxml.twelveish.activities.LanguageSelectionActivity;
import com.layoutxml.twelveish.activities.TextSelectionActivity;
import com.layoutxml.twelveish.adapters.ImageRecyclerViewAdapter;
import com.layoutxml.twelveish.adapters.SwitchRecyclerViewAdapter;
import com.layoutxml.twelveish.adapters.TextviewRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SharedSettingsFragment extends Fragment implements ImageRecyclerViewAdapter.ItemClickImageListener, TextviewRecyclerViewAdapter.ItemClickListener{

    private ImageRecyclerViewAdapter adapterMI;
    private TextviewRecyclerViewAdapter adapterMT;
    private final String settingsMIName = "settingsSI";
    private final String settingsMTName = "settingsST";
    private SettingsManager settingsManager;
    private CustomizationScreen activity;
    private List<Pair<String,Integer>> optionsTI;
    private List<Pair<String, String>> optionsTT;
    private int colorSelectionRequestCode = 0;
    private int languageSelectionRequestCode = 1;
    RecyclerView recyclerViewTT;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.shared_settings_fragment,container,false);

        activity = (CustomizationScreen) getContext();
        settingsManager = activity.getSettingsManagerComponent().getSettingsManager();

        optionsTI = new ArrayList<>();
        generateColorOptions();

        optionsTT = new ArrayList<>();
        generateLanguageOptions();

        RecyclerView recyclerViewTI = view.findViewById(R.id.topImageRV);
        recyclerViewTI.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterMI = new ImageRecyclerViewAdapter(getContext(),optionsTI, settingsMIName);
        adapterMI.setClickListener(this);
        recyclerViewTI.setAdapter(adapterMI);

        recyclerViewTT = view.findViewById(R.id.topTextRV);
        recyclerViewTT.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterMT = new TextviewRecyclerViewAdapter(getContext(),optionsTT, settingsMTName);
        adapterMT.setClickListener(this);
        recyclerViewTT.setAdapter(adapterMT);

        return view;
    }

    private void generateColorOptions() {
        optionsTI.clear();
        optionsTI.add(new Pair<String, Integer>("Background Color",settingsManager.integerHashmap.get(getResources().getString(R.string.preference_background_color))));
    }

    private void generateLanguageOptions(){
        optionsTT.clear();
        String chosenLanguage = new Locale(settingsManager.stringHashmap.get(getString(R.string.preference_language)))
                .getDisplayLanguage(new Locale("en")); // return the name of the chosen language in English
        optionsTT.add(new Pair<String, String>("Language","Currently set to "+chosenLanguage));

    }

    @Override
    public void onItemClickImage(View view, int position, Integer currentColor, String name) {
        Intent intent = new Intent(getContext(), ColorSelectionActivity.class);
        startActivityForResult(intent, colorSelectionRequestCode);
    }

    @Override
    public void onItemClick(View view, int position, String name){
        Intent intent = new Intent(getContext(), LanguageSelectionActivity.class);
        intent.putExtra("SETTING_TYPE", TextSelectionActivity.LANGUAGE_SELECTION);
        startActivityForResult(intent, languageSelectionRequestCode);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode== Activity.RESULT_OK) {
            switch (requestCode) {
                case 0:
                    settingsManager.integerHashmap.put(getResources().getString(R.string.preference_background_color), data.getIntExtra("newColor", Color.parseColor("#000000")));
                    generateColorOptions();
                    adapterMI.notifyDataSetChanged();
                    break;
                case 1:
                    settingsManager.stringHashmap.put(getResources().getString(R.string.preference_language), data.getStringExtra("newLanguage"));

                    settingsManager.significantTimeChange = true;
                    activity.invalidatePreview();

                    optionsTT.clear();
                    generateLanguageOptions();
                    adapterMT.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }
}


