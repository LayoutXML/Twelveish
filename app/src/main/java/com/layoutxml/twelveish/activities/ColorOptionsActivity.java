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

public class ColorOptionsActivity extends Activity {

    private static final String TAG = "ColorOptionsActivity";
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
        color.setName("Black");
        color.setColorcode(android.graphics.Color.parseColor("#000000"));
        values.add(color);

        color = new Color();
        color.setName("Red");
        color.setColorcode(android.graphics.Color.parseColor("#ff0000"));
        values.add(color);

        color = new Color();
        color.setName("Magenta");
        color.setColorcode(android.graphics.Color.parseColor("#ff00ff"));
        values.add(color);

        color = new Color();
        color.setName("Yellow");
        color.setColorcode(android.graphics.Color.parseColor("#ffff00"));
        values.add(color);

        color = new Color();
        color.setName("Green");
        color.setColorcode(android.graphics.Color.parseColor("#00ff00"));
        values.add(color);

        color = new Color();
        color.setName("Cyan");
        color.setColorcode(android.graphics.Color.parseColor("#00ffff"));
        values.add(color);

        color = new Color();
        color.setName("Blue");
        color.setColorcode(android.graphics.Color.parseColor("#0000ff"));
        values.add(color);

        color = new Color();
        color.setName("White");
        color.setColorcode(android.graphics.Color.parseColor("#ffffff"));
        values.add(color);

        color = new Color();
        color.setName("Material Red");
        color.setColorcode(android.graphics.Color.parseColor("#A62C23"));
        values.add(color);

        color = new Color();
        color.setName("Material Pink");
        color.setColorcode(android.graphics.Color.parseColor("#A61646"));
        values.add(color);

        color = new Color();
        color.setName("Material Purple");
        color.setColorcode(android.graphics.Color.parseColor("#9224A6"));
        values.add(color);

        color = new Color();
        color.setName("Material Deep Purple");
        color.setColorcode(android.graphics.Color.parseColor("#5E35A6"));
        values.add(color);

        color = new Color();
        color.setName("Material Indigo");
        color.setColorcode(android.graphics.Color.parseColor("#3A4AA6"));
        values.add(color);

        color = new Color();
        color.setName("Material Blue");
        color.setColorcode(android.graphics.Color.parseColor("#1766A6"));
        values.add(color);

        color = new Color();
        color.setName("Material Light Blue");
        color.setColorcode(android.graphics.Color.parseColor("#0272A6"));
        values.add(color);

        color = new Color();
        color.setName("Material Cyan");
        color.setColorcode(android.graphics.Color.parseColor("#0092A6"));
        values.add(color);

        color = new Color();
        color.setName("Material Teal");
        color.setColorcode(android.graphics.Color.parseColor("#00A695"));
        values.add(color);

        color = new Color();
        color.setName("Material Green");
        color.setColorcode(android.graphics.Color.parseColor("#47A64A"));
        values.add(color);

        color = new Color();
        color.setName("Material Light Green");
        color.setColorcode(android.graphics.Color.parseColor("#76A63F"));
        values.add(color);

        color = new Color();
        color.setName("Material Lime");
        color.setColorcode(android.graphics.Color.parseColor("#99A62B"));
        values.add(color);

        color = new Color();
        color.setName("Material Yellow");
        color.setColorcode(android.graphics.Color.parseColor("#A69926"));
        values.add(color);

        color = new Color();
        color.setName("Material Amber");
        color.setColorcode(android.graphics.Color.parseColor("#A67E05"));
        values.add(color);

        color = new Color();
        color.setName("Material Orange");
        color.setColorcode(android.graphics.Color.parseColor("#A66300"));
        values.add(color);

        color = new Color();
        color.setName("Material Deep Orange");
        color.setColorcode(android.graphics.Color.parseColor("#A63716"));
        values.add(color);

        color = new Color();
        color.setName("Material Brown");
        color.setColorcode(android.graphics.Color.parseColor("#A67563"));
        values.add(color);

        color = new Color();
        color.setName("Material Gray");
        color.setColorcode(android.graphics.Color.parseColor("#676767"));
        values.add(color);

        color = new Color();
        color.setName("Material Blue Gray");
        color.setColorcode(android.graphics.Color.parseColor("#7295A6"));
        values.add(color);

        color = new Color();
        color.setName("Gold");
        color.setColorcode(android.graphics.Color.parseColor("#FFD700"));
        values.add(color);

        color = new Color();
        color.setName("Sunset");
        color.setColorcode(android.graphics.Color.parseColor("#F8B195"));
        values.add(color);

        color = new Color();
        color.setName("Fog");
        color.setColorcode(android.graphics.Color.parseColor("#A8A7A7"));
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
                        prefs.edit().putInt(getString(R.string.preference_background_color),selectedMenuItem.getColorcode()).apply();
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
