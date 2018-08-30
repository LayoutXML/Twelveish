/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 *
 */

package com.layoutxml.twelveish.config;

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
import com.layoutxml.twelveish.activities.CapitalisationActivity;
import com.layoutxml.twelveish.activities.ColorOptionsActivity;
import com.layoutxml.twelveish.activities.ColorOptionsListActivity;
import com.layoutxml.twelveish.activities.DateOptionsListActivity;
import com.layoutxml.twelveish.activities.FontOptionsActivity;
import com.layoutxml.twelveish.activities.LanguageOptionsActivity;
import com.layoutxml.twelveish.activities.Licenses;
import com.layoutxml.twelveish.activities.MiscOptionsActivity;
import com.layoutxml.twelveish.activities.ShowHideOptionsActivity;
import com.layoutxml.twelveish.objects.Setting;

import java.util.ArrayList;
import java.util.List;

public class DigitalWatchFaceWearableConfigActivity extends Activity {

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
        setting.setName("Complication");
        setting.setIcon(R.drawable.ic_complication);
        values.add(setting);

        setting = new Setting();
        setting.setName("Colors");
        setting.setIcon(R.drawable.ic_color);
        values.add(setting);

        setting = new Setting();
        setting.setName("Font");
        setting.setIcon(R.drawable.ic_font);
        values.add(setting);

        setting = new Setting();
        setting.setName("Date format");
        setting.setIcon(R.drawable.ic_date);
        values.add(setting);

        setting = new Setting();
        setting.setName("Capitalisation");
        setting.setIcon(R.drawable.ic_capitalisation);
        values.add(setting);

        setting = new Setting();
        setting.setName("Show/hide elements");
        setting.setIcon(R.drawable.ic_showhide);
        values.add(setting);

        setting = new Setting();
        setting.setName("Language");
        setting.setIcon(R.drawable.ic_language);
        values.add(setting);

        setting = new Setting();
        setting.setName("Miscellaneous");
        setting.setIcon(R.drawable.ic_misc);
        values.add(setting);

        setting = new Setting();
        setting.setName("Info");
        setting.setIcon(R.drawable.ic_info);
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
                        switch (position){
                            case 0:
                                if (android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.N) {
                                    intent = new Intent(DigitalWatchFaceWearableConfigActivity.this, ComplicationConfigActivity.class);
                                    DigitalWatchFaceWearableConfigActivity.this.startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(),"Complications not supported",Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 1:
                                intent = new Intent(DigitalWatchFaceWearableConfigActivity.this, ColorOptionsListActivity.class);
                                DigitalWatchFaceWearableConfigActivity.this.startActivity(intent);
                                break;
                            case 2:
                                intent = new Intent(DigitalWatchFaceWearableConfigActivity.this, FontOptionsActivity.class);
                                DigitalWatchFaceWearableConfigActivity.this.startActivity(intent);
                                break;
                            case 3:
                                intent = new Intent(DigitalWatchFaceWearableConfigActivity.this, DateOptionsListActivity.class);
                                DigitalWatchFaceWearableConfigActivity.this.startActivity(intent);
                                break;
                            case 4:
                                intent = new Intent(DigitalWatchFaceWearableConfigActivity.this, CapitalisationActivity.class);
                                DigitalWatchFaceWearableConfigActivity.this.startActivity(intent);
                                break;
                            case 5:
                                intent = new Intent(DigitalWatchFaceWearableConfigActivity.this, ShowHideOptionsActivity.class);
                                DigitalWatchFaceWearableConfigActivity.this.startActivity(intent);
                                break;
                            case 6:
                                intent = new Intent(DigitalWatchFaceWearableConfigActivity.this, LanguageOptionsActivity.class);
                                DigitalWatchFaceWearableConfigActivity.this.startActivity(intent);
                                break;
                            case 7:
                                intent = new Intent(DigitalWatchFaceWearableConfigActivity.this, MiscOptionsActivity.class);
                                DigitalWatchFaceWearableConfigActivity.this.startActivity(intent);
                                break;
                            case 8:
                                intent = new Intent(DigitalWatchFaceWearableConfigActivity.this, Licenses.class);
                                DigitalWatchFaceWearableConfigActivity.this.startActivity(intent);
                                break;
                            default:
                                break;
                        }
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