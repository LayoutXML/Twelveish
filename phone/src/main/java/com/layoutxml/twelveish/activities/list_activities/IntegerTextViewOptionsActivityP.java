/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 *
 */

package com.layoutxml.twelveish.activities.list_activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.layoutxml.twelveish.MainActivity;
import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.objects.IntegerOptionP;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class IntegerTextViewOptionsActivityP extends Activity {

    private List<IntegerOptionP> values = new ArrayList<>();
    private CapitalisationAdapter mAdapter;
    private String preferencesKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        RecyclerView mRecyclerView = findViewById(R.id.menuList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new CapitalisationAdapter();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        if (getIntent().getStringExtra("Activity").equals("Capitalization")) {
            generateCapitalizationValues();
            preferencesKey = getString(R.string.preference_capitalisation);
        }
        else if (getIntent().getStringExtra("Activity").equals("DateOrder")) {
            generateDateOrderValues();
            preferencesKey = getString(R.string.preference_date_order);
        }
    }

    private void generateCapitalizationValues(){
        IntegerOptionP capitalisation = new IntegerOptionP();
        capitalisation.setName("All words title case");
        capitalisation.setInteger(0);
        values.add(capitalisation);

        capitalisation = new IntegerOptionP();
        capitalisation.setName("All uppercase");
        capitalisation.setInteger(1);
        values.add(capitalisation);

        capitalisation = new IntegerOptionP();
        capitalisation.setName("All lowercase");
        capitalisation.setInteger(2);
        values.add(capitalisation);

        capitalisation = new IntegerOptionP();
        capitalisation.setName("First word title case");
        capitalisation.setInteger(3);
        values.add(capitalisation);

        capitalisation = new IntegerOptionP();
        capitalisation.setName("First word in every\nline title case");
        capitalisation.setInteger(4);
        values.add(capitalisation);

        mAdapter.notifyDataSetChanged();
    }

    private void generateDateOrderValues() {
        IntegerOptionP dateOrder = new IntegerOptionP();
        dateOrder.setName("MDY");
        dateOrder.setInteger(0);
        values.add(dateOrder);

        dateOrder = new IntegerOptionP();
        dateOrder.setName("DMY");
        dateOrder.setInteger(1);
        values.add(dateOrder);

        dateOrder = new IntegerOptionP();
        dateOrder.setName("YMD");
        dateOrder.setInteger(2);
        values.add(dateOrder);

        dateOrder = new IntegerOptionP();
        dateOrder.setName("YDM");
        dateOrder.setInteger(3);
        values.add(dateOrder);
    }

    public class CapitalisationAdapter extends RecyclerView.Adapter<CapitalisationAdapter.MyViewHolder>{

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView name;

            MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.dateoptionslistListTextView);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition(); // gets item position
                        if (MainActivity.communicator!=null) {
                            MainActivity.communicator.sendPreference(preferencesKey, values.get(position).getInteger().toString(), "Integer", getApplicationContext());
                            Toast.makeText(getApplicationContext(), "Preference set", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                });
            }
        }

        @NonNull
        @Override
        public CapitalisationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.textview_item,parent,false);
            return new CapitalisationAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull CapitalisationAdapter.MyViewHolder holder, int position) {
            IntegerOptionP capitalisation = values.get(position);
            holder.name.setText(capitalisation.getName());
        }

        @Override
        public int getItemCount() {
            return values.size();
        }
    }

}
