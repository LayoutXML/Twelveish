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
        setting.setName("Nothing in this screen yet.\nSupport LayoutXML by purchasing \"Support Development\" app on Google Play Store. Thanks.");
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

            MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.settingsListTextView);

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

        }

        @Override
        public int getItemCount() {
            return values.size();
        }
    }
}
