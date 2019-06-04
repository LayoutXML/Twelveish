package com.layoutxml.twelveish.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.adapters.ImageRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ColorSelectionActivity extends Activity implements ImageRecyclerViewAdapter.ItemClickImageListener {

    private ImageRecyclerViewAdapter adapter;
    private String settingsName = "colorSelectionList";
    private List<Pair<String, Integer>> colorOptions;
    private static final String TAG = "ColorSelectionActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_selection_activity);

        colorOptions = new ArrayList<>();
        colorOptions.add(new Pair<String, Integer>("White", Color.parseColor("#ffffff")));
        colorOptions.add(new Pair<String, Integer>("Gray 400", Color.parseColor("#bdbdbd")));
        colorOptions.add(new Pair<String, Integer>("Gray 500", Color.parseColor("#9e9e9e")));
        colorOptions.add(new Pair<String, Integer>("Gray 600", Color.parseColor("#757575")));
        colorOptions.add(new Pair<String, Integer>("Gray 700", Color.parseColor("#616161")));
        colorOptions.add(new Pair<String, Integer>("Gray 800", Color.parseColor("#424242")));
        colorOptions.add(new Pair<String, Integer>("Gray 900", Color.parseColor("#212121")));
        colorOptions.add(new Pair<String, Integer>("Black", Color.parseColor("#000000")));
        colorOptions.add(new Pair<String, Integer>("Red", Color.parseColor("#ff0000")));
        colorOptions.add(new Pair<String, Integer>("Magenta", Color.parseColor("#ff00ff")));
        colorOptions.add(new Pair<String, Integer>("Yellow", Color.parseColor("#ffff00")));
        colorOptions.add(new Pair<String, Integer>("Green", Color.parseColor("#00ff00")));
        colorOptions.add(new Pair<String, Integer>("Cyan", Color.parseColor("#00ffff")));

        RecyclerView recyclerView = findViewById(R.id.colorList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ImageRecyclerViewAdapter(this,colorOptions, settingsName);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClickImage(View view, int position, Integer currentColor, String name) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("newColor",currentColor);
        setResult(Activity.RESULT_OK,returnIntent);
        Log.d(TAG, "onItemClickImage: result set");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
