/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 * This product is protected by copyright and distributed under licenses restricting copying, distribution and decompilation.
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.objects.Color;

import java.util.ArrayList;
import java.util.List;

public class TextColorOptionsActivity extends Activity{

    private static final String TAG = "MainColorOptionsActivit";
    private List<Color> values = new ArrayList<>();
    private ColorsAdapter mAdapter;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wearablerecyclerview_activity);

        prefs = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        WearableRecyclerView mWearableRecyclerView = findViewById(R.id.wearable_recycler_view);
        mWearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);

        mAdapter = new ColorsAdapter();
        mWearableRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mWearableRecyclerView.setAdapter(mAdapter);
        generateValues();
    }

    private void generateValues(){
        Color color = new Color();
        color.setName("White");
        color.setColorcode(android.graphics.Color.parseColor("#ffffff"));
        values.add(color);

        color = new Color();
        color.setName("Gray 400");
        color.setColorcode(android.graphics.Color.parseColor("#bdbdbd"));
        values.add(color);

        color = new Color();
        color.setName("Gray 500");
        color.setColorcode(android.graphics.Color.parseColor("#9e9e9e"));
        values.add(color);

        color = new Color();
        color.setName("Gray 600");
        color.setColorcode(android.graphics.Color.parseColor("#757575"));
        values.add(color);

        color = new Color();
        color.setName("Gray 700");
        color.setColorcode(android.graphics.Color.parseColor("#616161"));
        values.add(color);

        color = new Color();
        color.setName("Gray 800");
        color.setColorcode(android.graphics.Color.parseColor("#424242"));
        values.add(color);

        color = new Color();
        color.setName("Gray 900");
        color.setColorcode(android.graphics.Color.parseColor("#212121"));
        values.add(color);

        color = new Color();
        color.setName("Black");
        color.setColorcode(android.graphics.Color.parseColor("#000000"));
        values.add(color);

        mAdapter.notifyDataSetChanged();
    }

    public class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.MyViewHolder>{

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
                        Color selectedMenuItem = values.get(position);
                        prefs.edit().putInt(getIntent().getStringExtra("SettingsValue"),selectedMenuItem.getColorcode()).apply();
                        Toast.makeText(getApplicationContext(), "\""+selectedMenuItem.getName()+"\" set", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }

        @NonNull
        @Override
        public ColorsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d(TAG,"MyViewHolder onCreateViewHolder");
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.imageview_and_textview_item,parent,false);
            return new ColorsAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ColorsAdapter.MyViewHolder holder, int position) {
            Log.d(TAG,"MyViewHolder onBindViewHolder");
            Color color = values.get(position);
            holder.name.setText(color.getName());
            holder.icon.setColorFilter(color.getColorcode(), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        @Override
        public int getItemCount() {
            return values.size();
        }
    }

}
