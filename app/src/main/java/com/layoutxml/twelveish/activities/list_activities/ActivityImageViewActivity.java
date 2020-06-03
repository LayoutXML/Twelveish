/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 *
 */

package com.layoutxml.twelveish.activities.list_activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.activities.AboutActivity;
import com.layoutxml.twelveish.activities.FontSizeInfoActivity;
import com.layoutxml.twelveish.activities.ComplicationConfigActivity;
import com.layoutxml.twelveish.objects.ActivityOption;

import java.util.ArrayList;
import java.util.List;

public class ActivityImageViewActivity extends Activity {

    private List<ActivityOption> values = new ArrayList<>();
    private SettingsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wearablerecyclerview_activity);

        WearableRecyclerView mWearableRecyclerView = findViewById(R.id.wearable_recycler_view);
        mWearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);

        mAdapter = new SettingsAdapter();
        mWearableRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mWearableRecyclerView.setAdapter(mAdapter);
        generateValues();
    }

    private void generateValues(){
        ActivityOption activityOption = new ActivityOption();
        if (android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.N) {
            activityOption.setName("Complication");
            activityOption.setIcon(R.drawable.ic_complicationp);
            activityOption.setActivity(ComplicationConfigActivity.class);
            values.add(activityOption);
        }

        activityOption = new ActivityOption();
        activityOption.setName("Colors");
        activityOption.setIcon(R.drawable.ic_colorp);
        activityOption.setActivity(ActivityTextViewActivity.class);
        activityOption.setExtra("ColorOptionsList");
        values.add(activityOption);

        activityOption = new ActivityOption();
        activityOption.setName("Font");
        activityOption.setIcon(R.drawable.ic_fontp);
        activityOption.setActivity(FontTextViewActivity.class);
        values.add(activityOption);

        activityOption = new ActivityOption();
        activityOption.setName("Date format");
        activityOption.setIcon(R.drawable.ic_datep);
        activityOption.setActivity(ActivityTextViewActivity.class);
        activityOption.setExtra("DateOptionsList");
        values.add(activityOption);

        activityOption = new ActivityOption();
        activityOption.setName("Capitalisation");
        activityOption.setIcon(R.drawable.ic_capitalisationp);
        activityOption.setActivity(IntegerTextViewOptionsActivity.class);
        activityOption.setExtra("Capitalization");
        values.add(activityOption);

        activityOption = new ActivityOption();
        activityOption.setName("Show/hide elements");
        activityOption.setIcon(R.drawable.ic_showhidep);
        activityOption.setActivity(BooleanSwitcherActivity.class);
        activityOption.setExtra("ShowHide");
        values.add(activityOption);

        activityOption = new ActivityOption();
        activityOption.setName("Language");
        activityOption.setIcon(R.drawable.ic_languagep);
        activityOption.setActivity(StringTextViewActivity.class);
        activityOption.setExtra("Language");
        values.add(activityOption);

        activityOption = new ActivityOption();
        activityOption.setName("Text size offset");
        activityOption.setIcon(R.drawable.ic_capitalisationp);
        activityOption.setActivity(FontSizeInfoActivity.class);
        activityOption.setExtra("TextSize");
        values.add(activityOption);

        activityOption = new ActivityOption();
        activityOption.setName("Miscellaneous");
        activityOption.setIcon(R.drawable.ic_miscp);
        activityOption.setActivity(BooleanSwitcherActivity.class);
        activityOption.setExtra("MiscOptions");
        values.add(activityOption);

        activityOption = new ActivityOption();
        activityOption.setName("Info");
        activityOption.setIcon(R.drawable.ic_infop);
        activityOption.setActivity(AboutActivity.class);
        values.add(activityOption);

        mAdapter.notifyDataSetChanged();
    }

    public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.MyViewHolder>{

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView name;
            ImageView icon;

            MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.settingsListTextView);
                icon = view.findViewById(R.id.settingsListImagetView);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition(); // gets item position
                        Intent intent;
                        intent = new Intent(ActivityImageViewActivity.this, values.get(position).getActivity());
                        intent.putExtra("Activity",values.get(position).getExtra());
                        ActivityImageViewActivity.this.startActivity(intent);
                    }
                });
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.imageview_and_textview_item,parent,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            ActivityOption activityOption = values.get(position);
            holder.name.setText(activityOption.getName());
            holder.icon.setImageResource(activityOption.getIcon());

        }

        @Override
        public int getItemCount() {
            return values.size();
        }
    }

}