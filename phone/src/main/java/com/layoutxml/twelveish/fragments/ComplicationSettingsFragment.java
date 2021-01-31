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
import com.layoutxml.twelveish.adapters.SwitchRecyclerViewAdapter;
import com.layoutxml.twelveish.adapters.TextviewRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ComplicationSettingsFragment extends Fragment implements TextviewRecyclerViewAdapter.ItemClickListener, SwitchRecyclerViewAdapter.ItemClickSwitchListener {

    private TextviewRecyclerViewAdapter adapterSC;
    private SwitchRecyclerViewAdapter adapterBO;
    private final String complicationSCName = "complicationSC";
    private final String complicationBOName = "complicationBO";
    private SettingsManager settingsManager;
    private CustomizationScreen activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.complication_settings_fragment,container,false);

        activity = (CustomizationScreen) getContext();
        settingsManager = activity.getSettingsManagerComponent().getSettingsManager();

        List<Pair<String, String>> optionsSC = new ArrayList<Pair<String, String>>();
        optionsSC.add(new Pair<String, String>("Left Complication","Click to launch menu on your watch"));
        optionsSC.add(new Pair<String, String>("Right Complication","Click to launch menu on your watch"));
        optionsSC.add(new Pair<String, String>("Bottom Complication","Click to launch menu on your watch"));

        List<Pair<String, String>> optionsBO = new ArrayList<>();
        optionsBO.add(new Pair<String,String>("Disable complication tap actions",getString(R.string.preference_tap_complications)));
        optionsBO.add(new Pair<String, String>("Show complications", getResources().getString(R.string.preference_show_complications)));
        optionsBO.add(new Pair<String, String>("Show complications in ambient", getResources().getString(R.string.preference_show_complications_ambient)));

        RecyclerView recyclerViewSC = view.findViewById(R.id.setComplicationsRV);
        recyclerViewSC.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterSC = new TextviewRecyclerViewAdapter(getContext(),optionsSC, complicationSCName);
        adapterSC.setClickListener(this);
        recyclerViewSC.setAdapter(adapterSC);

        RecyclerView recyclerViewBO = view.findViewById(R.id.complicationsSwitchRV);
        recyclerViewBO.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterBO = new SwitchRecyclerViewAdapter(getContext(),optionsBO, complicationBOName, settingsManager);
        adapterBO.setClickListener(this);
        recyclerViewBO.setAdapter(adapterBO);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onItemClick(View view, int position, String name) {
        if (name.equals(complicationSCName)) {
            //TODO: launch activities
        }
    }

    @Override
    public void onItemClickSwitch(View view, int position, boolean newValue, String name) {
        if (name.equals(complicationBOName)) {
            activity.invalidatePreview();
        }
    }
}
