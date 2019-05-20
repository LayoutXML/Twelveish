/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 *
 */

package com.layoutxml.twelveish;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.wearable.Wearable;
import com.layoutxml.twelveish.objects.ActivityOptionP;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private List<ActivityOptionP> values = new ArrayList<>();
    private SettingsAdapter mAdapter;

    //Communication:
//    public static boolean isWatchConnected = false;
    private Communicator communicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        communicator = new Communicator(getApplicationContext());
        //communicator.initiateHandshake(getApplicationContext());
        //communicator.requestBooleanPreferences(getApplicationContext(),null);
//        Wearable.getDataClient(getApplicationContext()).addListener(communicator);

        RecyclerView mRecyclerView = findViewById(R.id.menuList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new SettingsAdapter();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        generateValues();
    }

    private void generateValues() {
//        ActivityOptionP activityOptionP = new ActivityOptionP();
//        activityOptionP.setName("Colors");
//        activityOptionP.setIcon(R.drawable.ic_colorp);
//        activityOptionP.setActivity(ActivityTextViewActivityP.class);
//        activityOptionP.setExtra("ColorOptionsList");
//        values.add(activityOptionP);
//
//        activityOptionP = new ActivityOptionP();
//        activityOptionP.setName("Font");
//        activityOptionP.setIcon(R.drawable.ic_fontp);
//        activityOptionP.setActivity(FontTextViewActivityP.class);
//        values.add(activityOptionP);
//
//        activityOptionP = new ActivityOptionP();
//        activityOptionP.setName("Date format");
//        activityOptionP.setIcon(R.drawable.ic_datep);
//        activityOptionP.setActivity(ActivityTextViewActivityP.class);
//        activityOptionP.setExtra("DateOptionsList");
//        values.add(activityOptionP);
//
//        activityOptionP = new ActivityOptionP();
//        activityOptionP.setName("Capitalisation");
//        activityOptionP.setIcon(R.drawable.ic_capitalisationp);
//        activityOptionP.setActivity(IntegerTextViewOptionsActivityP.class);
//        activityOptionP.setExtra("Capitalization");
//        values.add(activityOptionP);
//
//        activityOptionP = new ActivityOptionP();
//        activityOptionP.setName("Show/hide elements");
//        activityOptionP.setIcon(R.drawable.ic_showhidep);
//        activityOptionP.setActivity(BooleanSwitcherActivityP.class);
//        activityOptionP.setExtra("ShowHide");
//        values.add(activityOptionP);
//
//        activityOptionP = new ActivityOptionP();
//        activityOptionP.setName("Language");
//        activityOptionP.setIcon(R.drawable.ic_languagep);
//        activityOptionP.setActivity(StringTextViewActivityP.class);
//        activityOptionP.setExtra("Language");
//        values.add(activityOptionP);
////
////        activityOptionP = new ActivityOptionP();
////        activityOptionP.setName("Text size offset");
////        activityOptionP.setIcon(R.drawable.ic_capitalisation);
////        activityOptionP.setActivity(FontSizeInfoActivityP.class);
////        activityOptionP.setExtra("TextSize");
////        values.add(activityOptionP);
//
//        activityOptionP = new ActivityOptionP();
//        activityOptionP.setName("Miscellaneous");
//        activityOptionP.setIcon(R.drawable.ic_miscp);
//        activityOptionP.setActivity(BooleanSwitcherActivityP.class);
//        activityOptionP.setExtra("MiscOptions");
//        values.add(activityOptionP);
//
//        activityOptionP = new ActivityOptionP();
//        activityOptionP.setName("Info");
//        activityOptionP.setIcon(R.drawable.ic_infop);
//        activityOptionP.setActivity(AboutActivityP.class);
//        values.add(activityOptionP);

        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
                        intent = new Intent(MainActivity.this, values.get(position).getActivity());
                        intent.putExtra("Activity",values.get(position).getExtra());
                        intent.putExtra("SettingsValue",values.get(position).getExtra2());
                        MainActivity.this.startActivity(intent);
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
            ActivityOptionP activityOptionP = values.get(position);
            holder.name.setText(activityOptionP.getName());
            holder.icon.setImageResource(activityOptionP.getIcon());
        }

        @Override
        public int getItemCount() {
            return values.size();
        }
    }
}
