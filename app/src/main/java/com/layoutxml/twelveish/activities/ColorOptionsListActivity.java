/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 * This product is protected by copyright and distributed under licenses restricting copying, distribution and decompilation.
 */

package com.layoutxml.twelveish.activities;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.TextView;

import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.objects.ColorOption;

import java.util.ArrayList;
import java.util.List;

public class ColorOptionsListActivity extends Activity{

    private static final String TAG = "ColorOptionsListActivit";
    private List<ColorOption> values = new ArrayList<>();
    private ColorOptionsListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wearablerecyclerview_activity);

        WearableRecyclerView mWearableRecyclerView = findViewById(R.id.wearable_recycler_view);
        mWearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);

        mAdapter = new ColorOptionsListAdapter();
        mWearableRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mWearableRecyclerView.setAdapter(mAdapter);
        generateValues();
    }

    private void generateValues(){
        ColorOption dateOption = new ColorOption();
        dateOption.setName("Main text (when active)");
        values.add(dateOption);

        dateOption = new ColorOption();
        dateOption.setName("Main text (in ambient)");
        values.add(dateOption);

        dateOption = new ColorOption();
        dateOption.setName("Secondary color (when active)");
        values.add(dateOption);

        dateOption = new ColorOption();
        dateOption.setName("Secondary color (in ambient)");
        values.add(dateOption);

        dateOption = new ColorOption();
        dateOption.setName("Background (when active)");
        values.add(dateOption);

        mAdapter.notifyDataSetChanged();
    }

    public class ColorOptionsListAdapter extends RecyclerView.Adapter<ColorOptionsListAdapter.MyViewHolder>{

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView name;

            MyViewHolder(View view) {
                super(view);
                Log.d(TAG,"MyViewHolder");
                name = view.findViewById(R.id.dateoptionslistListTextView);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition(); // gets item position
                        Intent intent;
                        switch (position){
                            case 0:
                                intent = new Intent(ColorOptionsListActivity.this, TextColorOptionsActivity.class);
                                intent.putExtra("SettingsValue",getString(R.string.preference_main_color));
                                ColorOptionsListActivity.this.startActivity(intent);
                                break;
                            case 1:
                                intent = new Intent(ColorOptionsListActivity.this, TextColorOptionsActivity.class);
                                intent.putExtra("SettingsValue",getString(R.string.preference_main_color_ambient));
                                ColorOptionsListActivity.this.startActivity(intent);
                                break;
                            case 2:
                                intent = new Intent(ColorOptionsListActivity.this, TextColorOptionsActivity.class);
                                intent.putExtra("SettingsValue",getString(R.string.preference_secondary_color));
                                ColorOptionsListActivity.this.startActivity(intent);
                                break;
                            case 3:
                                intent = new Intent(ColorOptionsListActivity.this, TextColorOptionsActivity.class);
                                intent.putExtra("SettingsValue",getString(R.string.preference_secondary_color_ambient));
                                ColorOptionsListActivity.this.startActivity(intent);
                                break;
                            case 4:
                                intent = new Intent(ColorOptionsListActivity.this, ColorOptionsActivity.class);
                                ColorOptionsListActivity.this.startActivity(intent);
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
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.textview_item,parent,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Log.d(TAG,"MyViewHolder onBindViewHolder");
            ColorOption colorOption = values.get(position);
            holder.name.setText(colorOption.getName());

        }

        @Override
        public int getItemCount() {
            return values.size();
        }

    }

}
