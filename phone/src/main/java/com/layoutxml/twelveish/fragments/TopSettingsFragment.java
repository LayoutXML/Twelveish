package com.layoutxml.twelveish.fragments;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.top_settings_fragment,container,false);

        activity = (CustomizationScreen) getContext();
        settingsManager = activity.getSettingsManagerComponent().getSettingsManager();

        List<Pair<String,Integer>> optionsTI = new ArrayList<>();
        optionsTI.add(new Pair<String, Integer>("Text Color",settingsManager.integerHashmap.get(getResources().getString(R.string.preference_secondary_text_color))));
        optionsTI.add(new Pair<String, Integer>("Text Color in Ambient",settingsManager.integerHashmap.get(getResources().getString(R.string.preference_secondary_text_color_ambient))));

        List<Pair<String, String>> optionsTT = new ArrayList<>();
        optionsTT.add(new Pair<String, String>("Font","Currently set "+settingsManager.stringHashmap.get(getResources().getString(R.string.preference_font_secondary)))); //TODO
        optionsTT.add(new Pair<String, String>("Date Order","Currently set "+settingsManager.integerHashmap.get(getResources().getString(R.string.preference_date_order))));
        optionsTT.add(new Pair<String, String>("Date Separator Symbol","Currently set "+settingsManager.stringHashmap.get(getResources().getString(R.string.preference_date_separator))));
        optionsTT.add(new Pair<String, String>("Text Size Offset","Currently set "+settingsManager.integerHashmap.get(getResources().getString(R.string.secondary_text_size_offset))));

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

    @Override
    public void onItemClickSwitch(View view, int position, boolean newValue, String name) {
        if (name.equals(settingsTSName)) {
            activity.invalidatePreview();
        }
    }

    @Override
    public Integer onItemClickImage(View view, int position, Integer currentColor, String name) {return 0;}

    @Override
    public void onItemClick(View view, int position, String name){}
}
