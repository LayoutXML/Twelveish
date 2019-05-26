package com.layoutxml.twelveish;

import android.content.Context;

import java.util.HashMap;

public class SettingsManager {

    public HashMap<String, Boolean> booleanHashmap;
    public HashMap<String, Integer> integerHashmap;
    public HashMap<String, String> stringHashmap;
    private Context context;

    public SettingsManager(Context context) {
        booleanHashmap = new HashMap<>();
        integerHashmap = new HashMap<>();
        stringHashmap = new HashMap<>();
        this.context = context;
    }

    public void initializeDefaultBooleans() {
        booleanHashmap.put(context.getResources().getString(R.string.preference_military_time),false);
        booleanHashmap.put(context.getResources().getString(R.string.preference_military_text_time),false);
        booleanHashmap.put(context.getResources().getString(R.string.preference_show_am_pm),true);
        booleanHashmap.put(context.getResources().getString(R.string.preference_show_digital_clock),true);
        booleanHashmap.put(context.getResources().getString(R.string.preference_show_digital_clock_ambient),true);
        booleanHashmap.put(context.getResources().getString(R.string.preference_show_calendar),true);
        booleanHashmap.put(context.getResources().getString(R.string.preference_show_calendar_ambient),true);
        booleanHashmap.put(context.getResources().getString(R.string.preference_show_suffixes),true);               //DEPRECATED
        booleanHashmap.put(context.getResources().getString(R.string.preference_show_battery),true);
        booleanHashmap.put(context.getResources().getString(R.string.preference_show_battery_ambient),true);
        booleanHashmap.put(context.getResources().getString(R.string.preference_show_day),true);
        booleanHashmap.put(context.getResources().getString(R.string.preference_show_day_ambient),true);
        booleanHashmap.put(context.getResources().getString(R.string.preference_show_words),true);                  //DEPRECATED
        booleanHashmap.put(context.getResources().getString(R.string.preference_show_words_ambient),true);
        booleanHashmap.put(context.getResources().getString(R.string.preference_show_seconds),true);
        booleanHashmap.put(context.getResources().getString(R.string.preference_show_complications),true);
        booleanHashmap.put(context.getResources().getString(R.string.preference_show_complications_ambient),true);
        booleanHashmap.put(context.getResources().getString(R.string.preference_tap_complications),false);
        booleanHashmap.put(context.getResources().getString(R.string.preference_legacy_word_arrangement),false);    //DEPRECATED
    }

    public void initializeDefaultIntegers() {
        integerHashmap.put(context.getResources().getString(R.string.preference_background_color),android.graphics.Color.parseColor("#000000"));
        integerHashmap.put(context.getResources().getString(R.string.preference_main_text_color),android.graphics.Color.parseColor("#ffffff"));
        integerHashmap.put(context.getResources().getString(R.string.preference_main_text_color_ambient),android.graphics.Color.parseColor("#ffffff"));
        integerHashmap.put(context.getResources().getString(R.string.preference_secondary_text_color),android.graphics.Color.parseColor("#ffffff"));
        integerHashmap.put(context.getResources().getString(R.string.preference_secondary_text_color_ambient),android.graphics.Color.parseColor("#ffffff"));
        integerHashmap.put(context.getResources().getString(R.string.preference_date_order),0);
        integerHashmap.put(context.getResources().getString(R.string.preference_capitalisation),0);
        integerHashmap.put(context.getResources().getString(R.string.main_text_size_offset),0);
        integerHashmap.put(context.getResources().getString(R.string.secondary_text_size_offset),0);
    }

    public void initializeDefaultStrings() {
        stringHashmap.put(context.getResources().getString(R.string.preference_date_separator),"/");
        stringHashmap.put(context.getResources().getString(R.string.preference_language),"en");
        stringHashmap.put(context.getResources().getString(R.string.preference_font),"robotolight");
        stringHashmap.put(context.getResources().getString(R.string.preference_font_secondary),"robotolight");
    }

}
