package com.layoutxml.twelveish.objects;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.layoutxml.twelveish.Communicator;
import com.layoutxml.twelveish.CustomizationScreen;
import com.layoutxml.twelveish.HomeScreen;
import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.SettingsManager;
import com.layoutxml.twelveish.WordClockListener;
import com.layoutxml.twelveish.WordClockTask;
import com.layoutxml.twelveish.dagger.App;
import com.layoutxml.twelveish.dagger.DaggerSettingsManagerComponent;
import com.layoutxml.twelveish.dagger.SettingsManagerComponent;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class WatchPreviewView extends View implements WordClockListener {

    private int secondaryTextOffset = 0;
    private int mainTextOffset = 0;
    private int height = 0;
    private Paint paint;
    private Paint paintFrame;
    private SettingsManager settingsManager;
    private boolean mAmbient = false;
    private Paint mTextPaint;
    private Paint mTextPaint2;
    private Typeface NORMAL_TYPEFACE = Typeface.create("sans-serif-light", Typeface.NORMAL);
    private boolean significantTimeChange = false;
    private Calendar mCalendar;
    private int lastSignificantMinutes = -1;
    private int lastSignificantHours = -1;
    private String dayOfTheWeek = "";
    private String text3 = "";
    private String text1 = "";
    private String text1backup = "";
    private String text2 = "";
    private float basey = -1;
    private float x;
    private String[] Prefixes;
    private String[] Suffixes;
    private String[] WeekDays;
    private int[] TimeShift = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1};
    private boolean[] PrefixNewLine = new boolean[]{false, false, true, true, true, true, true, true, true, true, true, true};
    private boolean[] SuffixNewLine = new boolean[]{false, true, false, true, false, false, false, true, false, true, false, false};
    private static final String TAG = "WatchPreviewView";
    private WordClockTask wordClockTask;
    private Communicator communicator;
    private int mChinSize = 0;
    private boolean complicationRightSet = false;
    private boolean complicationLeftSet = false;
    private int secondaryTextSizeDP = 14;
    private int batteryLevel = 100;
    private int previousHight = 0;
    private Context activity;
    private String oldLanguage = "";

    public WatchPreviewView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paintFrame = new Paint();
        activity = context;

        try {
            CustomizationScreen activity = (CustomizationScreen) getContext();
            settingsManager = activity.getSettingsManagerComponent().getSettingsManager();
            communicator = ((App) activity.getApplication()).getCommunicatorComponent().getCommunicator();
            communicator.requestConfig(getContext(),new WeakReference<WatchPreviewView>(this));
            Log.d(TAG, "communicatorID" + communicator);
            Log.d(TAG, "WatchPreviewView: from customization");
        } catch (Exception e) {
            SettingsManagerComponent settingsManagerComponent = DaggerSettingsManagerComponent.factory().create(getContext());
            settingsManager = settingsManagerComponent.getSettingsManager();
            HomeScreen activity = (HomeScreen) getContext();
            communicator = ((App) activity.getApplication()).getCommunicatorComponent().getCommunicator();
            communicator.requestPreferences(getContext(), new WeakReference<WatchPreviewView>(this));
            Log.d(TAG, "WatchPreviewView: from home screen");
            Log.d(TAG, "communicatorID: " + communicator);
        }

        mTextPaint = new Paint();
        mTextPaint.setTypeface(getFont(settingsManager.stringHashmap.get(getResources().getString(R.string.preference_font_secondary))));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mTextPaint2 = new Paint();
        mTextPaint2.setTypeface(getFont(settingsManager.stringHashmap.get(getResources().getString(R.string.preference_font))));
        mTextPaint2.setAntiAlias(true);
        mTextPaint2.setTextAlign(Paint.Align.CENTER);

        mTextPaint.setColor(settingsManager.integerHashmap.get(context.getString(R.string.preference_secondary_text_color)));
        mTextPaint2.setColor(settingsManager.integerHashmap.get(context.getString(R.string.preference_main_text_color)));

        mTextPaint.setTextSize(22 + secondaryTextOffset); //secondary text
        mTextPaint2.setTextSize(24 + mainTextOffset);



        generateAffixes(); // Fetch the prefixes and suffixes for the chosen language

        final Handler handler = new Handler(Looper.getMainLooper());
        Runnable reDrawer = new Runnable(){
            public void run(){
                invalidate();
                handler.postDelayed(this,1000);
            }
        };
        reDrawer.run();

        getContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                batteryLevel = (int) (100 * intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) / ((float) (intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1))));
                text1 = (batteryLevel + "%");
            }
        }, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
    }

    private void generateAffixes(){
        switch (settingsManager.stringHashmap.get(activity.getString(R.string.preference_language))) {
            case "nl":
                Prefixes = getResources().getStringArray(R.array.PrefixesNL);
                Suffixes = getResources().getStringArray(R.array.SuffixesNL);
                WeekDays = getResources().getStringArray(R.array.WeekDaysNL);
                TimeShift = new int[]{0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1};
                PrefixNewLine = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true};
                SuffixNewLine = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false};
                break;
            case "en":
                Prefixes = getResources().getStringArray(R.array.Prefixes);
                Suffixes = getResources().getStringArray(R.array.Suffixes);
                WeekDays = getResources().getStringArray(R.array.WeekDays);
                TimeShift = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1};
                PrefixNewLine = new boolean[]{false, false, true, true, true, true, true, true, true, true, true, true};
                SuffixNewLine = new boolean[]{false, true, false, true, false, false, false, true, false, true, false, false};
                break;
            case "de":
                Prefixes = getResources().getStringArray(R.array.PrefixesDE);
                Suffixes = getResources().getStringArray(R.array.SuffixesDE);
                WeekDays = getResources().getStringArray(R.array.WeekDaysDE);
                TimeShift = new int[]{0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1};
                PrefixNewLine = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true};
                SuffixNewLine = new boolean[]{false, false, false, true, false, false, false, false, false, true, false, false};
                break;
            case "el":
                Prefixes = getResources().getStringArray(R.array.PrefixesEL);
                Suffixes = getResources().getStringArray(R.array.SuffixesEL);
                WeekDays = getResources().getStringArray(R.array.WeekDaysEL);
                TimeShift = new int[]{0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1};
                PrefixNewLine = new boolean[]{true, false, true, true, true, true, true, true, true, true, true, true};
                SuffixNewLine = new boolean[]{false, true, true, true, true, true, true, true, true, true, false, false};
                break;
            case "lt":
                Prefixes = getResources().getStringArray(R.array.PrefixesLT);
                Suffixes = getResources().getStringArray(R.array.SuffixesLT);
                WeekDays = getResources().getStringArray(R.array.WeekDaysLT);
                TimeShift = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1};
                PrefixNewLine = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true};
                SuffixNewLine = new boolean[]{false, false, true, true, true, true, true, true, false, false, false, false};
                break;
            case "fi":
                Prefixes = getResources().getStringArray(R.array.PrefixesFI);
                Suffixes = getResources().getStringArray(R.array.SuffixesFI);
                WeekDays = getResources().getStringArray(R.array.WeekDaysFI);
                TimeShift = new int[]{0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1};
                PrefixNewLine = new boolean[]{false, true, true, true, true, true, true, true, true, true, true, true};
                SuffixNewLine = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false};
                break;
            case "no":
                Prefixes = getResources().getStringArray(R.array.PrefixesNO);
                Suffixes = getResources().getStringArray(R.array.SuffixesNO);
                WeekDays = getResources().getStringArray(R.array.WeekDaysNO);
                TimeShift = new int[]{0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1};
                PrefixNewLine = new boolean[]{false, true, true, true, true, true, true, true, true, true, true, true};
                SuffixNewLine = new boolean[]{true, false, false, false, false, false, false, false, false, false, false, false};
                break;
            case "ru":
                Prefixes = getResources().getStringArray(R.array.PrefixesRU);
                Suffixes = getResources().getStringArray(R.array.SuffixesRU);
                WeekDays = getResources().getStringArray(R.array.WeekDaysRU);
                TimeShift = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1};
                PrefixNewLine = new boolean[]{false, false, true, false, true, false, false, false, true, true, true, false};
                SuffixNewLine = new boolean[]{false, true, true, true, true, true, true, true, false, false, false, false};
                break;
            case "hu":
                Prefixes = getResources().getStringArray(R.array.PrefixesHU);
                Suffixes = getResources().getStringArray(R.array.SuffixesHU);
                WeekDays = getResources().getStringArray(R.array.WeekDaysHU);
                TimeShift = new int[]{0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
                PrefixNewLine = new boolean[]{false, false, true, true, true, true, true, true, true, true, false, true};
                SuffixNewLine = new boolean[]{true, true, true, false, false, true, false, true, false, true, true, false};
                break;
            case "it":
                Prefixes = getResources().getStringArray(R.array.PrefixesIT);
                Suffixes = getResources().getStringArray(R.array.SuffixesIT);
                WeekDays = getResources().getStringArray(R.array.WeekDaysIT);
                TimeShift = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1};
                PrefixNewLine = new boolean[]{false, false, true, true, true, true, true, true, true, false, true, true};
                SuffixNewLine = new boolean[]{true, true, true, true, true, true, true, true, true, true, false, false};
                break;
            case "es":
                Prefixes = getResources().getStringArray(R.array.PrefixesES);
                Suffixes = getResources().getStringArray(R.array.SuffixesES);
                WeekDays = getResources().getStringArray(R.array.WeekDaysES);
                TimeShift = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1};
                PrefixNewLine = new boolean[]{false, false, true, false, false, true, false, false, false, false, false, true};
                SuffixNewLine = new boolean[]{false, true, true, true, true, true, true, true, true, true, true, false};
                break;
            case "fr":
                Prefixes = getResources().getStringArray(R.array.PrefixesFR);
                Suffixes = getResources().getStringArray(R.array.SuffixesFR);
                WeekDays = getResources().getStringArray(R.array.WeekDaysFR);
                TimeShift = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1};
                PrefixNewLine = new boolean[]{false, true, false, false, false, false, false, true, false, false, false, false};
                SuffixNewLine = new boolean[]{false, false, true, true, true, true, true, true, true, true, true, true};
                break;
            case "pt":
                Prefixes = getResources().getStringArray(R.array.PrefixesPT);
                Suffixes = getResources().getStringArray(R.array.SuffixesPT);
                WeekDays = getResources().getStringArray(R.array.WeekDaysPT);
                TimeShift = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1};
                PrefixNewLine = new boolean[]{false, false, true, false, true, true, false, true, true, false, false, true};
                SuffixNewLine = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, false};
                break;
            case "sv":
                Prefixes = getResources().getStringArray(R.array.PrefixesSV);
                Suffixes = getResources().getStringArray(R.array.SuffixesSV);
                WeekDays = getResources().getStringArray(R.array.WeekDaysSV);
                TimeShift = new int[]{0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1};
                PrefixNewLine = new boolean[]{false, true, true, true, true, true, true, true, true, true, true, true};
                SuffixNewLine = new boolean[]{true, false, false, false, false, false, true, false, false, false, false, false};
                break;
            default:
                Prefixes = getResources().getStringArray(R.array.Prefixes);
                Suffixes = getResources().getStringArray(R.array.Suffixes);
                WeekDays = getResources().getStringArray(R.array.WeekDays);
                TimeShift = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1};
                PrefixNewLine = new boolean[]{false, false, true, true, true, true, true, true, true, true, true, true};
                SuffixNewLine = new boolean[]{false, true, false, true, false, false, false, true, false, true, false, false};
                break;
        }

        oldLanguage = settingsManager.stringHashmap.get(activity.getString(R.string.preference_language));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw");
        int x = getWidth()/2;
        int y = getHeight()/2;
        paint.setColor(Color.parseColor("#000000"));

        if(!oldLanguage.equals(settingsManager.stringHashmap.get(activity.getString(R.string.preference_language)))) // The language has been changed, update our prefixes and suffixes
            generateAffixes();

        mTextPaint.setTypeface(getFont(settingsManager.stringHashmap.get(getResources().getString(R.string.preference_font_secondary))));

        // Update the size offsets
        mainTextOffset = settingsManager.integerHashmap.get(getResources().getString(R.string.main_text_size_offset));
        secondaryTextOffset = settingsManager.integerHashmap.get(getResources().getString(R.string.secondary_text_size_offset));


        if (mAmbient)
            paintFrame.setColor(Color.parseColor("#333333"));
        else
            paintFrame.setColor(Color.DKGRAY);

        //Set colors
        if (mAmbient) {
            paint.setColor(Color.parseColor("#000000"));
            mTextPaint.setColor(settingsManager.integerHashmap.get(getContext().getString(R.string.preference_secondary_text_color_ambient)));
            mTextPaint2.setColor(settingsManager.integerHashmap.get(getContext().getString(R.string.preference_main_text_color_ambient)));
        } else {
            paint.setColor(settingsManager.integerHashmap.get(getContext().getString(R.string.preference_background_color)));
            mTextPaint.setColor(settingsManager.integerHashmap.get(getContext().getString(R.string.preference_secondary_text_color)));
            mTextPaint2.setColor(settingsManager.integerHashmap.get(getContext().getString(R.string.preference_main_text_color)));
        }
        canvas.drawCircle(x,y,y+2,paintFrame);
        canvas.drawCircle(x,y,y-2,paint);

        //Get time
        mCalendar = Calendar.getInstance();
        int seconds = mCalendar.get(Calendar.SECOND);
        int minutes = mCalendar.get(Calendar.MINUTE);
        if ((minutes%5==0 || minutes==1) && (seconds<2)) {
            significantTimeChange = true;
        }
        int hourDigital = settingsManager.booleanHashmap.get(getContext().getString(R.string.preference_military_time)) ? mCalendar.get(Calendar.HOUR_OF_DAY) : mCalendar.get(Calendar.HOUR);
        if (hourDigital == 0 && !settingsManager.booleanHashmap.get(getContext().getString(R.string.preference_military_time)))
            hourDigital = 12;
        if (hourDigital-lastSignificantHours!=0 || minutes-lastSignificantMinutes>5 || lastSignificantMinutes-minutes<-5) {
            significantTimeChange = true;
        }

        //Get digital clock
        String ampmSymbols = (!settingsManager.booleanHashmap.get(getContext().getString(R.string.preference_military_time))) ? (mCalendar.get(Calendar.HOUR_OF_DAY) >= 12 ? " pm" : " am") : "";
        String text = (mAmbient || !settingsManager.booleanHashmap.get(getContext().getString(R.string.preference_show_seconds)))
                ? String.format(Locale.UK, "%d:%02d" + ampmSymbols, hourDigital, minutes)
                : String.format(Locale.UK, "%d:%02d:%02d" + ampmSymbols, hourDigital, minutes, seconds);

        //Draw digital clock, date, battery percentage and day of the week

        mTextPaint.setTypeface(getFont(settingsManager.stringHashmap.get(getResources().getString(R.string.preference_font_secondary)))); // Set the font for our digital clock.
        float firstSeparator = 60.0f;
        if ((mAmbient && !settingsManager.booleanHashmap.get(getContext().getString(R.string.preference_show_digital_clock_ambient))) || (!mAmbient && !settingsManager.booleanHashmap.get(getContext().getString(R.string.preference_show_digital_clock)))) {
            text = "";
        }
        if (!text.equals("") || !dayOfTheWeek.equals("")) {
            if (!text.equals("") && !dayOfTheWeek.equals("")) {
                canvas.drawText(text + " • " + dayOfTheWeek, getWidth() / 2.0f, firstSeparator - mTextPaint.ascent(), mTextPaint);
                firstSeparator = 60 - mTextPaint.ascent() + mTextPaint.descent();
            } else if (!text.equals("")) {
                canvas.drawText(text, getWidth() / 2.0f, firstSeparator - mTextPaint.ascent(), mTextPaint);
                firstSeparator = 60 - mTextPaint.ascent() + mTextPaint.descent();
            } else {
                canvas.drawText(dayOfTheWeek, getWidth() / 2.0f, firstSeparator - mTextPaint.ascent(), mTextPaint);
                firstSeparator = 60 - mTextPaint.ascent() + mTextPaint.descent();
            }
        }
        if (!((mAmbient && settingsManager.booleanHashmap.get(getContext().getString(R.string.preference_show_calendar_ambient))) || (!mAmbient && settingsManager.booleanHashmap.get(getContext().getString(R.string.preference_show_calendar))))) {
            text3 = "";
        }
        if (!((mAmbient && settingsManager.booleanHashmap.get(getContext().getString(R.string.preference_show_battery_ambient))) || (!mAmbient && settingsManager.booleanHashmap.get(getContext().getString(R.string.preference_show_battery))))) {
            if (!text1.equals("")) {
                text1backup = text1;
            }
            text1 = "";
        } else if (text1.equals("")) {
            text1 = text1backup;
        }
        if (!text3.equals("") || !text1.equals("")) {
            if (!text3.equals("") && !text1.equals("")) {
                canvas.drawText(text3 + " • " + text1, getWidth() / 2.0f, firstSeparator - mTextPaint.ascent(), mTextPaint);
                firstSeparator = firstSeparator - mTextPaint.ascent() + mTextPaint.descent();
            } else if (!text3.equals("")) {
                canvas.drawText(text3, getWidth() / 2.0f, firstSeparator - mTextPaint.ascent(), mTextPaint);
                firstSeparator = firstSeparator - mTextPaint.ascent() + mTextPaint.descent();
            } else {
                canvas.drawText(text1, getWidth() / 2.0f, firstSeparator - mTextPaint.ascent(), mTextPaint);
                firstSeparator = firstSeparator - mTextPaint.ascent() + mTextPaint.descent();
            }
        }
        if (firstSeparator < getHeight() / 4)
            firstSeparator = (float) getHeight() / 4;

        if (text2.equals(""))
            significantTimeChange = true;
        if (basey==-1)
            significantTimeChange = true;
        if (previousHight!=getHeight()) {
            significantTimeChange = true;
            previousHight = getHeight();
        }

        if (settingsManager.significantTimeChange) {
            significantTimeChange = true;
            settingsManager.significantTimeChange = false;
        }

        //Draw text clock
        if (significantTimeChange) {
            mTextPaint2.setTypeface(getFont(settingsManager.stringHashmap.get(getResources().getString(R.string.preference_font))));
            mTextPaint.setTextSize(getHeight() * 0.06f + secondaryTextOffset); //secondary text
            getDate();
            lastSignificantMinutes = minutes;
            lastSignificantHours = hourDigital;
            int index = minutes / 5;
            int hourText = settingsManager.booleanHashmap.get(getContext().getString(R.string.preference_military_text_time)) ? mCalendar.get(Calendar.HOUR_OF_DAY) + TimeShift[index] : mCalendar.get(Calendar.HOUR) + TimeShift[index];
            if (hourText >= 24 && settingsManager.booleanHashmap.get(getContext().getString(R.string.preference_military_text_time)))
                hourText -= 24;
            else if (hourText > 12 && !settingsManager.booleanHashmap.get(getContext().getString(R.string.preference_military_text_time)))
                hourText -= 12;
            if (hourText == 0 && !settingsManager.booleanHashmap.get(getContext().getString(R.string.preference_military_text_time)))
                hourText = 12;
            wordClockTask = new WordClockTask(new WeakReference<Context>(getContext()),
                        settingsManager.stringHashmap.get(getContext().getString(R.string.preference_font)),
                        settingsManager.integerHashmap.get(getContext().getString(R.string.preference_capitalisation)),
                        hourText,
                        minutes,
                        index,
                        Prefixes,
                        Suffixes,
                        PrefixNewLine,
                        SuffixNewLine,
                        settingsManager.stringHashmap.get(getContext().getString(R.string.preference_language)),
                        true,
                        false,
                        complicationLeftSet,
                        complicationRightSet,
                        getHeight(),
                        getHeight(),firstSeparator,mChinSize,mainTextOffset,new WeakReference<WordClockListener>(this));
            Log.d(TAG, "onDraw: getHeight: "+getHeight());
            wordClockTask.execute();
            significantTimeChange = false;
        }

        //Draw text
        float t = basey;
        for (String line : text2.split("\n")) {
            canvas.drawText(line, x, t, mTextPaint2);
            t += mTextPaint2.descent() - mTextPaint2.ascent();
        }

        //Draw complication
        //TODO placeholder
    }

    private Typeface getFont(String newFont){
        Typeface NORMAL_TYPEFACE2;
        switch (newFont) {
            case "robotolight":
                NORMAL_TYPEFACE2 = Typeface.create("sans-serif-light", Typeface.NORMAL);
                // mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                break;
            case "alegreya":
                NORMAL_TYPEFACE2 = ResourcesCompat.getFont(activity, R.font.alegreya);
                // mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                break;
            case "cabin":
                NORMAL_TYPEFACE2 = ResourcesCompat.getFont(activity, R.font.cabin);
                // mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                break;
            case "ibmplexsans":
                NORMAL_TYPEFACE2 = ResourcesCompat.getFont(activity, R.font.ibmplexsans);
                // mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                break;
            case "inconsolata":
                NORMAL_TYPEFACE2 = ResourcesCompat.getFont(activity, R.font.inconsolata);
                // mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                break;
            case "merriweather":
                NORMAL_TYPEFACE2 = ResourcesCompat.getFont(activity, R.font.merriweather);
                // mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                break;
            case "nunito":
                NORMAL_TYPEFACE2 = ResourcesCompat.getFont(activity, R.font.nunito);
                // mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                break;
            case "pacifico":
                NORMAL_TYPEFACE2 = ResourcesCompat.getFont(activity, R.font.pacifico);
                // mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                break;
            case "quattrocento":
                NORMAL_TYPEFACE2 = ResourcesCompat.getFont(activity, R.font.quattrocento);
                // mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                break;
            case "quicksand":
                NORMAL_TYPEFACE2 = ResourcesCompat.getFont(activity, R.font.quicksand);
                // mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                break;
            case "rubik":
                NORMAL_TYPEFACE2 = ResourcesCompat.getFont(activity, R.font.rubik);
                // mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                break;
            default:
                NORMAL_TYPEFACE2 = Typeface.create("sans-serif-light", Typeface.NORMAL);
                // mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                break;
        }

        return NORMAL_TYPEFACE2;
    }

    private void getDate() {
        //Get date
        int first, second, third;
        boolean FourFirst;
        switch (settingsManager.integerHashmap.get(getContext().getString(R.string.preference_date_order))) {
            case 0:
                first = mCalendar.get(Calendar.MONTH) + 1; //MDY
                second = mCalendar.get(Calendar.DAY_OF_MONTH);
                third = mCalendar.get(Calendar.YEAR);
                FourFirst = false;
                break;
            case 1:
                first = mCalendar.get(Calendar.DAY_OF_MONTH); //DMY
                second = mCalendar.get(Calendar.MONTH) + 1;
                third = mCalendar.get(Calendar.YEAR);
                FourFirst = false;
                break;
            case 2:
                first = mCalendar.get(Calendar.YEAR); //YMD
                second = mCalendar.get(Calendar.MONTH) + 1;
                third = mCalendar.get(Calendar.DAY_OF_MONTH);
                FourFirst = true;
                break;
            case 3:
                first = mCalendar.get(Calendar.YEAR); //YDM
                second = mCalendar.get(Calendar.DAY_OF_MONTH);
                third = mCalendar.get(Calendar.MONTH) + 1;
                FourFirst = true;
                break;
            default:
                first = mCalendar.get(Calendar.MONTH) + 1;
                second = mCalendar.get(Calendar.DAY_OF_MONTH);
                third = mCalendar.get(Calendar.YEAR);
                FourFirst = false;
                break;
        }
        if (FourFirst)
            text3 = String.format(Locale.UK, "%04d" + settingsManager.stringHashmap.get(getContext().getString(R.string.preference_date_separator)) + "%02d" + settingsManager.stringHashmap.get(getContext().getString(R.string.preference_date_separator)) + "%02d", first, second, third);
        else
            text3 = String.format(Locale.UK, "%02d" + settingsManager.stringHashmap.get(getContext().getString(R.string.preference_date_separator)) + "%02d" + settingsManager.stringHashmap.get(getContext().getString(R.string.preference_date_separator)) + "%04d", first, second, third);


        //Get day of the week
        if ((mAmbient && settingsManager.booleanHashmap.get(getContext().getString(R.string.preference_show_day_ambient))) || (!mAmbient && settingsManager.booleanHashmap.get(getContext().getString(R.string.preference_show_day))))
            dayOfTheWeek = WeekDays[mCalendar.get(Calendar.DAY_OF_WEEK) - 1];
        else
            dayOfTheWeek = "";
    }

    @Override
    public void wordClockListener(WordClockTaskWrapper wordClockTaskWrapper) {
        basey = wordClockTaskWrapper.getBasey();
        Log.d(TAG, "wordClockListener: baseY "+basey);
        text2 = wordClockTaskWrapper.getText();
        mTextPaint2.setTextSize(wordClockTaskWrapper.getTextSize()+mainTextOffset);
        x = wordClockTaskWrapper.getX();
        invalidate();
    }

    public void receivedDataListener(String[] booleanPreferencesTemp) {
        Log.d(TAG, "receivedDataListener");
        mChinSize = Integer.parseInt(booleanPreferencesTemp[0]);
        complicationLeftSet = booleanPreferencesTemp[1].equals("true");
        complicationRightSet = booleanPreferencesTemp[2].equals("true");
    }

    public void changeAmbientMode(boolean value) {
        mAmbient = value;
        significantTimeChange = true;
        Log.d(TAG, "changeAmbientMode: "+value);
    }

    public void loadSettings(SettingsManager newSettings){
        for(Map.Entry<String, String> preference : newSettings.stringHashmap.entrySet()){
            String key = preference.getKey();
            String value = preference.getValue();
            if(value != null)
                this.settingsManager.stringHashmap.put(key, value);
        }

        for(Map.Entry<String, Boolean> preference : newSettings.booleanHashmap.entrySet()){
            String key = preference.getKey();
            boolean value = preference.getValue();
            this.settingsManager.booleanHashmap.put(key, value);
        }

        for(Map.Entry<String, Integer> preference : newSettings.integerHashmap.entrySet()){
            String key = preference.getKey();
            int value = preference.getValue();

            this.settingsManager.integerHashmap.put(key, value);
        }

        settingsManager.significantTimeChange = true;
        invalidate();
    }
}
