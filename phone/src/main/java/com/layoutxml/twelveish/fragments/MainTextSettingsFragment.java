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

public class MainTextSettingsFragment extends Fragment implements ImageRecyclerViewAdapter.ItemClickImageListener, TextviewRecyclerViewAdapter.ItemClickListener, SwitchRecyclerViewAdapter.ItemClickSwitchListener{

    private ImageRecyclerViewAdapter adapterMI;
    private TextviewRecyclerViewAdapter adapterMT;
    private SwitchRecyclerViewAdapter adapterMS;
    private final String settingsMIName = "settingsMI";
    private final String settingsMTName = "settingsMT";
    private final String settingsMSName = "settingsMS";
    private SettingsManager settingsManager;
    private CustomizationScreen activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.top_settings_fragment,container,false);

        activity = (CustomizationScreen) getContext();
        settingsManager = activity.getSettingsManagerComponent().getSettingsManager();

        List<Pair<String,Integer>> optionsTI = new ArrayList<>();
        optionsTI.add(new Pair<String, Integer>("Text Color",settingsManager.integerHashmap.get(getResources().getString(R.string.preference_main_text_color))));
        optionsTI.add(new Pair<String, Integer>("Text Color in Ambient",settingsManager.integerHashmap.get(getResources().getString(R.string.preference_main_text_color_ambient))));

        List<Pair<String, String>> optionsTT = new ArrayList<>();
        optionsTT.add(new Pair<String, String>("Font","Currently set "+settingsManager.stringHashmap.get(getResources().getString(R.string.preference_font))));
        optionsTT.add(new Pair<String, String>("Capitalization","Currently set "+settingsManager.integerHashmap.get(getResources().getString(R.string.preference_capitalisation))));
        optionsTT.add(new Pair<String, String>("Text Size Offset","Currently set "+settingsManager.integerHashmap.get(getResources().getString(R.string.main_text_size_offset))));

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

    @Override
    public void onItemClickSwitch(View view, int position, boolean newValue, String name) {
        if (name.equals(settingsMSName)) {
            activity.invalidatePreview();
        }
    }

    @Override
    public Integer onItemClickImage(View view, int position, Integer currentColor, String name) {return 0;}

    @Override
    public void onItemClick(View view, int position, String name){}
}

