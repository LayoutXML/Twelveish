/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 *
 */

package com.layoutxml.twelveish.activities.list_activities;

import android.app.Activity;
import android.content.Intent;
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

import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.activities.StringTextViewActivity;
import com.layoutxml.twelveish.objects.ActivityOption;

import java.util.ArrayList;
import java.util.List;

public class ActivityTextViewActivity extends Activity{

    private List<ActivityOption> values = new ArrayList<>();
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

        if (getIntent().getStringExtra("Activity").equals("ColorOptionsList"))
            generateColorOptionsListValues();
        else if (getIntent().getStringExtra("Activity").equals("DateOptionsList"))
            generateDateOptionsListValues();
    }

    private void generateColorOptionsListValues(){
        ActivityOption dateOption = new ActivityOption();
        dateOption.setName("Main text (active)");
        dateOption.setActivity(IntegerImageViewActivity.class);
        dateOption.setExtra("TextColor");
        dateOption.setExtra2(getString(R.string.preference_main_color));
        values.add(dateOption);

        dateOption = new ActivityOption();
        dateOption.setName("Main text (ambient)");
        dateOption.setActivity(IntegerImageViewActivity.class);
        dateOption.setExtra("TextColor");
        dateOption.setExtra2(getString(R.string.preference_main_color_ambient));
        values.add(dateOption);

        dateOption = new ActivityOption();
        dateOption.setName("Secondary text (active)");
        dateOption.setActivity(IntegerImageViewActivity.class);
        dateOption.setExtra("TextColor");
        dateOption.setExtra2(getString(R.string.preference_secondary_color));
        values.add(dateOption);

        dateOption = new ActivityOption();
        dateOption.setName("Secondary text (ambient)");
        dateOption.setActivity(IntegerImageViewActivity.class);
        dateOption.setExtra("TextColor");
        dateOption.setExtra2(getString(R.string.preference_secondary_color_ambient));
        values.add(dateOption);

        dateOption = new ActivityOption();
        dateOption.setName("Background (active)");
        dateOption.setActivity(IntegerImageViewActivity.class);
        dateOption.setExtra("BackgroundColor");
        dateOption.setExtra2(getString(R.string.preference_background_color));
        values.add(dateOption);

        mAdapter.notifyDataSetChanged();
    }

    private void generateDateOptionsListValues(){
        ActivityOption dateOption = new ActivityOption();
        dateOption.setName("Date order");
        dateOption.setActivity(IntegerTextViewOptionsActivity.class);
        dateOption.setExtra("DateOrder");
        values.add(dateOption);

        dateOption = new ActivityOption();
        dateOption.setName("Separator symbol");
        dateOption.setActivity(StringTextViewActivity.class);
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
                        Intent intent = new Intent(ActivityTextViewActivity.this, values.get(position).getActivity());
                        intent.putExtra("Activity",values.get(position).getExtra());
                        intent.putExtra("SettingsValue",values.get(position).getExtra2());
                        ActivityTextViewActivity.this.startActivity(intent);
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
            ActivityOption colorOption = values.get(position);
            holder.name.setText(colorOption.getName());

        }

        @Override
        public int getItemCount() {
            return values.size();
        }

    }

}
