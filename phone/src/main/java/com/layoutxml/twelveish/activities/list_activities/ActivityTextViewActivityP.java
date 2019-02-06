/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 *
 */

package com.layoutxml.twelveish.activities.list_activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.objects.ActivityOptionP;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ActivityTextViewActivityP extends Activity{

    private List<ActivityOptionP> values = new ArrayList<>();
    private ColorOptionsListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView mRecyclerView = findViewById(R.id.menuList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ColorOptionsListAdapter();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        if (getIntent().getStringExtra("Activity").equals("ColorOptionsList"))
            generateColorOptionsListValues();
        else if (getIntent().getStringExtra("Activity").equals("DateOptionsList"))
            generateDateOptionsListValues();
    }

    private void generateColorOptionsListValues(){
        ActivityOptionP dateOption = new ActivityOptionP();
        dateOption.setName("Main text (active)");
        dateOption.setActivity(IntegerImageViewActivityP.class);
        dateOption.setExtra("TextColor");
        dateOption.setExtra2(getString(R.string.preference_main_color));
        values.add(dateOption);

        dateOption = new ActivityOptionP();
        dateOption.setName("Main text (ambient)");
        dateOption.setActivity(IntegerImageViewActivityP.class);
        dateOption.setExtra("TextColor");
        dateOption.setExtra2(getString(R.string.preference_main_color_ambient));
        values.add(dateOption);

        dateOption = new ActivityOptionP();
        dateOption.setName("Secondary text (active)");
        dateOption.setActivity(IntegerImageViewActivityP.class);
        dateOption.setExtra("TextColor");
        dateOption.setExtra2(getString(R.string.preference_secondary_color));
        values.add(dateOption);

        dateOption = new ActivityOptionP();
        dateOption.setName("Secondary text (ambient)");
        dateOption.setActivity(IntegerImageViewActivityP.class);
        dateOption.setExtra("TextColor");
        dateOption.setExtra2(getString(R.string.preference_secondary_color_ambient));
        values.add(dateOption);

        dateOption = new ActivityOptionP();
        dateOption.setName("Background (active)");
        dateOption.setActivity(IntegerImageViewActivityP.class);
        dateOption.setExtra("BackgroundColor");
        dateOption.setExtra2(getString(R.string.preference_background_color));
        values.add(dateOption);

        mAdapter.notifyDataSetChanged();
    }

    private void generateDateOptionsListValues(){
        ActivityOptionP dateOption = new ActivityOptionP();
        dateOption.setName("Date order");
        dateOption.setActivity(IntegerTextViewOptionsActivityP.class);
        dateOption.setExtra("DateOrder");
        values.add(dateOption);

        dateOption = new ActivityOptionP();
        dateOption.setName("Separator symbol");
        dateOption.setActivity(StringTextViewActivityP.class);
        dateOption.setExtra("DateSeparator");
        values.add(dateOption);

        mAdapter.notifyDataSetChanged();
    }

    public class ColorOptionsListAdapter extends RecyclerView.Adapter<ColorOptionsListAdapter.MyViewHolder>{

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView name;

            MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.dateoptionslistListTextView);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition(); // gets item position
                        Intent intent = new Intent(ActivityTextViewActivityP.this, values.get(position).getActivity());
                        intent.putExtra("Activity",values.get(position).getExtra());
                        intent.putExtra("SettingsValue",values.get(position).getExtra2());
                        ActivityTextViewActivityP.this.startActivity(intent);
                    }
                });
            }

        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.textview_item,parent,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            ActivityOptionP colorOption = values.get(position);
            holder.name.setText(colorOption.getName());

        }

        @Override
        public int getItemCount() {
            return values.size();
        }

    }

}
