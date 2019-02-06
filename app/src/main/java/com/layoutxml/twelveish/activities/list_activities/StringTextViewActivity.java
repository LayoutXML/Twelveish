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
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.objects.StringOption;

import java.util.ArrayList;
import java.util.List;

public class StringTextViewActivity extends Activity{

    private List<StringOption> values = new ArrayList<>();
    private DateSeparatorAdapter mAdapter;
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

        mAdapter = new DateSeparatorAdapter();
        mWearableRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mWearableRecyclerView.setAdapter(mAdapter);

        if (getIntent().getStringExtra("Activity").equals("DateSeparator")) {
            generateDateSeparatorValues();
            preferencesKey = getString(R.string.preference_date_separator);
        } else if (getIntent().getStringExtra("Activity").equals("Language")) {
            generateLanguageValues();
            preferencesKey = getString(R.string.preference_language);
        }
    }

    private void generateLanguageValues(){
        StringOption option = new StringOption();
        option.setName("Dutch");
        option.setSymbol("nl");
        values.add(option);

        option = new StringOption();
        option.setName("English");
        option.setSymbol("en");
        values.add(option);

        option = new StringOption();
        option.setName("Finnish");
        option.setSymbol("fi");
        values.add(option);

        option = new StringOption();
        option.setName("French");
        option.setSymbol("fr");
        values.add(option);

        option = new StringOption();
        option.setName("German");
        option.setSymbol("de");
        values.add(option);

        option = new StringOption();
        option.setName("Greek");
        option.setSymbol("el");
        values.add(option);

        option = new StringOption();
        option.setName("Hungarian");
        option.setSymbol("hu");
        values.add(option);

        option = new StringOption();
        option.setName("Italian");
        option.setSymbol("it");
        values.add(option);

        option = new StringOption();
        option.setName("Lithuanian");
        option.setSymbol("lt");
        values.add(option);

        option = new StringOption();
        option.setName("Norwegian");
        option.setSymbol("no");
        values.add(option);

        option = new StringOption();
        option.setName("Portuguese");
        option.setSymbol("pt");
        values.add(option);

        option = new StringOption();
        option.setName("Russian");
        option.setSymbol("ru");
        values.add(option);

        option = new StringOption();
        option.setName("Spanish");
        option.setSymbol("es");
        values.add(option);

        mAdapter.notifyDataSetChanged();
    }

    private void generateDateSeparatorValues() {
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
                        prefs.edit().putString(preferencesKey,selectedMenuItem.getSymbol()).apply();
                        Toast.makeText(getApplicationContext(), "Preference set", Toast.LENGTH_SHORT).show();
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
