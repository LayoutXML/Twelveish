/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 *
 */

package com.layoutxml.twelveish.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.objects.BooleanOption;

import java.util.ArrayList;
import java.util.List;

public class ShowHideOptionsActivity extends Activity{


    private List<BooleanOption> values = new ArrayList<>();
    private ShowHideOptionsAdapter mAdapter;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTheme(R.style.MainStyle);
        setContentView(R.layout.wearablerecyclerview_activity);

        prefs = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        WearableRecyclerView mWearableRecyclerView = findViewById(R.id.wearable_recycler_view);
        mWearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);

        mAdapter = new ShowHideOptionsAdapter();
        mWearableRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mWearableRecyclerView.setAdapter(mAdapter);
        generateValues();
    }

    private void generateValues(){
        BooleanOption option = new BooleanOption();
        option.setName("Digital clock (active)");
        option.setKey(getString(R.string.preference_show_secondary_active));
        option.setDefaultOption(true);
        values.add(option);

        option = new BooleanOption();
        option.setName("Digital clock (ambient)");
        option.setKey(getString(R.string.preference_show_secondary));
        option.setDefaultOption(true);
        values.add(option);

        option = new BooleanOption();
        option.setName("Week day (active)");
        option.setKey(getString(R.string.preference_show_day));
        option.setDefaultOption(true);
        values.add(option);

        option = new BooleanOption();
        option.setName("Week day (ambient)");
        option.setKey(getString(R.string.preference_show_day_ambient));
        option.setDefaultOption(true);
        values.add(option);

        option = new BooleanOption();
        option.setName("Date (active)");
        option.setKey(getString(R.string.preference_show_secondary_calendar_active));
        option.setDefaultOption(true);
        values.add(option);

        option = new BooleanOption();
        option.setName("Date (ambient)");
        option.setKey(getString(R.string.preference_show_secondary_calendar));
        option.setDefaultOption(true);
        values.add(option);

        option = new BooleanOption();
        option.setName("Battery (active)");
        option.setKey(getString(R.string.preference_show_battery));
        option.setDefaultOption(true);
        values.add(option);

        option = new BooleanOption();
        option.setName("Battery (ambient)");
        option.setKey(getString(R.string.preference_show_battery_ambient));
        option.setDefaultOption(true);
        values.add(option);

        option = new BooleanOption();
        option.setName("Word clock (active)");
        option.setKey(getString(R.string.preference_show_words));
        option.setDefaultOption(true);
        values.add(option);

        option = new BooleanOption();
        option.setName("Word clock (ambient)");
        option.setKey(getString(R.string.preference_show_words_ambient));
        option.setDefaultOption(true);
        values.add(option);

        option = new BooleanOption();
        option.setName("Complications (active)");
        option.setKey(getString(R.string.preference_show_complications));
        option.setDefaultOption(true);
        values.add(option);

        option = new BooleanOption();
        option.setName("Complications (ambient)");
        option.setKey(getString(R.string.preference_show_complications_ambient));
        option.setDefaultOption(true);
        values.add(option);

        mAdapter.notifyDataSetChanged();
    }

    public class ShowHideOptionsAdapter extends RecyclerView.Adapter<ShowHideOptionsAdapter.MyViewHolder>{

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
                        BooleanOption selectedMenuItem = values.get(position);
                        prefs.edit().putBoolean(selectedMenuItem.getKey(),!selectedMenuItem.getBool()).apply();
                        selectedMenuItem.setBool(!selectedMenuItem.getBool());
                        switcher.setChecked(selectedMenuItem.getBool());
                    }
                });
            }
        }

        @NonNull
        @Override
        public ShowHideOptionsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.switch_and_textview_item,parent,false);
            return new ShowHideOptionsAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ShowHideOptionsAdapter.MyViewHolder holder, int position) {
            BooleanOption option = values.get(position);
            holder.name.setText(option.getName());
            Boolean shouldBeOn = prefs.getBoolean(option.getKey(),option.getDefaultOption());
            holder.switcher.setChecked(shouldBeOn);
            option.setBool(shouldBeOn);
        }

        @Override
        public int getItemCount() {
            return values.size();
        }
    }
    
}
