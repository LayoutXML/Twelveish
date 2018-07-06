/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 * This product is protected by copyright and distributed under licenses restricting copying, distribution and decompilation.
 */

package com.layoutxml.twelveish.config;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.layoutxml.twelveish.R;

import java.util.ArrayList;
import java.util.List;

public class DigitalWatchFaceWearableConfigActivity extends Activity {

    private static final String TAG = "ConfigActivity";
    private List<Setting> values = new ArrayList<>();
    private SettingsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wearable_config_activity);

        WearableRecyclerView mWearableRecyclerView = findViewById(R.id.wearable_recycler_view);
        mWearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);

        mAdapter = new SettingsAdapter();
        mWearableRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mWearableRecyclerView.setAdapter(mAdapter);
        generateValues();
    }

    private void generateValues(){
        Setting setting = new Setting();
        setting.setName("Test");
        setting.setIcon(R.drawable.ic_color);
        values.add(setting);
        setting = new Setting();
        setting.setName("Test2");
        setting.setIcon(R.drawable.ic_color);
        values.add(setting);
        setting = new Setting();
        setting.setName("Test3");
        setting.setIcon(R.drawable.ic_color);
        values.add(setting);
        setting = new Setting();
        setting.setName("Test4");
        setting.setIcon(R.drawable.ic_color);
        values.add(setting);
        setting = new Setting();
        setting.setName("Test5");
        setting.setIcon(R.drawable.ic_color);
        values.add(setting);
        mAdapter.notifyDataSetChanged();
    }

    public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.MyViewHolder>{

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView name;
            ImageView icon;

            MyViewHolder(View view) {
                super(view);
                Log.d(TAG,"MyViewHolder");
                name = view.findViewById(R.id.settingsListTextView);
                icon = view.findViewById(R.id.settingsListImagetView);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d(TAG,"MyViewHolder onCreateViewHolder");
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_list_item_view,parent,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Log.d(TAG,"MyViewHolder onBindViewHolder");
            Setting setting = values.get(position);
            holder.name.setText(setting.getName());
            holder.icon.setImageResource(setting.getIcon());

        }

        @Override
        public int getItemCount() {
            return values.size();
        }
    }

}