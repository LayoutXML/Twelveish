package com.layoutxml.twelveish.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
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
import com.layoutxml.twelveish.objects.FontOption;

import java.util.ArrayList;
import java.util.List;

public class FontOptionsActivity extends Activity{

    private static final String TAG = "FontOptionsActivity";
    private List<FontOption> values = new ArrayList<>();
    private FontOptionsListAdapter mAdapter;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wearablerecyclerview_activity);

        prefs = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        WearableRecyclerView mWearableRecyclerView = findViewById(R.id.wearable_recycler_view);
        mWearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);

        mAdapter = new FontOptionsListAdapter();
        mWearableRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mWearableRecyclerView.setAdapter(mAdapter);
        generateValues();
    }

    private void generateValues(){
        FontOption option = new FontOption();
        option.setName("Roboto light (default)");
        option.setKey("robotolight");
        option.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        values.add(option);

        option = new FontOption();
        option.setName("Allura");
        option.setKey("allura");
        option.setTypeface(ResourcesCompat.getFont(this,R.font.allura));
        values.add(option);

        option = new FontOption();
        option.setName("Divlit");
        option.setKey("divlit");
        option.setTypeface(ResourcesCompat.getFont(this,R.font.divlit));
        values.add(option);

        option = new FontOption();
        option.setName("Halo 3");
        option.setKey("halo3");
        option.setTypeface(ResourcesCompat.getFont(this,R.font.halo3));
        values.add(option);

        option = new FontOption();
        option.setName("Homoarakhn");
        option.setKey("homoarakhn");
        option.setTypeface(ResourcesCompat.getFont(this,R.font.homoarakhn));
        values.add(option);

        mAdapter.notifyDataSetChanged();
    }

    public class FontOptionsListAdapter extends RecyclerView.Adapter<FontOptionsListAdapter.MyViewHolder>{

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
                        FontOption selectedMenuItem = values.get(position);
                        prefs.edit().putString(getString(R.string.preference_font),selectedMenuItem.getKey()).apply();
                        Toast.makeText(getApplicationContext(), "\""+selectedMenuItem.getName()+"\" set", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d(TAG,"MyViewHolder onCreateViewHolder");
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.textview_item,parent,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Log.d(TAG,"MyViewHolder onBindViewHolder");
            FontOption fontOption = values.get(position);
            holder.name.setText(fontOption.getName());
            holder.name.setTypeface(fontOption.getTypeface());

        }

        @Override
        public int getItemCount() {
            return values.size();
        }

    }

}
