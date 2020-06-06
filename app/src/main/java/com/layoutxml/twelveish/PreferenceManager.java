package com.layoutxml.twelveish;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;

import com.layoutxml.twelveish.enums.Capitalisation;
import com.layoutxml.twelveish.enums.DateOrder;

import java.util.ArrayList;

import lombok.Getter;

@Getter
public class PreferenceManager {
    private final Context context;
    private final SharedPreferences preferences;

    private int backgroundColor;
    private int mainTextColorActive;
    private int mainTextColorAmbient;
    private int secondaryTextColorActive;
    private int secondaryTextColorAmbient;
    private boolean MilitaryFormatDigital;
    private boolean MilitaryFormatText;
    private DateOrder dateOrder;
    private String dateSeparator;
    private Capitalisation capitalisation;
    private boolean showSecondaryTextActive;
    private boolean showSecondaryTextAmbient;
    private boolean showSecondaryCalendarActive;
    private boolean showSecondaryCalendarAmbient;
    private boolean showBatteryActive;
    private boolean showBatteryAmbient;
    private boolean showSeconds;
    private boolean showComplicationActive;
    private boolean showComplicationAmbient;
    private boolean showDayActive;
    private boolean showDayAmbient;
    private boolean disableComplicationTap;
    private int mainTextSizeOffset;
    private int secondaryTextSizeOffset;
    private String fontName;
    private boolean complicationLeftSet;
    private boolean complicationRightSet;
    private Typeface font;

    PreferenceManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        loadPreferences();
    }

    public void loadPreferences() {
        backgroundColor = preferences.getInt(context.getString(R.string.preference_background_color), android.graphics.Color.parseColor("#000000"));
        mainTextColorActive = preferences.getInt(context.getString(R.string.preference_main_color), android.graphics.Color.parseColor("#ffffff"));
        mainTextColorAmbient = preferences.getInt(context.getString(R.string.preference_main_color_ambient), android.graphics.Color.parseColor("#ffffff"));
        secondaryTextColorActive = preferences.getInt(context.getString(R.string.preference_secondary_color), android.graphics.Color.parseColor("#ffffff"));
        secondaryTextColorAmbient = preferences.getInt(context.getString(R.string.preference_secondary_color_ambient), android.graphics.Color.parseColor("#ffffff"));
        MilitaryFormatDigital = preferences.getBoolean(context.getString(R.string.preference_military_time), false);
        MilitaryFormatText = preferences.getBoolean(context.getString(R.string.preference_militarytext_time), false);
        dateOrder = DateOrder.values()[preferences.getInt(context.getString(R.string.preference_date_order), 0)];
        dateSeparator = preferences.getString(context.getString(R.string.preference_date_separator), "/");
        capitalisation = Capitalisation.values()[preferences.getInt(context.getString(R.string.preference_capitalisation), 0)];
        showSecondaryTextAmbient = preferences.getBoolean(context.getString(R.string.preference_show_secondary), true);
        showSecondaryTextActive = preferences.getBoolean(context.getString(R.string.preference_show_secondary_active), true);
        showSecondaryCalendarAmbient = preferences.getBoolean(context.getString(R.string.preference_show_secondary_calendar), true);
        showSecondaryCalendarActive = preferences.getBoolean(context.getString(R.string.preference_show_secondary_calendar_active), true);
        showBatteryActive = preferences.getBoolean(context.getString(R.string.preference_show_battery), true);
        showBatteryAmbient = preferences.getBoolean(context.getString(R.string.preference_show_battery_ambient), true);
        showDayActive = preferences.getBoolean(context.getString(R.string.preference_show_day), true);
        showDayAmbient = preferences.getBoolean(context.getString(R.string.preference_show_day_ambient), true);
        showSeconds = preferences.getBoolean(context.getString(R.string.preference_show_seconds), true);
        showComplicationActive = preferences.getBoolean(context.getString(R.string.preference_show_complications), true);
        showComplicationAmbient = preferences.getBoolean(context.getString(R.string.preference_show_complications_ambient), true);
        fontName = preferences.getString(context.getString(R.string.preference_font), "robotolight");
        disableComplicationTap = preferences.getBoolean(context.getString(R.string.preference_tap), false);
        complicationLeftSet = preferences.getBoolean(context.getString(R.string.complication_left_set), false);
        complicationRightSet = preferences.getBoolean(context.getString(R.string.complication_right_set), false);
        mainTextSizeOffset = preferences.getInt(context.getString(R.string.main_text_size_offset), 0);
        secondaryTextSizeOffset = preferences.getInt(context.getString(R.string.secondary_text_size_offset), 0);
        loadFont();
    }

    private void loadFont() {
        switch (fontName) {
            case "alegreya":
                font = ResourcesCompat.getFont(context, R.font.alegreya);
                break;
            case "cabin":
                font = ResourcesCompat.getFont(context, R.font.cabin);
                break;
            case "ibmplexsans":
                font = ResourcesCompat.getFont(context, R.font.ibmplexsans);
                break;
            case "inconsolata":
                font = ResourcesCompat.getFont(context, R.font.inconsolata);
                break;
            case "merriweather":
                font = ResourcesCompat.getFont(context, R.font.merriweather);
                break;
            case "nunito":
                font = ResourcesCompat.getFont(context, R.font.nunito);
                break;
            case "pacifico":
                font = ResourcesCompat.getFont(context, R.font.pacifico);
                break;
            case "quattrocento":
                font = ResourcesCompat.getFont(context, R.font.quattrocento);
                break;
            case "quicksand":
                font = ResourcesCompat.getFont(context, R.font.quicksand);
                break;
            case "rubik":
                font = ResourcesCompat.getFont(context, R.font.rubik);
                break;
            default:
                // robotolight
                font = Typeface.create("sans-serif-light", Typeface.NORMAL);
                break;
        }
    }

    public ArrayList<String> getPreferencesList() {
        ArrayList<String> preferences = new ArrayList<>();

        preferences.add("militaryTime");
        preferences.add(Boolean.toString(MilitaryFormatDigital));

        preferences.add("militaryTextTime");
        preferences.add(Boolean.toString(MilitaryFormatText));

        preferences.add("ampm");
        preferences.add(Boolean.toString(true)); // TODO: remove in favor of militaryTime

        preferences.add("showSecondary");
        preferences.add(Boolean.toString(showSecondaryTextAmbient));

        preferences.add("showSecondaryActive");
        preferences.add(Boolean.toString(showSecondaryTextActive));

        preferences.add("showSecondaryCalendar");
        preferences.add(Boolean.toString(showSecondaryCalendarAmbient));

        preferences.add("showSecondaryCalendarActive");
        preferences.add(Boolean.toString(showSecondaryCalendarActive));

        preferences.add("showSuffixes");
        preferences.add(Boolean.toString(true)); // TODO: consider removing altogether

        preferences.add("showBattery");
        preferences.add(Boolean.toString(showBatteryActive));

        preferences.add("showBatteryAmbient");
        preferences.add(Boolean.toString(showBatteryAmbient));

        preferences.add("showWords");
        preferences.add(Boolean.toString(true)); // TODO: consider removing altogether

        preferences.add("showWordsAmbient");
        preferences.add(Boolean.toString(true)); // TODO: consider removing altogether

        preferences.add("showSeconds");
        preferences.add(Boolean.toString(showSeconds));

        preferences.add("showComplication");
        preferences.add(Boolean.toString(showComplicationActive));

        preferences.add("showComplicationAmbient");
        preferences.add(Boolean.toString(showComplicationAmbient));

        preferences.add("showDay");
        preferences.add(Boolean.toString(showDayActive));

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
