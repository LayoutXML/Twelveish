/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 *
 */

package com.layoutxml.twelveish;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.layoutxml.twelveish.objects.Setting;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private List<Setting> values = new ArrayList<>();
    private SettingsAdapter mAdapter;

    //Communication:
    public static boolean isWatchConnected = false;
    private Communicator communicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        communicator = new Communicator(getApplicationContext());
        communicator.initiateHandshake(getApplicationContext());

        RecyclerView mRecyclerView = findViewById(R.id.menuList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new SettingsAdapter();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        generateValues();
    }

    private void generateValues() {
        Setting setting = new Setting();
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
        setting.setName("Text size offset");
        setting.setIcon(R.drawable.ic_capitalisation);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        communicator.destroy();
        communicator = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Wearable.getDataClient(getApplicationContext()).addListener(communicator);
        communicator.initiateHandshake(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.getDataClient(getApplicationContext()).removeListener(communicator);
    }

    public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.MyViewHolder>{

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView name;
            ImageView icon;

            MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.settingsListTextView);
                icon = view.findViewById(R.id.settingsListImagetView);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition(); // gets item position
                        Intent intent;
                        switch (position){
                            /*case 0:
                                intent = new Intent(MainActivity.this, ComplicationConfigActivity.class);
                                MainActivity.this.startActivity(intent);
                                break;*/
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
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.textview_item,parent,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
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
