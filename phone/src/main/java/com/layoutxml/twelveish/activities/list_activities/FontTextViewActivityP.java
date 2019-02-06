
package com.layoutxml.twelveish.activities.list_activities;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.layoutxml.twelveish.MainActivity;
import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.objects.FontOptionP;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FontTextViewActivityP extends Activity{

    private List<FontOptionP> values = new ArrayList<>();
    private FontOptionsListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView mRecyclerView = findViewById(R.id.menuList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new FontOptionsListAdapter();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        generateValues();
    }

    private void generateValues(){
        FontOptionP option = new FontOptionP();
        option.setName("Roboto light (default)");
        option.setKey("robotolight");
        option.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        values.add(option);

        option = new FontOptionP();
        option.setName("Alegreya");
        option.setKey("alegreya");
        option.setTypeface(ResourcesCompat.getFont(this, R.font.alegreya));
        values.add(option);

        option = new FontOptionP();
        option.setName("Cabin");
        option.setKey("cabin");
        option.setTypeface(ResourcesCompat.getFont(this, R.font.cabin));
        values.add(option);

        option = new FontOptionP();
        option.setName("IBM Plex Sans");
        option.setKey("ibmplexsans");
        option.setTypeface(ResourcesCompat.getFont(this, R.font.ibmplexsans));
        values.add(option);

        option = new FontOptionP();
        option.setName("Inconsolata");
        option.setKey("inconsolata");
        option.setTypeface(ResourcesCompat.getFont(this, R.font.inconsolata));
        values.add(option);

        option = new FontOptionP();
        option.setName("Merriweather");
        option.setKey("merriweather");
        option.setTypeface(ResourcesCompat.getFont(this, R.font.merriweather));
        values.add(option);

        option = new FontOptionP();
        option.setName("Nunito");
        option.setKey("nunito");
        option.setTypeface(ResourcesCompat.getFont(this, R.font.nunito));
        values.add(option);

        option = new FontOptionP();
        option.setName("Pacifico");
        option.setKey("pacifico");
        option.setTypeface(ResourcesCompat.getFont(this, R.font.pacifico));
        values.add(option);

        option = new FontOptionP();
        option.setName("Quattrocento");
        option.setKey("quattrocento");
        option.setTypeface(ResourcesCompat.getFont(this, R.font.quattrocento));
        values.add(option);

        option = new FontOptionP();
        option.setName("Quicksand");
        option.setKey("quicksand");
        option.setTypeface(ResourcesCompat.getFont(this, R.font.quicksand));
        values.add(option);

        option = new FontOptionP();
        option.setName("Rubik");
        option.setKey("rubik");
        option.setTypeface(ResourcesCompat.getFont(this, R.font.rubik));
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
                        FontOptionP selectedMenuItem = values.get(position);
                        if (MainActivity.communicator!=null) {
                            MainActivity.communicator.sendPreference(getString(R.string.preference_font), selectedMenuItem.getKey(), "String", getApplicationContext());
                            Toast.makeText(getApplicationContext(), "Preference set", Toast.LENGTH_SHORT).show();
                        }
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
            FontOptionP fontOption = values.get(position);
            holder.name.setText(fontOption.getName());
            holder.name.setTypeface(fontOption.getTypeface());

        }

        @Override
        public int getItemCount() {
            return values.size();
        }

    }

}
