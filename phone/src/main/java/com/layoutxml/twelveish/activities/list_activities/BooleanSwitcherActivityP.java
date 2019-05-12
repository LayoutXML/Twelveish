/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 *
 */

package com.layoutxml.twelveish.activities.list_activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.layoutxml.twelveish.MainActivity;
import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.objects.BooleanOptionP;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
public class BooleanSwitcherActivityP extends Activity{

    private List<BooleanOptionP> values = new ArrayList<>();
    private MiscOptionsAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private HashMap<String, Boolean> preferences;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.menuList);
        mRecyclerView.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBarBoolean);
        progressBar.setVisibility(View.VISIBLE);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MiscOptionsAdapter();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        if (MainActivity.communicator!=null) {
            MainActivity.communicator.requestBooleanPreferences(getApplicationContext(), new WeakReference<>(BooleanSwitcherActivityP.this));
            if (MainActivity.communicator.booleanPreferences!=null)
                receivedDataListener(MainActivity.communicator.booleanPreferences);
        } else {
            finish();
        }
    }

    private void generateMiscOptionsValues(){
        BooleanOptionP option = new BooleanOptionP();
        option.setName("24h format (Words)");
        option.setKey(getString(R.string.preference_militarytext_time));
        option.setDefaultOption(false);
        option.setBool(preferences.get("militaryTextTime"));
        values.add(option);

        option = new BooleanOptionP();
        option.setName("24h format (Digital)");
        option.setKey(getString(R.string.preference_military_time));
        option.setDefaultOption(false);
        option.setBool(preferences.get("militaryTime"));
        values.add(option);

        option = new BooleanOptionP();
        option.setName("Show am/pm (Digital)");
        option.setKey(getString(R.string.preference_ampm));
        option.setDefaultOption(true);
        option.setBool(preferences.get("ampm"));
        values.add(option);

        option = new BooleanOptionP();
        option.setName("Disable tapping on complications");
        option.setKey(getString(R.string.preference_tap));
        option.setDefaultOption(false);
        option.setBool(preferences.get("disableComplicationTap"));
        values.add(option);

        option = new BooleanOptionP();
        option.setName("Legacy word arrangement");
        option.setKey(getString(R.string.preference_legacy_word_arrangement));
        option.setDefaultOption(false);
        option.setBool(preferences.get("legacyWords"));
        values.add(option);

        option = new BooleanOptionP();
        option.setName("Show seconds (active)");
        option.setKey(getString(R.string.preference_show_seconds));
        option.setDefaultOption(true);
        option.setBool(preferences.get("showSeconds"));
        values.add(option);

        option = new BooleanOptionP();
        option.setName("Show Suffixes");
        option.setKey(getString(R.string.preference_show_suffixes));
        option.setDefaultOption(true);
        option.setBool(preferences.get("showSuffixes"));
        values.add(option);

        mAdapter.notifyDataSetChanged();
    }

    private void generateShowHideValues(){
        BooleanOptionP option = new BooleanOptionP();
        option.setName("Digital clock (active)");
        option.setKey(getString(R.string.preference_show_secondary_active));
        option.setDefaultOption(true);
        option.setBool(preferences.get("showSecondaryActive"));
        values.add(option);

        option = new BooleanOptionP();
        option.setName("Digital clock (ambient)");
        option.setKey(getString(R.string.preference_show_secondary));
        option.setDefaultOption(true);
        option.setBool(preferences.get("showSecondary"));
        values.add(option);

        option = new BooleanOptionP();
        option.setName("Week day (active)");
        option.setKey(getString(R.string.preference_show_day));
        option.setDefaultOption(true);
        option.setBool(preferences.get("showDay"));
        values.add(option);

        option = new BooleanOptionP();
        option.setName("Week day (ambient)");
        option.setKey(getString(R.string.preference_show_day_ambient));
        option.setDefaultOption(true);
        option.setBool(preferences.get("showDayAmbient"));
        values.add(option);

        option = new BooleanOptionP();
        option.setName("Date (active)");
        option.setKey(getString(R.string.preference_show_secondary_calendar_active));
        option.setDefaultOption(true);
        option.setBool(preferences.get("showSecondaryCalendarActive"));
        values.add(option);

        option = new BooleanOptionP();
        option.setName("Date (ambient)");
        option.setKey(getString(R.string.preference_show_secondary_calendar));
        option.setDefaultOption(true);
        option.setBool(preferences.get("showSecondaryCalendar"));
        values.add(option);

        option = new BooleanOptionP();
        option.setName("Battery (active)");
        option.setKey(getString(R.string.preference_show_battery));
        option.setDefaultOption(true);
        option.setBool(preferences.get("showBattery"));
        values.add(option);

        option = new BooleanOptionP();
        option.setName("Battery (ambient)");
        option.setKey(getString(R.string.preference_show_battery_ambient));
        option.setDefaultOption(true);
        option.setBool(preferences.get("showBatteryAmbient"));
        values.add(option);

        option = new BooleanOptionP();
        option.setName("Word clock (active)");
        option.setKey(getString(R.string.preference_show_words));
        option.setDefaultOption(true);
        option.setBool(preferences.get("showWords"));
        values.add(option);

        option = new BooleanOptionP();
        option.setName("Word clock (ambient)");
        option.setKey(getString(R.string.preference_show_words_ambient));
        option.setDefaultOption(true);
        option.setBool(preferences.get("showWordsAmbient"));
        values.add(option);

        option = new BooleanOptionP();
        option.setName("Complications (active)");
        option.setKey(getString(R.string.preference_show_complications));
        option.setDefaultOption(true);
        option.setBool(preferences.get("showComplication"));
        values.add(option);

        option = new BooleanOptionP();
        option.setName("Complications (ambient)");
        option.setKey(getString(R.string.preference_show_complications_ambient));
        option.setDefaultOption(true);
        option.setBool(preferences.get("showComplicationAmbient"));
        values.add(option);

        mAdapter.notifyDataSetChanged();
    }

    public void receivedDataListener(String[] array) {
        preferences = new HashMap<>();

        for (int i=0; i<array.length; i+=2) {
            boolean value = array[i+1].equals("true");
            preferences.put(array[i],value);
        }

        if (getIntent().getStringExtra("Activity").equals("MiscOptions"))
            generateMiscOptionsValues();
        else if (getIntent().getStringExtra("Activity").equals("ShowHide"))
            generateShowHideValues();

        mRecyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    public class MiscOptionsAdapter extends RecyclerView.Adapter<MiscOptionsAdapter.MyViewHolder>{

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView name;
            Switch switcher;

            MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.miscoptionsListTextView);
                switcher = view.findViewById(R.id.miscoptionsListSwitch);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition(); // gets item position
                        BooleanOptionP selected = values.get(position);
                        selected.setBool(!selected.getBool());
                        switcher.setChecked(selected.getBool());
                        if (MainActivity.communicator!=null) {
                            MainActivity.communicator.sendPreference(selected.getKey(), selected.getBool().toString(), "Boolean", getApplicationContext());
                            Toast.makeText(getApplicationContext(), "Preference set", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

        @NonNull
        @Override
        public MiscOptionsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.switch_and_textview_item,parent,false);
            return new MiscOptionsAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MiscOptionsAdapter.MyViewHolder holder, int position) {
            BooleanOptionP option = values.get(position);
            holder.name.setText(option.getName());
            holder.switcher.setChecked(option.getBool());
        }

        @Override
        public int getItemCount() {
            return values.size();
        }
    }
}
