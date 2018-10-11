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

public class LanguageOptionsActivity extends Activity {

    private List<StringOption> values = new ArrayList<>();
    private LanguageOptionsListAdapter mAdapter;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wearablerecyclerview_activity);

        prefs = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        WearableRecyclerView mWearableRecyclerView = findViewById(R.id.wearable_recycler_view);
        mWearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);

        mAdapter = new LanguageOptionsListAdapter();
        mWearableRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mWearableRecyclerView.setAdapter(mAdapter);
        generateValues();
    }

    private void generateValues(){
        StringOption option = new StringOption();
        option.setName("English");
        option.setSymbol("en");
        values.add(option);

        option = new StringOption();
        option.setName("Finish");
        option.setSymbol("fi");
        values.add(option);

        option = new StringOption();
        option.setName("German");
        option.setSymbol("de");
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
        option.setName("Russian");
        option.setSymbol("ru");
        values.add(option);

        mAdapter.notifyDataSetChanged();
    }

    public class LanguageOptionsListAdapter extends RecyclerView.Adapter<LanguageOptionsListAdapter.MyViewHolder>{

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
                        prefs.edit().putString(getString(R.string.preference_language),selectedMenuItem.getSymbol()).apply();
                        Toast.makeText(getApplicationContext(), "\""+selectedMenuItem.getName()+"\" set", Toast.LENGTH_SHORT).show();
                        finish();
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
            StringOption languageOption = values.get(position);
            holder.name.setText(languageOption.getName());

        }

        @Override
        public int getItemCount() {
            return values.size();
        }

    }

}
