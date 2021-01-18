package com.layoutxml.twelveish.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.layoutxml.twelveish.activities.AboutActivityP;
import com.layoutxml.twelveish.activities.ColorSelectionActivity;
import com.layoutxml.twelveish.activities.TextSelectionActivity;
import com.layoutxml.twelveish.adapters.ImageRecyclerViewAdapter;
import com.layoutxml.twelveish.adapters.SwitchRecyclerViewAdapter;
import com.layoutxml.twelveish.adapters.TextviewRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainTextSettingsFragment extends Fragment implements ImageRecyclerViewAdapter.ItemClickImageListener, TextviewRecyclerViewAdapter.ItemClickListener, SwitchRecyclerViewAdapter.ItemClickSwitchListener{

    private ImageRecyclerViewAdapter adapterMI;
    private TextviewRecyclerViewAdapter adapterMT;
    private SwitchRecyclerViewAdapter adapterMS;
    private final String settingsMIName = "settingsMI";
    private final String settingsMTName = "settingsMT";
    private final String settingsMSName = "settingsMS";
    private SettingsManager settingsManager;
    private CustomizationScreen activity;
    private List<Pair<String,Integer>> optionsTI;
    private static final String TAG = "MainTextSettingsFragmen";
    private List<Pair<String, String>> optionsTT;


    // Set up request codes for option picker activities
    private final int reqColorActive = 0;
    private final int reqColorAmbient = 1;
    private final int reqFont = 2;
    private final int reqCapitalization = 3;
    private final int reqTextOffset = 4;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.top_settings_fragment,container,false);

        activity = (CustomizationScreen) getContext();
        settingsManager = activity.getSettingsManagerComponent().getSettingsManager();

        optionsTI = new ArrayList<>();
        generateColorOptions();

        generateTextOptions();


        List<Pair<String,String>> optionsTS = new ArrayList<>();
        optionsTS.add(new Pair<String, String>("24h Format",getResources().getString(R.string.preference_military_text_time)));

        RecyclerView recyclerViewTI = view.findViewById(R.id.topImageRV);
        recyclerViewTI.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterMI = new ImageRecyclerViewAdapter(getContext(),optionsTI, settingsMIName);
        adapterMI.setClickListener(this);
        recyclerViewTI.setAdapter(adapterMI);

        RecyclerView recyclerViewTT = view.findViewById(R.id.topTextRV);
        recyclerViewTT.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterMT = new TextviewRecyclerViewAdapter(getContext(),optionsTT, settingsMTName);
        adapterMT.setClickListener(this);
        recyclerViewTT.setAdapter(adapterMT);

        RecyclerView recyclerViewTS = view.findViewById(R.id.topSwitchRV);
        recyclerViewTS.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterMS = new SwitchRecyclerViewAdapter(getContext(),optionsTS, settingsMSName, settingsManager);
        adapterMS.setClickListener(this);
        recyclerViewTS.setAdapter(adapterMS);

        return view;
    }

    private void generateColorOptions() {
        optionsTI.clear();
        optionsTI.add(new Pair<String, Integer>("Text Color",settingsManager.integerHashmap.get(getResources().getString(R.string.preference_main_text_color))));
        optionsTI.add(new Pair<String, Integer>("Text Color in Ambient",settingsManager.integerHashmap.get(getResources().getString(R.string.preference_main_text_color_ambient))));
    }

    private void generateTextOptions(){
        optionsTT = new ArrayList<>();
        optionsTT.add(new Pair<String, String>("Font","Currently set "+settingsManager.stringHashmap.get(getResources().getString(R.string.preference_font))));
        optionsTT.add(new Pair<String, String>("Capitalization","Currently set "+settingsManager.integerHashmap.get(getResources().getString(R.string.preference_capitalisation))));
        optionsTT.add(new Pair<String, String>("Text Size Offset","Currently set "+settingsManager.integerHashmap.get(getResources().getString(R.string.main_text_size_offset))));
    }

    @Override
    public void onItemClickSwitch(View view, int position, boolean newValue, String name) {
        if (name.equals(settingsMSName)) {
            activity.invalidatePreview();
        }
    }

    @Override
    public void onItemClickImage(View view, int position, Integer currentColor, String name) {
        Intent intent = new Intent(getContext(), ColorSelectionActivity.class);
        startActivityForResult(intent, position);
    }

    @Override
    public void onItemClick(View view, int position, String name){
        if(name.equals(settingsMTName)){
            Intent intent;
            switch (position){
                case 0:
                    intent = new Intent(getContext(), TextSelectionActivity.class);
                    intent.putExtra("SETTING_TYPE", TextSelectionActivity.FONT_SELECTION);
                    startActivityForResult(intent, reqFont);
                    break;
                /*case 1:
                    intent = new Intent(getContext(), TextSelectionActivity.class);
                    intent.putExtra("SETTING_TYPE", TextSelectionActivity.CAPITALIZATION);
                    startActivityForResult(intent, reqCapitalization);
                    break;*/
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult: result received");
        if (resultCode==Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: result is for "+requestCode);
            switch (requestCode) {
                case reqColorActive:
                    settingsManager.integerHashmap.put(getResources().getString(R.string.preference_main_text_color), data.getIntExtra("newColor", Color.parseColor("#000000")));
                    generateColorOptions();
                    adapterMI.notifyDataSetChanged();
                    break;
                case reqColorAmbient:
                    settingsManager.integerHashmap.put(getResources().getString(R.string.preference_main_text_color_ambient), data.getIntExtra("newColor", Color.parseColor("#000000")));
                    generateColorOptions();
                    adapterMI.notifyDataSetChanged();
                    break;
                case reqFont:
                    // TODO: Handle new Font setting
                    settingsManager.stringHashmap.put(getResources().getString(R.string.preference_font), data.getStringExtra("newFont"));
                    generateTextOptions();
                    adapterMT.notifyDataSetChanged();
                    activity.invalidatePreview();
                    break;
                case reqCapitalization:
                    // TODO: Handle new capitalization setting
                    settingsManager.integerHashmap.put(getResources().getString(R.string.preference_capitalisation), data.getIntExtra("newCapitalization", 0));
                    generateTextOptions();
                    adapterMT.notifyDataSetChanged();
                    activity.invalidatePreview();
                    break;
                case reqTextOffset:
                    // TODO: Handle new text offset setting
                    break;
                default:
                    break;
            }
        }
    }
}

