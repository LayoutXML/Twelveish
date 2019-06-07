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
        //white-black
        colorOptions.add(new Pair<String, Integer>("White", Color.parseColor("#ffffff")));
        colorOptions.add(new Pair<String, Integer>("Gray 400", Color.parseColor("#bdbdbd")));
        colorOptions.add(new Pair<String, Integer>("Gray 500", Color.parseColor("#9e9e9e")));
        colorOptions.add(new Pair<String, Integer>("Material Blue Gray", Color.parseColor("#607D8B")));
        colorOptions.add(new Pair<String, Integer>("Gray 600", Color.parseColor("#757575")));
        colorOptions.add(new Pair<String, Integer>("Gray 700", Color.parseColor("#616161")));
        colorOptions.add(new Pair<String, Integer>("Gray 800", Color.parseColor("#424242")));
        colorOptions.add(new Pair<String, Integer>("Gray 900", Color.parseColor("#212121")));
        colorOptions.add(new Pair<String, Integer>("Black", Color.parseColor("#000000")));
        colorOptions.add(new Pair<String, Integer>("Material Red", Color.parseColor("#F44336")));
        colorOptions.add(new Pair<String, Integer>("Vibrant Red", Color.parseColor("#FF1744")));
        colorOptions.add(new Pair<String, Integer>("Red", Color.parseColor("#ff0000")));
        colorOptions.add(new Pair<String, Integer>("Material Pink", Color.parseColor("#E91E63")));
        colorOptions.add(new Pair<String, Integer>("Vibrant Pink", Color.parseColor("#F50057")));
        colorOptions.add(new Pair<String, Integer>("Material Purple", Color.parseColor("#9C27B0")));
        colorOptions.add(new Pair<String, Integer>("Vibrant Purple", Color.parseColor("#D500F9")));
        colorOptions.add(new Pair<String, Integer>("Magenta", Color.parseColor("#ff00ff")));
        colorOptions.add(new Pair<String, Integer>("Material Deep Purple", Color.parseColor("#673AB7")));
        colorOptions.add(new Pair<String, Integer>("Vibrant Deep Purple", Color.parseColor("#651FFF")));
        colorOptions.add(new Pair<String, Integer>("Material Indigo", Color.parseColor("#3F51B5")));
        colorOptions.add(new Pair<String, Integer>("Vibrant Indigo", Color.parseColor("#3D5AFE")));
        colorOptions.add(new Pair<String, Integer>("Blue", Color.parseColor("#0000ff")));
        colorOptions.add(new Pair<String, Integer>("Material Blue", Color.parseColor("#2196F3")));
        colorOptions.add(new Pair<String, Integer>("Vibrant Blue", Color.parseColor("#2979FF")));
        colorOptions.add(new Pair<String, Integer>("Material Light Blue", Color.parseColor("#03A9F4")));
        colorOptions.add(new Pair<String, Integer>("Vibrant Light Blue", Color.parseColor("#00B0FF")));
        colorOptions.add(new Pair<String, Integer>("Material Cyan", Color.parseColor("#00BCD4")));
        colorOptions.add(new Pair<String, Integer>("Vibrant Cyan", Color.parseColor("#00E5FF")));
        colorOptions.add(new Pair<String, Integer>("Cyan", Color.parseColor("#00ffff")));
        colorOptions.add(new Pair<String, Integer>("Material Teal", Color.parseColor("#009688")));
        colorOptions.add(new Pair<String, Integer>("Vibrant Teal", Color.parseColor("#1DE9B6")));
        colorOptions.add(new Pair<String, Integer>("Material Green", Color.parseColor("#4CAF50")));
        colorOptions.add(new Pair<String, Integer>("Vibrant Green", Color.parseColor("#00E676")));
        colorOptions.add(new Pair<String, Integer>("Green", Color.parseColor("#00ff00")));
        colorOptions.add(new Pair<String, Integer>("Material Light Green", Color.parseColor("#8BC34A")));
        colorOptions.add(new Pair<String, Integer>("Vibrant Light Green", Color.parseColor("#76FF03")));
        colorOptions.add(new Pair<String, Integer>("Material Lime", Color.parseColor("#CDDC39")));
        colorOptions.add(new Pair<String, Integer>("Vibrant Lime", Color.parseColor("#C6FF00")));
        colorOptions.add(new Pair<String, Integer>("Yellow", Color.parseColor("#ffff00")));
        colorOptions.add(new Pair<String, Integer>("Material Yellow", Color.parseColor("#FFEB3B")));
        colorOptions.add(new Pair<String, Integer>("Vibrant Yellow", Color.parseColor("#FFEA00")));
        colorOptions.add(new Pair<String, Integer>("Material Amber", Color.parseColor("#FFC107")));
        colorOptions.add(new Pair<String, Integer>("Vibrant Amber", Color.parseColor("#FFC400")));
        colorOptions.add(new Pair<String, Integer>("Material Orange", Color.parseColor("#FF9800")));
        colorOptions.add(new Pair<String, Integer>("Vibrant Orange", Color.parseColor("#FF9100")));
        colorOptions.add(new Pair<String, Integer>("Material Deep Orange", Color.parseColor("#FF5722")));
        colorOptions.add(new Pair<String, Integer>("Vibrant Deep Orange", Color.parseColor("#FF3D00")));
        colorOptions.add(new Pair<String, Integer>("Material Brown", Color.parseColor("#795548")));

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
