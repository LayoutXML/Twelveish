package com.layoutxml.twelveish.activities.list_activities;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.objects.FontOption;

import java.util.ArrayList;
import java.util.List;

public class FontTextViewActivity extends Activity{

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
        option.setName("Alegreya");
        option.setKey("alegreya");
        option.setTypeface(ResourcesCompat.getFont(this,R.font.alegreya));
        values.add(option);

        option = new FontOption();
        option.setName("Cabin");
        option.setKey("cabin");
        option.setTypeface(ResourcesCompat.getFont(this,R.font.cabin));
        values.add(option);

        option = new FontOption();
        option.setName("IBM Plex Sans");
        option.setKey("ibmplexsans");
        option.setTypeface(ResourcesCompat.getFont(this,R.font.ibmplexsans));
        values.add(option);

        option = new FontOption();
        option.setName("Inconsolata");
        option.setKey("inconsolata");
        option.setTypeface(ResourcesCompat.getFont(this,R.font.inconsolata));
        values.add(option);

        option = new FontOption();
        option.setName("Merriweather");
        option.setKey("merriweather");
        option.setTypeface(ResourcesCompat.getFont(this,R.font.merriweather));
        values.add(option);

        option = new FontOption();
        option.setName("Nunito");
        option.setKey("nunito");
        option.setTypeface(ResourcesCompat.getFont(this,R.font.nunito));
        values.add(option);

        option = new FontOption();
        option.setName("Pacifico");
        option.setKey("pacifico");
        option.setTypeface(ResourcesCompat.getFont(this,R.font.pacifico));
        values.add(option);

        option = new FontOption();
        option.setName("Quattrocento");
        option.setKey("quattrocento");
        option.setTypeface(ResourcesCompat.getFont(this,R.font.quattrocento));
        values.add(option);

        option = new FontOption();
        option.setName("Quicksand");
        option.setKey("quicksand");
        option.setTypeface(ResourcesCompat.getFont(this,R.font.quicksand));
        values.add(option);

        option = new FontOption();
        option.setName("Rubik");
        option.setKey("rubik");
        option.setTypeface(ResourcesCompat.getFont(this,R.font.rubik));
        values.add(option);

        mAdapter.notifyDataSetChanged();
    }

    public class FontOptionsListAdapter extends RecyclerView.Adapter<FontOptionsListAdapter.MyViewHolder>{

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView name;

            MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.dateoptionslistListTextView);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition(); // gets item position
                        FontOption selectedMenuItem = values.get(position);
                        prefs.edit().putString(getString(R.string.preference_font),selectedMenuItem.getKey()).apply();
                        Toast.makeText(getApplicationContext(), "Preference set", Toast.LENGTH_SHORT).show();
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
