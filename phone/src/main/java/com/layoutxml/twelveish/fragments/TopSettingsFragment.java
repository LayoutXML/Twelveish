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
import com.layoutxml.twelveish.activities.ColorSelectionActivity;
import com.layoutxml.twelveish.activities.TextSelectionActivity;
import com.layoutxml.twelveish.adapters.ImageRecyclerViewAdapter;
import com.layoutxml.twelveish.adapters.SwitchRecyclerViewAdapter;
import com.layoutxml.twelveish.adapters.TextviewRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class TopSettingsFragment extends Fragment implements ImageRecyclerViewAdapter.ItemClickImageListener, TextviewRecyclerViewAdapter.ItemClickListener, SwitchRecyclerViewAdapter.ItemClickSwitchListener{

    private ImageRecyclerViewAdapter adapterTI;
    private TextviewRecyclerViewAdapter adapterTT;
    private SwitchRecyclerViewAdapter adapterTS;
    private final String settingsTIName = "settingsTI";
    private final String settingsTTName = "settingsTT";
    private final String settingsTSName = "settingsTS";
    private SettingsManager settingsManager;
    private CustomizationScreen activity;
    private List<Pair<String,Integer>> optionsTI;
    private List<Pair<String, String>> optionsTT;

    private final int reqSecColorActive = 0;
    private final int reqSecColorAmbient = 1;
    private final int reqFont = 2;
    private final int reqDateOrder = 3;
    private final int reqDateSymbol = 4;
    private final int reqTextOffset = 5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.top_settings_fragment,container,false);

        activity = (CustomizationScreen) getContext();
        settingsManager = activity.getSettingsManagerComponent().getSettingsManager();

        optionsTI = new ArrayList<>();
        generateColorOptions();

        optionsTT = new ArrayList<>();
        generateTextOptions();

        List<Pair<String,String>> optionsTS = new ArrayList<>();
        optionsTS.add(new Pair<String, String>("Digital Clock",getString(R.string.preference_show_digital_clock)));
        optionsTS.add(new Pair<String, String>("Digital Clock in Ambient",getResources().getString(R.string.preference_show_digital_clock_ambient)));
        optionsTS.add(new Pair<String, String>("Day of the Week",getResources().getString(R.string.preference_show_day)));
        optionsTS.add(new Pair<String, String>("Day of the Week in Ambient",getResources().getString(R.string.preference_show_day_ambient)));
        optionsTS.add(new Pair<String, String>("Date",getResources().getString(R.string.preference_show_calendar)));
        optionsTS.add(new Pair<String, String>("Date in Ambient",getResources().getString(R.string.preference_show_calendar_ambient)));
        optionsTS.add(new Pair<String, String>("Battery Percentage",getResources().getString(R.string.preference_show_battery)));
        optionsTS.add(new Pair<String, String>("Battery Percentage in Ambient",getResources().getString(R.string.preference_show_battery_ambient)));
        optionsTS.add(new Pair<String, String>("24h Format",getResources().getString(R.string.preference_military_time)));
        optionsTS.add(new Pair<String, String>("Show Seconds in Active",getResources().getString(R.string.preference_show_seconds)));

        RecyclerView recyclerViewTI = view.findViewById(R.id.topImageRV);
        recyclerViewTI.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterTI = new ImageRecyclerViewAdapter(getContext(),optionsTI,settingsTIName);
        adapterTI.setClickListener(this);
        recyclerViewTI.setAdapter(adapterTI);

        RecyclerView recyclerViewTT = view.findViewById(R.id.topTextRV);
        recyclerViewTT.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterTT = new TextviewRecyclerViewAdapter(getContext(),optionsTT,settingsTTName);
        adapterTT.setClickListener(this);
        recyclerViewTT.setAdapter(adapterTT);

        RecyclerView recyclerViewTS = view.findViewById(R.id.topSwitchRV);
        recyclerViewTS.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterTS = new SwitchRecyclerViewAdapter(getContext(),optionsTS,settingsTSName, settingsManager);
        adapterTS.setClickListener(this);
        recyclerViewTS.setAdapter(adapterTS);

        return view;
    }

    private void generateColorOptions() {
        optionsTI.clear();
        optionsTI.add(new Pair<String, Integer>("Text Color",settingsManager.integerHashmap.get(getResources().getString(R.string.preference_secondary_text_color))));
        optionsTI.add(new Pair<String, Integer>("Text Color in Ambient",settingsManager.integerHashmap.get(getResources().getString(R.string.preference_secondary_text_color_ambient))));
    }

    private void generateTextOptions(){
        optionsTT.clear();

        int dateOrder = settingsManager.integerHashmap.get(getResources().getString(R.string.preference_date_order));
        String[] dateOrderString = {"Month-Day-Year", "Day-Month-Year", "Year-Month-Day", "Year-Day-Month"};

        optionsTT.add(new Pair<String, String>("Font","Currently set to "+settingsManager.stringHashmap.get(getResources().getString(R.string.preference_font_secondary))));
        optionsTT.add(new Pair<String, String>("Date Order","Currently set to "+dateOrderString[dateOrder]));
        optionsTT.add(new Pair<String, String>("Date Separator Symbol","Currently set to "+settingsManager.stringHashmap.get(getResources().getString(R.string.preference_date_separator))));
        optionsTT.add(new Pair<String, String>("Text Size Offset","Currently set to "+settingsManager.integerHashmap.get(getResources().getString(R.string.secondary_text_size_offset))));
    }

    @Override
    public void onItemClickSwitch(View view, int position, boolean newValue, String name) {
        if (name.equals(settingsTSName)) {
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
        if(name.equals(settingsTTName)){
            Intent intent;

            intent = new Intent(getContext(), TextSelectionActivity.class);
            intent.putExtra("SETTING_TYPE", position + 2);
            startActivityForResult(intent,position + 2);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode== Activity.RESULT_OK) {
            switch (requestCode) {
                case reqSecColorActive:
                    settingsManager.integerHashmap.put(getResources().getString(R.string.preference_secondary_text_color), data.getIntExtra("newColor", Color.parseColor("#000000")));
                    generateColorOptions();
                    adapterTI.notifyDataSetChanged();
                    break;
                case reqSecColorAmbient:
                    settingsManager.integerHashmap.put(getResources().getString(R.string.preference_secondary_text_color_ambient), data.getIntExtra("newColor", Color.parseColor("#000000")));
                    generateColorOptions();
                    adapterTI.notifyDataSetChanged();
                    break;
                case reqFont:
                    settingsManager.stringHashmap.put(activity.getString(R.string.preference_font_secondary), data.getStringExtra("newFont"));

                    settingsManager.significantTimeChange = true;
                    generateTextOptions();
                    adapterTT.notifyDataSetChanged();
                    break;
                case reqDateOrder:
                    settingsManager.integerHashmap.put(activity.getString(R.string.preference_date_order), data.getIntExtra("newDateOrder", 0));

                    generateTextOptions();
                    settingsManager.significantTimeChange = true;

                    adapterTT.notifyDataSetChanged();
                    activity.invalidatePreview();
                    break;
                case reqDateSymbol:
                    break;
                case reqTextOffset:
                    break;
                default:
                    break;
            }
        }
    }
}
