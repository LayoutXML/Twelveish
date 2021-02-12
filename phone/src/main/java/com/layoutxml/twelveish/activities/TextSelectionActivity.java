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
import com.layoutxml.twelveish.SettingsManager;
import com.layoutxml.twelveish.adapters.TextviewRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TextSelectionActivity extends AppCompatActivity implements TextviewRecyclerViewAdapter.ItemClickListener {

    private TextviewRecyclerViewAdapter adapter;
    private List<Pair<String, String>> settingOptions;
    private String settingsName = "languageSelectionList";
    private String[] availableLanguages;
    private int settingType;

    public static int LANGUAGE_SELECTION = 0;
    public static int CAPITALIZATION = 1;
    public static int FONT_SELECTION = 2;
    public static int DATE_ORDER = 3;
    public static int SEPARATOR_SYMBOL = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_selection_activity);

        settingType = getIntent().getIntExtra("SETTING_TYPE", 0);

        if(settingType == LANGUAGE_SELECTION){
            generateLanguageList();
        } else if(settingType == FONT_SELECTION){
            generateFontList();
        } else if(settingType == SEPARATOR_SYMBOL){
            generateSeparatorList();
        } else if(settingType == CAPITALIZATION){
            generateCapitalizationList();
        } else if(settingType == DATE_ORDER){
            generateDateOrderList();
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

    private void generateDateOrderList(){
        Calendar mCalendar = Calendar.getInstance();
        settingOptions = new ArrayList<>();

        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        int month = mCalendar.get(Calendar.MONTH) + 1;
        int year = mCalendar.get(Calendar.YEAR);

        String dateStrings[] = new String[4];
        dateStrings[0] = String.format(Locale.UK, "%02d" + "-" + "%02d" + "-" + "%04d", month, day, year);
        dateStrings[1] = String.format(Locale.UK, "%02d" + "-" + "%02d" + "-" + "%04d", day, month, year);
        dateStrings[2] = String.format(Locale.UK, "%04d" + "-" + "%02d" + "-" + "%02d", year, month, day);
        dateStrings[3] = String.format(Locale.UK, "%04d" + "-" + "%02d" + "-" + "%02d", year, day, month);

        settingOptions.add(new Pair<String, String>("Month-Day-Year", dateStrings[0]));
        settingOptions.add(new Pair<String, String>("Day-Month-Year", dateStrings[1]));
        settingOptions.add(new Pair<String, String>("Year-Month-Day", dateStrings[2]));
        settingOptions.add(new Pair<String, String>("Year-Day-Month", dateStrings[3]));
    }

    @Override
    public void onItemClick(View view, int position, String name) {
        Intent returnIntent = new Intent();

        if(settingType == LANGUAGE_SELECTION){
            String newLanguage = availableLanguages[position];
            returnIntent.putExtra("newLanguage", newLanguage);

        } else if(settingType == FONT_SELECTION){
            returnIntent.putExtra("newFont", settingOptions.get(position).second);
        } else if(settingType == SEPARATOR_SYMBOL){
            returnIntent.putExtra("newSeparator", settingOptions.get(position).second);
        } else if(settingType == CAPITALIZATION){
            returnIntent.putExtra("newCapitalization", position);
        } else if(settingType == DATE_ORDER){
            returnIntent.putExtra("newDateOrder", position);
        }

        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}