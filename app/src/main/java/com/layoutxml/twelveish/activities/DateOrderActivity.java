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
import android.widget.TextView;
import android.widget.Toast;

import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.objects.IntegerOption;

import java.util.ArrayList;
import java.util.List;

public class DateOrderActivity extends Activity{

    private static final String TAG = "DateOrderActivity";
    private List<IntegerOption> values = new ArrayList<>();
    private DateOrderAdapter mAdapter;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wearablerecyclerview_activity);

        prefs = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        WearableRecyclerView mWearableRecyclerView = findViewById(R.id.wearable_recycler_view);
        mWearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);

        mAdapter = new DateOrderAdapter();
        mWearableRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mWearableRecyclerView.setAdapter(mAdapter);
        generateValues();
    }

    private void generateValues() {
        IntegerOption dateOrder = new IntegerOption();
        dateOrder.setName("MDY");
        dateOrder.setInteger(0);
        values.add(dateOrder);

        dateOrder = new IntegerOption();
        dateOrder.setName("DMY");
        dateOrder.setInteger(1);
        values.add(dateOrder);

        dateOrder = new IntegerOption();
        dateOrder.setName("YMD");
        dateOrder.setInteger(2);
        values.add(dateOrder);

        dateOrder = new IntegerOption();
        dateOrder.setName("YDM");
        dateOrder.setInteger(3);
        values.add(dateOrder);
    }

    public class DateOrderAdapter extends RecyclerView.Adapter<DateOrderAdapter.MyViewHolder>{

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
                        IntegerOption selectedMenuItem = values.get(position);
                        prefs.edit().putInt(getString(R.string.preference_date_order),values.get(position).getInteger()).apply();
                        Toast.makeText(getApplicationContext(), "\""+selectedMenuItem.getName()+"\" set", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }

        @NonNull
        @Override
        public DateOrderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d(TAG,"MyViewHolder onCreateViewHolder");
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.textview_item,parent,false);
            return new DateOrderAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull DateOrderAdapter.MyViewHolder holder, int position) {
            Log.d(TAG,"MyViewHolder onBindViewHolder");
            IntegerOption dateOrder = values.get(position);
            holder.name.setText(dateOrder.getName());
        }

        @Override
        public int getItemCount() {
            return values.size();
        }
    }

}
