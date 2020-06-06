package com.layoutxml.twelveish;

import android.content.Context;
import android.content.SharedPreferences;

public class LanguageManager {
    private final Context context;
    private final SharedPreferences preferences;

    private String languageCode;
    private String[] prefixes;
    private String[] suffixes;
    private String[] weekdays;
    private String[] hours;
    private int[] timeShift;
    private boolean[] separatePrefix;
    private boolean[] separateSuffix;

    LanguageManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        loadPreferences();
    }

    public void loadPreferences() {
        languageCode = preferences.getString(context.getString(R.string.preference_language), "en");

        switch (languageCode) {
            case "nl":
                prefixes = context.getResources().getStringArray(R.array.PrefixesNL);
                suffixes = context.getResources().getStringArray(R.array.SuffixesNL);
                weekdays = context.getResources().getStringArray(R.array.WeekDaysNL);
                hours = context.getResources().getStringArray(R.array.ExactTimesNL);
                timeShift = new int[]{0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1};
                separatePrefix = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true};
                separateSuffix = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false};
                break;
            case "de":
                prefixes = context.getResources().getStringArray(R.array.PrefixesDE);
                suffixes = context.getResources().getStringArray(R.array.SuffixesDE);
                weekdays = context.getResources().getStringArray(R.array.WeekDaysDE);
                hours = context.getResources().getStringArray(R.array.ExactTimesDE);
                timeShift = new int[]{0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1};
                separatePrefix = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true};
                separateSuffix = new boolean[]{false, false, false, true, false, false, false, false, false, true, false, false};
                break;
            case "el":
                prefixes = context.getResources().getStringArray(R.array.PrefixesEL);
                suffixes = context.getResources().getStringArray(R.array.SuffixesEL);
                weekdays = context.getResources().getStringArray(R.array.WeekDaysEL);
                hours = context.getResources().getStringArray(R.array.ExactTimesEL);
                timeShift = new int[]{0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1};
                separatePrefix = new boolean[]{true, false, true, true, true, true, true, true, true, true, true, true};
                separateSuffix = new boolean[]{false, true, true, true, true, true, true, true, true, true, false, false};
                break;
            case "lt":
                prefixes = context.getResources().getStringArray(R.array.PrefixesLT);
                suffixes = context.getResources().getStringArray(R.array.SuffixesLT);
                weekdays = context.getResources().getStringArray(R.array.WeekDaysLT);
                hours = context.getResources().getStringArray(R.array.ExactTimesLT);
                timeShift = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1};
                separatePrefix = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true};
                separateSuffix = new boolean[]{false, false, true, true, true, true, true, true, false, false, false, false};
                break;
            case "fi":
                prefixes = context.getResources().getStringArray(R.array.PrefixesFI);
                suffixes = context.getResources().getStringArray(R.array.SuffixesFI);
                weekdays = context.getResources().getStringArray(R.array.WeekDaysFI);
                hours = context.getResources().getStringArray(R.array.ExactTimesFI);
                timeShift = new int[]{0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1};
                separatePrefix = new boolean[]{false, true, true, true, true, true, true, true, true, true, true, true};
                separateSuffix = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false};
                break;
            case "no":
                prefixes = context.getResources().getStringArray(R.array.PrefixesNO);
                suffixes = context.getResources().getStringArray(R.array.SuffixesNO);
                weekdays = context.getResources().getStringArray(R.array.WeekDaysNO);
                hours = context.getResources().getStringArray(R.array.ExactTimesNO);
                timeShift = new int[]{0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1};
                separatePrefix = new boolean[]{false, true, true, true, true, true, true, true, true, true, true, true};
                separateSuffix = new boolean[]{true, false, false, false, false, false, false, false, false, false, false, false};
                break;
            case "ru":
                prefixes = context.getResources().getStringArray(R.array.PrefixesRU);
                suffixes = context.getResources().getStringArray(R.array.SuffixesRU);
                weekdays = context.getResources().getStringArray(R.array.WeekDaysRU);
                hours = context.getResources().getStringArray(R.array.ExactTimesRU);
                timeShift = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1};
                separatePrefix = new boolean[]{false, false, true, false, true, false, false, false, true, true, true, false};
                separateSuffix = new boolean[]{false, true, true, true, true, true, true, true, false, false, false, false};
                break;
            case "hu":
                prefixes = context.getResources().getStringArray(R.array.PrefixesHU);
                suffixes = context.getResources().getStringArray(R.array.SuffixesHU);
                weekdays = context.getResources().getStringArray(R.array.WeekDaysHU);
                hours = context.getResources().getStringArray(R.array.ExactTimesHU);
                timeShift = new int[]{0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
                separatePrefix = new boolean[]{false, false, true, true, true, true, true, true, true, true, false, true};
                separateSuffix = new boolean[]{true, true, true, false, false, true, false, true, false, true, true, false};
                break;
            case "it":
                prefixes = context.getResources().getStringArray(R.array.PrefixesIT);
                suffixes = context.getResources().getStringArray(R.array.SuffixesIT);
                weekdays = context.getResources().getStringArray(R.array.WeekDaysIT);
                hours = context.getResources().getStringArray(R.array.ExactTimesIT);
                timeShift = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1};
                separatePrefix = new boolean[]{false, false, true, true, true, true, true, true, true, false, true, true};
                separateSuffix = new boolean[]{true, true, true, true, true, true, true, true, true, true, false, false};
                break;
            case "es":
                prefixes = context.getResources().getStringArray(R.array.PrefixesES);
                suffixes = context.getResources().getStringArray(R.array.SuffixesES);
                weekdays = context.getResources().getStringArray(R.array.WeekDaysES);
                hours = context.getResources().getStringArray(R.array.ExactTimesES);
                timeShift = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1};
                separatePrefix = new boolean[]{false, false, true, false, false, true, false, false, false, false, false, true};
                separateSuffix = new boolean[]{false, true, true, true, true, true, true, true, true, true, true, false};
                break;
            case "fr":
                prefixes = context.getResources().getStringArray(R.array.PrefixesFR);
                suffixes = context.getResources().getStringArray(R.array.SuffixesFR);
                weekdays = context.getResources().getStringArray(R.array.WeekDaysFR);
                hours = context.getResources().getStringArray(R.array.ExactTimesFR);
                timeShift = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1};
                separatePrefix = new boolean[]{false, true, false, false, false, false, false, true, false, false, false, false};
                separateSuffix = new boolean[]{false, false, true, true, true, true, true, true, true, true, true, true};
                break;
            case "pt":
                prefixes = context.getResources().getStringArray(R.array.PrefixesPT);
                suffixes = context.getResources().getStringArray(R.array.SuffixesPT);
                weekdays = context.getResources().getStringArray(R.array.WeekDaysPT);
                hours = context.getResources().getStringArray(R.array.ExactTimesPT);
                timeShift = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1};
                separatePrefix = new boolean[]{false, false, true, false, true, true, false, true, true, false, false, true};
                separateSuffix = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, false};
                break;
            case "sv":
                prefixes = context.getResources().getStringArray(R.array.PrefixesSV);
                suffixes = context.getResources().getStringArray(R.array.SuffixesSV);
                weekdays = context.getResources().getStringArray(R.array.WeekDaysSV);
                hours = context.getResources().getStringArray(R.array.ExactTimesSV);
                timeShift = new int[]{0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1};
                separatePrefix = new boolean[]{false, true, true, true, true, true, true, true, true, true, true, true};
                separateSuffix = new boolean[]{true, false, false, false, false, false, true, false, false, false, false, false};
                break;
            default:
                // en
                prefixes = context.getResources().getStringArray(R.array.Prefixes);
                suffixes = context.getResources().getStringArray(R.array.Suffixes);
                weekdays = context.getResources().getStringArray(R.array.WeekDays);
                hours = context.getResources().getStringArray(R.array.ExactTimes);
                timeShift = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1};
                separatePrefix = new boolean[]{false, false, true, true, true, true, true, true, true, true, true, true};
                separateSuffix = new boolean[]{false, true, false, true, false, false, false, true, false, true, false, false};
                break;
        }
    }

    public String getPrefix(int index) {
        return prefixes[index];
    }

    public String getSuffix(int index) {
        return suffixes[index];
    }

    public String getWeekday(int index) {
        return weekdays[index];
    }

    public String getHour(int index) {
        return hours[index];
    }

    public int getTimeShift(int index) {
        return timeShift[index];
    }

    public boolean getSeparatePrefix(int index) {
        return separatePrefix[index];
    }

    public boolean getSeparateSuffix(int index) {
        return separateSuffix[index];
    }
}
