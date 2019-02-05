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
import com.layoutxml.twelveish.objects.StringOptionP;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StringTextViewActivityP extends Activity{

    private List<StringOptionP> values = new ArrayList<>();
    private DateSeparatorAdapter mAdapter;
    private String preferencesKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        RecyclerView mRecyclerView = findViewById(R.id.menuList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new DateSeparatorAdapter();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        if (getIntent().getStringExtra("Activity").equals("DateSeparator")) {
            generateDateSeparatorValues();
            preferencesKey = getString(R.string.preference_date_separator);
        } else if (getIntent().getStringExtra("Activity").equals("Language")) {
            generateLanguageValues();
            preferencesKey = getString(R.string.preference_language);
        }
    }

    private void generateLanguageValues(){
        StringOptionP option = new StringOptionP();
        option.setName("Dutch");
        option.setSymbol("nl");
        values.add(option);

        option = new StringOptionP();
        option.setName("English");
        option.setSymbol("en");
        values.add(option);

        option = new StringOptionP();
        option.setName("Finnish");
        option.setSymbol("fi");
        values.add(option);

        option = new StringOptionP();
        option.setName("German");
        option.setSymbol("de");
        values.add(option);

        option = new StringOptionP();
        option.setName("Greek");
        option.setSymbol("el");
        values.add(option);

        option = new StringOptionP();
        option.setName("Hungarian");
        option.setSymbol("hu");
        values.add(option);

        option = new StringOptionP();
        option.setName("Italian");
        option.setSymbol("it");
        values.add(option);

        option = new StringOptionP();
        option.setName("Lithuanian");
        option.setSymbol("lt");
        values.add(option);

        option = new StringOptionP();
        option.setName("Norwegian");
        option.setSymbol("no");
        values.add(option);

        option = new StringOptionP();
        option.setName("Russian");
        option.setSymbol("ru");
        values.add(option);

        option = new StringOptionP();
        option.setName("Spanish");
        option.setSymbol("es");
        values.add(option);

        mAdapter.notifyDataSetChanged();
    }

    private void generateDateSeparatorValues() {
        StringOptionP stringOption = new StringOptionP();
        stringOption.setName("Slash /");
        stringOption.setSymbol("/");
        values.add(stringOption);

        stringOption = new StringOptionP();
        stringOption.setName("Period .");
        stringOption.setSymbol(".");
        values.add(stringOption);

        stringOption = new StringOptionP();
        stringOption.setName("Hyphen -");
        stringOption.setSymbol("-");
        values.add(stringOption);

        stringOption = new StringOptionP();
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
                        StringOptionP selectedMenuItem = values.get(position);
                        if (MainActivity.communicator!=null) {
                            MainActivity.communicator.sendPreference(preferencesKey, selectedMenuItem.getSymbol(), "String", getApplicationContext());
                            Toast.makeText(getApplicationContext(), "Preference set", Toast.LENGTH_SHORT).show();
                        }
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
            StringOptionP stringOption = values.get(position);
            holder.name.setText(stringOption.getName());
        }

        @Override
        public int getItemCount() {
            return values.size();
        }
    }
}
