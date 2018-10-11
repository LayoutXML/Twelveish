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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.objects.StringOption;

import java.util.ArrayList;
import java.util.List;

public class DateSeparatorActivity extends Activity{

    List<StringOption> values = new ArrayList<>();
    private DateSeparatorAdapter mAdapter;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wearablerecyclerview_activity);

        prefs = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        WearableRecyclerView mWearableRecyclerView = findViewById(R.id.wearable_recycler_view);
        mWearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);

        mAdapter = new DateSeparatorAdapter();
        mWearableRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mWearableRecyclerView.setAdapter(mAdapter);
        generateValues();
    }

    private void generateValues() {
        StringOption stringOption = new StringOption();
        stringOption.setName("Slash /");
        stringOption.setSymbol("/");
        values.add(stringOption);

        stringOption = new StringOption();
        stringOption.setName("Period .");
        stringOption.setSymbol(".");
        values.add(stringOption);

        stringOption = new StringOption();
        stringOption.setName("Hyphen -");
        stringOption.setSymbol("-");
        values.add(stringOption);

        stringOption = new StringOption();
        stringOption.setName("Space");
        stringOption.setSymbol(" ");
        values.add(stringOption);
    }

    public class DateSeparatorAdapter extends RecyclerView.Adapter<DateSeparatorAdapter.MyViewHolder>{

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView name;

            MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.dateoptionslistListTextView);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition(); // gets item position
                        StringOption selectedMenuItem = values.get(position);
                        prefs.edit().putString(getString(R.string.preference_date_separator),selectedMenuItem.getSymbol()).apply();
                        Toast.makeText(getApplicationContext(), "\""+selectedMenuItem.getName()+"\" set", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }

        @NonNull
        @Override
        public DateSeparatorAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.textview_item,parent,false);
            return new DateSeparatorAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull DateSeparatorAdapter.MyViewHolder holder, int position) {
            StringOption stringOption = values.get(position);
            holder.name.setText(stringOption.getName());
        }

        @Override
        public int getItemCount() {
            return values.size();
        }
    }
}
