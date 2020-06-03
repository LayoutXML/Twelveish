package com.layoutxml.twelveish;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

import lombok.Getter;

@Getter
public class PreferenceManager {
    private final Context context;
    private final SharedPreferences preferences;

    private int backgroundColor;
    private int mainColor;
    private int mainColorAmbient;
    private int secondaryColor;
    private int secondaryColorAmbient;
    private boolean militaryTime;
    private boolean militaryTextTime;
    private int dateOrder;
    private String dateSeparator;
    private int capitalisation;
    private boolean showSecondary;
    private boolean showSecondaryActive;
    private boolean showSecondaryCalendar;
    private boolean showSecondaryCalendarActive;
    private boolean showBattery;
    private boolean showBatteryAmbient;
    private boolean showSeconds;
    private boolean showComplication;
    private boolean showComplicationAmbient;
    private String language;
    private boolean showDay;
    private boolean showDayAmbient;
    private boolean disableComplicationTap;
    private int mainTextOffset;
    private int secondaryTextOffset;
    private String font;
    private boolean complicationLeftSet;
    private boolean complicationRightSet;

    PreferenceManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        loadPreferences();
    }

    public void loadPreferences() {
        backgroundColor = preferences.getInt(context.getString(R.string.preference_background_color), android.graphics.Color.parseColor("#000000"));
        mainColor = preferences.getInt(context.getString(R.string.preference_main_color), android.graphics.Color.parseColor("#ffffff"));
        mainColorAmbient = preferences.getInt(context.getString(R.string.preference_main_color_ambient), android.graphics.Color.parseColor("#ffffff"));
        secondaryColor = preferences.getInt(context.getString(R.string.preference_secondary_color), android.graphics.Color.parseColor("#ffffff"));
        secondaryColorAmbient = preferences.getInt(context.getString(R.string.preference_secondary_color_ambient), android.graphics.Color.parseColor("#ffffff"));
        militaryTime = preferences.getBoolean(context.getString(R.string.preference_military_time), false);
        militaryTextTime = preferences.getBoolean(context.getString(R.string.preference_militarytext_time), false);
        dateOrder = preferences.getInt(context.getString(R.string.preference_date_order), 0);
        dateSeparator = preferences.getString(context.getString(R.string.preference_date_separator), "/");
        capitalisation = preferences.getInt(context.getString(R.string.preference_capitalisation), 0);
        showSecondary = preferences.getBoolean(context.getString(R.string.preference_show_secondary), true);
        showSecondaryActive = preferences.getBoolean(context.getString(R.string.preference_show_secondary_active), true);
        showSecondaryCalendar = preferences.getBoolean(context.getString(R.string.preference_show_secondary_calendar), true);
        showSecondaryCalendarActive = preferences.getBoolean(context.getString(R.string.preference_show_secondary_calendar_active), true);
        showBattery = preferences.getBoolean(context.getString(R.string.preference_show_battery), true);
        showBatteryAmbient = preferences.getBoolean(context.getString(R.string.preference_show_battery_ambient), true);
        showDay = preferences.getBoolean(context.getString(R.string.preference_show_day), true);
        showDayAmbient = preferences.getBoolean(context.getString(R.string.preference_show_day_ambient), true);
        showSeconds = preferences.getBoolean(context.getString(R.string.preference_show_seconds), true);
        showComplication = preferences.getBoolean(context.getString(R.string.preference_show_complications), true);
        showComplicationAmbient = preferences.getBoolean(context.getString(R.string.preference_show_complications_ambient), true);
        language = preferences.getString(context.getString(R.string.preference_language), "en");
        font = preferences.getString(context.getString(R.string.preference_font), "robotolight");
        disableComplicationTap = preferences.getBoolean(context.getString(R.string.preference_tap), false);
        complicationLeftSet = preferences.getBoolean(context.getString(R.string.complication_left_set), false);
        complicationRightSet = preferences.getBoolean(context.getString(R.string.complication_right_set), false);
        mainTextOffset = preferences.getInt(context.getString(R.string.main_text_size_offset), 0);
        secondaryTextOffset = preferences.getInt(context.getString(R.string.secondary_text_size_offset), 0);
    }

    public ArrayList<String> getPreferencesList() {
        ArrayList<String> preferences = new ArrayList<>();

        preferences.add("militaryTime");
        preferences.add(Boolean.toString(militaryTime));

        preferences.add("militaryTextTime");
        preferences.add(Boolean.toString(militaryTextTime));

        preferences.add("ampm");
        preferences.add(Boolean.toString(true)); // TODO: remove in favor of militaryTime

        preferences.add("showSecondary");
        preferences.add(Boolean.toString(showSecondary));

        preferences.add("showSecondaryActive");
        preferences.add(Boolean.toString(showSecondaryActive));

        preferences.add("showSecondaryCalendar");
        preferences.add(Boolean.toString(showSecondaryCalendar));

        preferences.add("showSecondaryCalendarActive");
        preferences.add(Boolean.toString(showSecondaryCalendarActive));

        preferences.add("showSuffixes");
        preferences.add(Boolean.toString(true)); // TODO: consider removing altogether

        preferences.add("showBattery");
        preferences.add(Boolean.toString(showBattery));

        preferences.add("showBatteryAmbient");
        preferences.add(Boolean.toString(showBatteryAmbient));

        preferences.add("showWords");
        preferences.add(Boolean.toString(true)); // TODO: consider removing altogether

        preferences.add("showWordsAmbient");
        preferences.add(Boolean.toString(true)); // TODO: consider removing altogether

        preferences.add("showSeconds");
        preferences.add(Boolean.toString(showSeconds));

        preferences.add("showComplication");
        preferences.add(Boolean.toString(showComplication));

        preferences.add("showComplicationAmbient");
        preferences.add(Boolean.toString(showComplicationAmbient));

        preferences.add("showDay");
        preferences.add(Boolean.toString(showDay));

        preferences.add("showDayAmbient");
        preferences.add(Boolean.toString(showDayAmbient));

        preferences.add("disableComplicationTap");
        preferences.add(Boolean.toString(disableComplicationTap));

        preferences.add("legacyWords");
        preferences.add(Boolean.toString(false)); // TODO: remove altogether

        return preferences;
    }

    public void saveString(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    public void saveInt(String key, int value) {
        preferences.edit().putInt(key, value).apply();
    }

    public void saveBoolean(String key, Boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }
}
