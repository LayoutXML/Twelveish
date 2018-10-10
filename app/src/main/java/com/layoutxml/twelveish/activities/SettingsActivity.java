/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 *
 */

package com.layoutxml.twelveish.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
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
import android.widget.Toast;

import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.config.ComplicationConfigActivity;
import com.layoutxml.twelveish.objects.Setting;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends Activity {

    private static final String TAG = "ConfigActivity";
    private List<Setting> values = new ArrayList<>();
    private SettingsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wearablerecyclerview_activity);

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
        if (android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.N) {
            setting.setName("Complication");
            setting.setIcon(R.drawable.ic_complication);
            setting.setActivity(ComplicationConfigActivity.class);
            values.add(setting);
        }

        setting = new Setting();
        setting.setName("Colors");
        setting.setIcon(R.drawable.ic_color);
        setting.setActivity(ColorOptionsActivity.class);
        values.add(setting);

        setting = new Setting();
        setting.setName("Font");
        setting.setIcon(R.drawable.ic_font);
        setting.setActivity(FontOptionsActivity.class);
        values.add(setting);

        setting = new Setting();
        setting.setName("Date format");
        setting.setIcon(R.drawable.ic_date);
        setting.setActivity(DateOptionsListActivity.class);
        values.add(setting);

        setting = new Setting();
        setting.setName("Capitalisation");
        setting.setIcon(R.drawable.ic_capitalisation);
        setting.setActivity(CapitalisationActivity.class);
        values.add(setting);

        setting = new Setting();
        setting.setName("Show/hide elements");
        setting.setIcon(R.drawable.ic_showhide);
        setting.setActivity(ShowHideOptionsActivity.class);
        values.add(setting);

        setting = new Setting();
        setting.setName("Language");
        setting.setIcon(R.drawable.ic_language);
        setting.setActivity(LanguageOptionsActivity.class);
        values.add(setting);

        setting = new Setting();
        setting.setName("Miscellaneous");
        setting.setIcon(R.drawable.ic_misc);
        setting.setActivity(MiscOptionsActivity.class);
        values.add(setting);

        setting = new Setting();
        setting.setName("Info");
        setting.setIcon(R.drawable.ic_info);
        setting.setActivity(AboutActivity.class);
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

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition(); // gets item position
                        Intent intent;
                        intent = new Intent(SettingsActivity.this, values.get(position).getActivity());
                        SettingsActivity.this.startActivity(intent);
                    }
                });
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d(TAG,"MyViewHolder onCreateViewHolder");
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.imageview_and_textview_item,parent,false);
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