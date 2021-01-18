package com.layoutxml.twelveish.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.adapters.TextviewRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TextSelectionActivity extends AppCompatActivity implements TextviewRecyclerViewAdapter.ItemClickListener {

    private TextviewRecyclerViewAdapter adapter;
    private List<Pair<String, String>> settingOptions;
    private String settingsName = "languageSelectionList";
    private String[] availableLanguages;
    private String settingType = "";

    public static String LANGUAGE_SELECTION = "SELECT_LANGUAGE";
    public static String FONT_SELECTION = "SELECT_FONT";
    public static String SEPARATOR_SYMBOL = "SELECT_SEPARATOR";
    public static String CAPITALIZATION = "CAPITALIZATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_selection_activity);

        settingType = getIntent().getStringExtra("SETTING_TYPE");

        if(settingType.equals(LANGUAGE_SELECTION)){
            generateLanguageList();
        } else if(settingType.equals(FONT_SELECTION)){
            generateFontList();
        } else if(settingType.equals(SEPARATOR_SYMBOL)){
            generateSeparatorList();
        } else if(settingType.equals(CAPITALIZATION)){
            generateCapitalizationList();
        }

        RecyclerView recyclerView = findViewById(R.id.colorList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TextviewRecyclerViewAdapter(this, settingOptions, settingsName);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void generateFontList() {
        settingOptions = new ArrayList<>();
        settingOptions.add(new Pair<String, String>("Roboto Light", "robotolight"));
        settingOptions.add(new Pair<String, String>("Alegreya", "alegreya"));
        settingOptions.add(new Pair<String, String>("Cabin", "cabin"));
        settingOptions.add(new Pair<String, String>("IBMplex Sans", "ibmplexsans"));
        settingOptions.add(new Pair<String, String>("Inconsolata", "inconsolata"));
        settingOptions.add(new Pair<String, String>("Merriweather", "merriweather"));
        settingOptions.add(new Pair<String, String>("Nunito", "nunito"));
        settingOptions.add(new Pair<String, String>("Pacifico", "pacifico"));
        settingOptions.add(new Pair<String, String>("Quattro Cento", "quattrocento"));

    }

    private void generateSeparatorList() {
        settingOptions = new ArrayList<>();
        settingOptions.add(new Pair<String, String>("Slash", "/"));
        settingOptions.add(new Pair<String, String>("Period", "."));
        settingOptions.add(new Pair<String, String>("Hyphen", "-"));
        settingOptions.add(new Pair<String, String>("Space", " "));
    }

    private void generateCapitalizationList(){
        settingOptions = new ArrayList<>();
        settingOptions.add(new Pair<String, String>("All words title case", "The Quick Brown Fox Jumped Over The Lazy Dog"));
        settingOptions.add(new Pair<String, String>("All uppercase","THE QUICK BROWN FOX JUMPED OVER THE LAZY DOG"));
        settingOptions.add(new Pair<String, String>("All lowercase","the quick brown fox jumped over the lazy dog"));
        settingOptions.add(new Pair<String, String>("First word title case","The quick brown fox jumped over the lazy dog"));
        settingOptions.add(new Pair<String, String>("First word in every line title case","The quick brown fox jumped over the lazy dog"));
    }

    private void generateLanguageList(){
        availableLanguages = getResources().getStringArray(R.array.AvailableLanguages);
        settingOptions = new ArrayList<>();
        for(String lang : availableLanguages){
            Locale mLocale = new Locale(lang);
            settingOptions.add(new Pair<String, String>(mLocale.getDisplayLanguage(mLocale), mLocale.getDisplayLanguage(new Locale("en"))));
        }
    }

    @Override
    public void onItemClick(View view, int position, String name) {
        Intent returnIntent = new Intent();

        if(settingType.equals(LANGUAGE_SELECTION)){
            String newLanguage = availableLanguages[position];
            returnIntent.putExtra("newLanguage", newLanguage);
        } else if(settingType.equals(FONT_SELECTION)){
            returnIntent.putExtra("newFont", settingOptions.get(position).second);
        } else if(settingType.equals(SEPARATOR_SYMBOL)){
            returnIntent.putExtra("newSeparator", settingOptions.get(position).second);
        } else if(settingType.equals(CAPITALIZATION)){
            returnIntent.putExtra("newCapitalization", position);
        }

        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}