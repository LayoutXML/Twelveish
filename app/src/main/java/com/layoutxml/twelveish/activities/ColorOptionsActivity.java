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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.objects.IntegerOption;

import java.util.ArrayList;
import java.util.List;

public class ColorOptionsActivity extends Activity{

    private static final String TAG = "MainColorOptionsActivit";
    private List<IntegerOption> values = new ArrayList<>();
    private ColorsAdapter mAdapter;
    private SharedPreferences prefs;
    private String preferencesKey;

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

        if (getIntent().getStringExtra("Activity").equals("TextColor")) {
            generateTextColorValues();
        }
        else if (getIntent().getStringExtra("Activity").equals("BackgroundColor")) {
            generateBackgroundColorValues();
        }
        preferencesKey = getIntent().getStringExtra("SettingsValue");
    }

    private void generateTextColorValues(){
        IntegerOption integerOption = new IntegerOption();
        integerOption.setName("White");
        integerOption.setInteger(android.graphics.Color.parseColor("#ffffff"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Gray 400");
        integerOption.setInteger(android.graphics.Color.parseColor("#bdbdbd"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Gray 500");
        integerOption.setInteger(android.graphics.Color.parseColor("#9e9e9e"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Gray 600");
        integerOption.setInteger(android.graphics.Color.parseColor("#757575"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Gray 700");
        integerOption.setInteger(android.graphics.Color.parseColor("#616161"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Gray 800");
        integerOption.setInteger(android.graphics.Color.parseColor("#424242"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Gray 900");
        integerOption.setInteger(android.graphics.Color.parseColor("#212121"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Black");
        integerOption.setInteger(android.graphics.Color.parseColor("#000000"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Red");
        integerOption.setInteger(android.graphics.Color.parseColor("#ff0000"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Magenta");
        integerOption.setInteger(android.graphics.Color.parseColor("#ff00ff"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Yellow");
        integerOption.setInteger(android.graphics.Color.parseColor("#ffff00"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Green");
        integerOption.setInteger(android.graphics.Color.parseColor("#00ff00"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Cyan");
        integerOption.setInteger(android.graphics.Color.parseColor("#00ffff"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Blue");
        integerOption.setInteger(android.graphics.Color.parseColor("#0000ff"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Red");
        integerOption.setInteger(android.graphics.Color.parseColor("#A62C23"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Pink");
        integerOption.setInteger(android.graphics.Color.parseColor("#A61646"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Purple");
        integerOption.setInteger(android.graphics.Color.parseColor("#9224A6"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Deep Purple");
        integerOption.setInteger(android.graphics.Color.parseColor("#5E35A6"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Indigo");
        integerOption.setInteger(android.graphics.Color.parseColor("#3A4AA6"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Blue");
        integerOption.setInteger(android.graphics.Color.parseColor("#1766A6"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Light Blue");
        integerOption.setInteger(android.graphics.Color.parseColor("#0272A6"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Cyan");
        integerOption.setInteger(android.graphics.Color.parseColor("#0092A6"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Teal");
        integerOption.setInteger(android.graphics.Color.parseColor("#00A695"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Green");
        integerOption.setInteger(android.graphics.Color.parseColor("#47A64A"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Light Green");
        integerOption.setInteger(android.graphics.Color.parseColor("#76A63F"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Lime");
        integerOption.setInteger(android.graphics.Color.parseColor("#99A62B"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Yellow");
        integerOption.setInteger(android.graphics.Color.parseColor("#A69926"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Amber");
        integerOption.setInteger(android.graphics.Color.parseColor("#A67E05"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Orange");
        integerOption.setInteger(android.graphics.Color.parseColor("#A66300"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Deep Orange");
        integerOption.setInteger(android.graphics.Color.parseColor("#A63716"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Brown");
        integerOption.setInteger(android.graphics.Color.parseColor("#A67563"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Gray");
        integerOption.setInteger(android.graphics.Color.parseColor("#676767"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Blue Gray");
        integerOption.setInteger(android.graphics.Color.parseColor("#7295A6"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Gold");
        integerOption.setInteger(android.graphics.Color.parseColor("#FFD700"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Sunset");
        integerOption.setInteger(android.graphics.Color.parseColor("#F8B195"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Fog");
        integerOption.setInteger(android.graphics.Color.parseColor("#A8A7A7"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Summer Red");
        integerOption.setInteger(android.graphics.Color.parseColor("#fe4a49"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Aqua");
        integerOption.setInteger(android.graphics.Color.parseColor("#2ab7ca"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Sun");
        integerOption.setInteger(android.graphics.Color.parseColor("#fed766"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Dawn");
        integerOption.setInteger(android.graphics.Color.parseColor("#451e3e"));
        values.add(integerOption);

        mAdapter.notifyDataSetChanged();
    }

    private void generateBackgroundColorValues(){
        IntegerOption integerOption = new IntegerOption();
        integerOption.setName("Black");
        integerOption.setInteger(android.graphics.Color.parseColor("#000000"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Red");
        integerOption.setInteger(android.graphics.Color.parseColor("#ff0000"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Magenta");
        integerOption.setInteger(android.graphics.Color.parseColor("#ff00ff"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Yellow");
        integerOption.setInteger(android.graphics.Color.parseColor("#ffff00"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Green");
        integerOption.setInteger(android.graphics.Color.parseColor("#00ff00"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Cyan");
        integerOption.setInteger(android.graphics.Color.parseColor("#00ffff"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Blue");
        integerOption.setInteger(android.graphics.Color.parseColor("#0000ff"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("White");
        integerOption.setInteger(android.graphics.Color.parseColor("#ffffff"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Red");
        integerOption.setInteger(android.graphics.Color.parseColor("#A62C23"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Pink");
        integerOption.setInteger(android.graphics.Color.parseColor("#A61646"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Purple");
        integerOption.setInteger(android.graphics.Color.parseColor("#9224A6"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Deep Purple");
        integerOption.setInteger(android.graphics.Color.parseColor("#5E35A6"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Indigo");
        integerOption.setInteger(android.graphics.Color.parseColor("#3A4AA6"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Blue");
        integerOption.setInteger(android.graphics.Color.parseColor("#1766A6"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Light Blue");
        integerOption.setInteger(android.graphics.Color.parseColor("#0272A6"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Cyan");
        integerOption.setInteger(android.graphics.Color.parseColor("#0092A6"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Teal");
        integerOption.setInteger(android.graphics.Color.parseColor("#00A695"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Green");
        integerOption.setInteger(android.graphics.Color.parseColor("#47A64A"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Light Green");
        integerOption.setInteger(android.graphics.Color.parseColor("#76A63F"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Lime");
        integerOption.setInteger(android.graphics.Color.parseColor("#99A62B"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Yellow");
        integerOption.setInteger(android.graphics.Color.parseColor("#A69926"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Amber");
        integerOption.setInteger(android.graphics.Color.parseColor("#A67E05"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Orange");
        integerOption.setInteger(android.graphics.Color.parseColor("#A66300"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Deep Orange");
        integerOption.setInteger(android.graphics.Color.parseColor("#A63716"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Brown");
        integerOption.setInteger(android.graphics.Color.parseColor("#A67563"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Gray");
        integerOption.setInteger(android.graphics.Color.parseColor("#676767"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Material Blue Gray");
        integerOption.setInteger(android.graphics.Color.parseColor("#7295A6"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Gold");
        integerOption.setInteger(android.graphics.Color.parseColor("#FFD700"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Sunset");
        integerOption.setInteger(android.graphics.Color.parseColor("#F8B195"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Fog");
        integerOption.setInteger(android.graphics.Color.parseColor("#A8A7A7"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Summer Red");
        integerOption.setInteger(android.graphics.Color.parseColor("#fe4a49"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Aqua");
        integerOption.setInteger(android.graphics.Color.parseColor("#2ab7ca"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Sun");
        integerOption.setInteger(android.graphics.Color.parseColor("#fed766"));
        values.add(integerOption);

        integerOption = new IntegerOption();
        integerOption.setName("Dawn");
        integerOption.setInteger(android.graphics.Color.parseColor("#451e3e"));
        values.add(integerOption);

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
                        IntegerOption selectedMenuItem = values.get(position);
                        prefs.edit().putInt(preferencesKey,selectedMenuItem.getInteger()).apply();
                        Toast.makeText(getApplicationContext(), "Preference set", Toast.LENGTH_SHORT).show();
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
            IntegerOption integerOption = values.get(position);
            holder.name.setText(integerOption.getName());
            holder.icon.setColorFilter(integerOption.getInteger(), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        @Override
        public int getItemCount() {
            return values.size();
        }
    }

}
