/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 *
 */

package com.layoutxml.twelveish.activities.list_activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.enums.Capitalisation;
import com.layoutxml.twelveish.enums.DateOrder;
import com.layoutxml.twelveish.objects.IntegerOption;

import java.util.ArrayList;
import java.util.List;

public class IntegerTextViewOptionsActivity extends Activity {

    private List<IntegerOption> values = new ArrayList<>();
    private CapitalisationAdapter mAdapter;
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

        mAdapter = new CapitalisationAdapter();
        mWearableRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mWearableRecyclerView.setAdapter(mAdapter);

        if (getIntent().getStringExtra("Activity").equals("Capitalization")) {
            generateCapitalizationValues();
            preferencesKey = getString(R.string.preference_capitalisation);
        } else if (getIntent().getStringExtra("Activity").equals("DateOrder")) {
            generateDateOrderValues();
            preferencesKey = getString(R.string.preference_date_order);
        }
    }

    private void generateCapitalizationValues() {
        for (Capitalisation capitalisation: Capitalisation.values()) {
            IntegerOption capitalisationOption = new IntegerOption();
            capitalisationOption.setName(capitalisation.getLabel());
            capitalisationOption.setInteger(capitalisation.getIndex());
            values.add(capitalisationOption);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void generateDateOrderValues() {
        for (DateOrder order : DateOrder.values()) {
            IntegerOption dateOrderOption = new IntegerOption();
            dateOrderOption.setName(order.name());
            dateOrderOption.setInteger(order.getIndex());
            values.add(dateOrderOption);
        }
        mAdapter.notifyDataSetChanged();
    }

    public class CapitalisationAdapter extends RecyclerView.Adapter<CapitalisationAdapter.MyViewHolder> {

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView name;

            MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.dateoptionslistListTextView);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition(); // gets item position
                        prefs.edit().putInt(preferencesKey, values.get(position).getInteger()).apply();
                        Toast.makeText(getApplicationContext(), "Preference set", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }

        @NonNull
        @Override
        public CapitalisationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.textview_item, parent, false);
            return new CapitalisationAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull CapitalisationAdapter.MyViewHolder holder, int position) {
            IntegerOption capitalisation = values.get(position);
            holder.name.setText(capitalisation.getName());
        }

        @Override
        public int getItemCount() {
            return values.size();
        }
    }

}
