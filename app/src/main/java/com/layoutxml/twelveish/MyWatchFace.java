/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 *
 */

package com.layoutxml.twelveish;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationHelperActivity;
import android.support.wearable.complications.rendering.ComplicationDrawable;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import com.layoutxml.twelveish.config.ComplicationConfigActivity;
import com.layoutxml.twelveish.config.DigitalWatchFaceWearableConfigActivity;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static android.view.Gravity.CENTER_HORIZONTAL;
import static android.view.Gravity.TOP;

public class MyWatchFace extends CanvasWatchFaceService {
    private static Typeface NORMAL_TYPEFACE = Typeface.create("sans-serif-light", Typeface.NORMAL);

    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);
    private static final int MSG_UPDATE_TIME = 0;
    private static final String TAG = "MyWatchFace";
    private String[] Prefixes;
    private String[] Suffixes;
    private String[] WeekDays;
    private Integer[] TimeShift = new Integer[]{0,0,0,0,0,0,0,0,1,1,1,1};
    private Boolean[] PrefixNewLine = new Boolean[]{false,false,true,true,true,true,true,true,true,true,true,true};
    private Boolean[] SuffixNewLine = new Boolean[]{false,true,false,true,false,false,false,true,false,true,false,false};
    private Boolean isRound = true;
    private SharedPreferences prefs;
    private Integer batteryLevel=100;
    private Integer screenWidthG;
    private Integer screenHeightG;
    private Boolean showedRateAlready;
    private Boolean showedTutorialAlready;
    private Integer counter;
    //SharedPreferences:
    private Integer backgroundColor;
    private Integer mainColor;
    private Integer mainColorAmbient;
    private Integer secondaryColor;
    private Integer secondaryColorAmbient;
    private Boolean militaryTime;
    private Boolean militaryTextTime;
    private Integer dateOrder;
    private String dateSeparator;
    private Integer capitalisation;
    private Boolean ampm;
    private Boolean showSecondary;
    private Boolean showSecondaryActive;
    private Boolean showSecondaryCalendar;
    private Boolean showSecondaryCalendarActive;
    private Boolean showSuffixes;
    private Boolean showBattery;
    private Boolean showBatteryAmbient;
    private Boolean showWords;
    private Boolean showWordsAmbient;
    private Boolean showSeconds;
    private Boolean showComplication;
    private Boolean showComplicationAmbient;
    private String language;
    private String font;
    private Boolean showDay;
    private Boolean showDayAmbient;
    private Boolean disableComplicationTap;
    //Complications and their data
    private Boolean complicationLeftSet;
    private Boolean complicationRightSet;
    private static final int BOTTOM_COMPLICATION_ID = 0;
    private static final int LEFT_COMPLICATION_ID = 1;
    private static final int RIGHT_COMPLICATION_ID = 2;
    private static final int[] COMPLICATION_IDS= {BOTTOM_COMPLICATION_ID, LEFT_COMPLICATION_ID, RIGHT_COMPLICATION_ID};
    private static final int[][] COMPLICATION_SUPPORTED_TYPES = {
            {
                    ComplicationData.TYPE_RANGED_VALUE,
                    ComplicationData.TYPE_ICON,
                    ComplicationData.TYPE_LONG_TEXT,
                    ComplicationData.TYPE_SHORT_TEXT,
                    ComplicationData.TYPE_SMALL_IMAGE,
                    ComplicationData.TYPE_LARGE_IMAGE
            },
            {
                    ComplicationData.TYPE_RANGED_VALUE,
                    ComplicationData.TYPE_ICON,
                    ComplicationData.TYPE_SHORT_TEXT,
                    ComplicationData.TYPE_SMALL_IMAGE
            },
            {
                    ComplicationData.TYPE_RANGED_VALUE,
                    ComplicationData.TYPE_ICON,
                    ComplicationData.TYPE_SHORT_TEXT,
                    ComplicationData.TYPE_SMALL_IMAGE
            }
    };
    private SparseArray<ComplicationData> mActiveComplicationDataSparseArray;
    private SparseArray<ComplicationDrawable> mComplicationDrawableSparseArray;

    @Override
    public Engine onCreateEngine() {
        getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                batteryLevel = (int)(100*intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1)/((float)(intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1))));
            }
        }, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return new Engine();
    }

    public static int getComplicationId(ComplicationConfigActivity.ComplicationLocation complicationLocation){
        switch (complicationLocation) {
            case BOTTOM:
                return BOTTOM_COMPLICATION_ID;
            case LEFT:
                return LEFT_COMPLICATION_ID;
            case RIGHT:
                return RIGHT_COMPLICATION_ID;
            default:
                return -1;
        }
    }

    public static int[] getComplicationIds() {
        return COMPLICATION_IDS;
    }

    public static int[] getSupportedComplicationTypes(ComplicationConfigActivity.ComplicationLocation complicationLocation) {
        switch (complicationLocation) {
            case BOTTOM:
                return COMPLICATION_SUPPORTED_TYPES[0];
            case LEFT:
                return COMPLICATION_SUPPORTED_TYPES[1];
            case RIGHT:
                return COMPLICATION_SUPPORTED_TYPES[2];
            default:
                return new int[] {};
        }
    }

    private void drawComplications(Canvas canvas, long currentTimeMillis) {
        int complicationId;
        ComplicationDrawable complicationDrawable;
        for (int COMPLICATION_ID : COMPLICATION_IDS) {
            complicationId = COMPLICATION_ID;
            complicationDrawable = mComplicationDrawableSparseArray.get(complicationId);
            complicationDrawable.setBackgroundColorActive(backgroundColor);
            complicationDrawable.setHighlightColorActive(secondaryColor);
            complicationDrawable.setHighlightColorAmbient(secondaryColorAmbient);
            complicationDrawable.setIconColorActive(secondaryColor);
            complicationDrawable.setIconColorAmbient(secondaryColorAmbient);
            complicationDrawable.setTextColorActive(secondaryColor);
            complicationDrawable.setTextColorAmbient(secondaryColorAmbient);
            complicationDrawable.setRangedValuePrimaryColorActive(secondaryColor);
            complicationDrawable.setRangedValuePrimaryColorAmbient(secondaryColorAmbient);
            complicationDrawable.setRangedValueSecondaryColorActive(secondaryColor);
            complicationDrawable.setRangedValueSecondaryColorAmbient(secondaryColorAmbient);
            complicationDrawable.setRangedValueRingWidthActive(secondaryColor);
            complicationDrawable.setRangedValueRingWidthAmbient(secondaryColorAmbient);
            complicationDrawable.setTitleColorActive(secondaryColor);
            complicationDrawable.setTitleColorAmbient(secondaryColorAmbient);
            complicationDrawable.draw(canvas, currentTimeMillis);
        }
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<MyWatchFace.Engine> mWeakReference;

        EngineHandler(MyWatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            MyWatchFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine {

        private final Handler mUpdateTimeHandler = new EngineHandler(this);
        private Calendar mCalendar;
        private final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };
        private boolean mRegisteredTimeZoneReceiver = false;
        private float mChinSize;
        private Paint mBackgroundPaint;
        private Paint mTextPaint;
        private Paint mTextPaint2;
        private boolean mLowBitAmbient;
        private boolean mAmbient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(MyWatchFace.this)
                    .setStatusBarGravity(CENTER_HORIZONTAL | TOP)
                    .setShowUnreadCountIndicator(true)
                    .setAcceptsTapEvents(true)
                    .build());

            mCalendar = Calendar.getInstance();

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.background));

            mTextPaint = new Paint();
            mTextPaint.setTypeface(NORMAL_TYPEFACE);
            mTextPaint.setAntiAlias(true);
            mTextPaint.setTextAlign(Paint.Align.CENTER);

            mTextPaint2 = new Paint();
            mTextPaint2.setTypeface(NORMAL_TYPEFACE);
            mTextPaint2.setAntiAlias(true);
            mTextPaint2.setTextAlign(Paint.Align.CENTER);

            prefs = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            loadPreferences();

            if (android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.N)
                initializeComplications();
        }

        private void showRateNotification(){
            Log.d(TAG,"showRateNotification: start");
            int notificationId = 1;
            // The channel ID of the notification.
            String id = "Main";
            // Build intent for notification content
            Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.layoutxml.twelveish"));
            viewIntent.putExtra("Rate Twelveish", "Would you like to rate Twelveish? I won't ask again :)");
            PendingIntent viewPendingIntent =
                    PendingIntent.getActivity(getApplicationContext(), 0, viewIntent, 0);

            // Notification channel ID is ignored for Android 7.1.1
            // (API level 25) and lower.
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(getApplicationContext(), id)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Rate Twelveish")
                            .setContentText("Would you like to rate Twelveish? Tap to go to Google Play store.")
                            .setContentIntent(viewPendingIntent);

            // Get an instance of the NotificationManager service
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(getApplicationContext());

            // Issue the notification with notification manager.
            notificationManager.notify(notificationId, notificationBuilder.build());
        }

        private void showTutorialNotification(){
            int notificationId = 2;
            // The channel ID of the notification.
            String id = "Main";
            // Build intent for notification content
            Intent viewIntent = new Intent(getApplicationContext(), DigitalWatchFaceWearableConfigActivity.class);
            viewIntent.putExtra("Open settings", "Don't forget to customize the watch");
            PendingIntent viewPendingIntent =
                    PendingIntent.getActivity(getApplicationContext(), 0, viewIntent, 0);

            // Notification channel ID is ignored for Android 7.1.1
            // (API level 25) and lower.
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(getApplicationContext(), id)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Open Twelveish Settings")
                            .setContentText("Don't forget to customize Twelveish directly on your watch")
                            .setContentIntent(viewPendingIntent);

            // Get an instance of the NotificationManager service
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(getApplicationContext());

            // Issue the notification with notification manager.
            notificationManager.notify(notificationId, notificationBuilder.build());
        }

        private void initializeComplications() {
            mActiveComplicationDataSparseArray = new SparseArray<>(COMPLICATION_IDS.length);
            ComplicationDrawable bottomComplicationDrawable = (ComplicationDrawable)getDrawable(R.drawable.custom_complication_styles);
            ComplicationDrawable leftComplicationDrawable = (ComplicationDrawable)getDrawable(R.drawable.custom_complication_styles);
            ComplicationDrawable rightComplicationDrawable = (ComplicationDrawable)getDrawable(R.drawable.custom_complication_styles);
            assert bottomComplicationDrawable != null;
            bottomComplicationDrawable.setContext(getApplicationContext());
            assert leftComplicationDrawable != null;
            leftComplicationDrawable.setContext(getApplicationContext());
            assert rightComplicationDrawable != null;
            rightComplicationDrawable.setContext(getApplicationContext());
            mComplicationDrawableSparseArray = new SparseArray<>(COMPLICATION_IDS.length);
            mComplicationDrawableSparseArray.put(BOTTOM_COMPLICATION_ID, bottomComplicationDrawable);
            mComplicationDrawableSparseArray.put(LEFT_COMPLICATION_ID, leftComplicationDrawable);
            mComplicationDrawableSparseArray.put(RIGHT_COMPLICATION_ID, rightComplicationDrawable);
            setActiveComplications(COMPLICATION_IDS);
        }

        @Override
        public void onComplicationDataUpdate(
                int complicationId, ComplicationData complicationData) {
            mActiveComplicationDataSparseArray.put(complicationId, complicationData);
            ComplicationDrawable complicationDrawable = mComplicationDrawableSparseArray.get(complicationId);
            complicationDrawable.setComplicationData(complicationData);
            invalidate();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            screenHeightG=height;
            screenWidthG=width;
        }

        private void loadPreferences() {
            showedRateAlready = prefs.getBoolean(getString(R.string.showed_rate),false);
            counter = prefs.getInt(getString(R.string.counter),0);
            showedTutorialAlready = prefs.getBoolean(getString(R.string.showed_tutorial),false);
            backgroundColor = prefs.getInt(getString(R.string.preference_background_color), android.graphics.Color.parseColor("#000000"));
            mainColor = prefs.getInt(getString(R.string.preference_main_color), android.graphics.Color.parseColor("#ffffff"));
            mainColorAmbient = prefs.getInt(getString(R.string.preference_main_color_ambient), android.graphics.Color.parseColor("#ffffff"));
            secondaryColor = prefs.getInt(getString(R.string.preference_secondary_color), android.graphics.Color.parseColor("#ffffff"));
            secondaryColorAmbient = prefs.getInt(getString(R.string.preference_secondary_color_ambient), android.graphics.Color.parseColor("#ffffff"));
            militaryTime = prefs.getBoolean(getString(R.string.preference_military_time), false);
            militaryTextTime = prefs.getBoolean(getString(R.string.preference_militarytext_time), false);
            dateOrder = prefs.getInt(getString(R.string.preference_date_order), 0);
            dateSeparator = prefs.getString(getString(R.string.preference_date_separator), "/");
            capitalisation = prefs.getInt(getString(R.string.preference_capitalisation), 0);
            ampm = prefs.getBoolean(getString(R.string.preference_ampm), true);
            showSecondary = prefs.getBoolean(getString(R.string.preference_show_secondary), true);
            showSecondaryActive = prefs.getBoolean(getString(R.string.preference_show_secondary_active), true);
            showSecondaryCalendar = prefs.getBoolean(getString(R.string.preference_show_secondary_calendar), true);
            showSecondaryCalendarActive = prefs.getBoolean(getString(R.string.preference_show_secondary_calendar_active), true);
            showSuffixes = prefs.getBoolean(getString(R.string.preference_show_suffixes), true);
            showBattery = prefs.getBoolean(getString(R.string.preference_show_battery), true);
            showBatteryAmbient = prefs.getBoolean(getString(R.string.preference_show_battery_ambient), true);
            showDay = prefs.getBoolean(getString(R.string.preference_show_day), true);
            showDayAmbient = prefs.getBoolean(getString(R.string.preference_show_day_ambient), true);
            showWords = prefs.getBoolean(getString(R.string.preference_show_words), true);
            showWordsAmbient = prefs.getBoolean(getString(R.string.preference_show_words_ambient), true);
            showSeconds = prefs.getBoolean(getString(R.string.preference_show_seconds),true);
            showComplication = prefs.getBoolean(getString(R.string.preference_show_complications),true);
            showComplicationAmbient = prefs.getBoolean(getString(R.string.preference_show_complications_ambient),true);
            language = prefs.getString(getString(R.string.preference_language),"en");
            font = prefs.getString(getString(R.string.preference_font),"robotolight");
            disableComplicationTap = prefs.getBoolean(getString(R.string.preference_tap),false);
            complicationLeftSet = prefs.getBoolean(getString(R.string.complication_left_set),false);
            complicationRightSet = prefs.getBoolean(getString(R.string.complication_right_set),false);

            //Work with given preferences
            switch (language) {
                case "en":
                    Prefixes = getResources().getStringArray(R.array.Prefixes);
                    Suffixes = getResources().getStringArray(R.array.Suffixes);
                    WeekDays = getResources().getStringArray(R.array.WeekDays);
                    TimeShift = new Integer[]{0,0,0,0,0,0,0,0,1,1,1,1};
                    PrefixNewLine = new Boolean[]{false,false,true,true,true,true,true,true,true,true,true,true};
                    SuffixNewLine = new Boolean[]{false,true,false,true,false,false,false,true,false,true,false,false};
                    break;
                case "de":
                    Prefixes = getResources().getStringArray(R.array.PrefixesDE);
                    Suffixes = getResources().getStringArray(R.array.SuffixesDE);
                    WeekDays = getResources().getStringArray(R.array.WeekDaysDE);
                    TimeShift = new Integer[]{0,0,0,0,1,1,1,1,1,1,1,1};
                    PrefixNewLine = new Boolean[]{true,true,true,true,true,true,true,true,true,true,true,true};
                    SuffixNewLine = new Boolean[]{false,false,false,true,false,false,false,false,false,true,false,false};
                    break;
                case "lt":
                    Prefixes = getResources().getStringArray(R.array.PrefixesLT);
                    Suffixes = getResources().getStringArray(R.array.SuffixesLT);
                    WeekDays = getResources().getStringArray(R.array.WeekDaysLT);
                    TimeShift = new Integer[]{0,0,0,0,0,0,0,0,1,1,1,1};
                    PrefixNewLine = new Boolean[]{true,true,true,true,true,true,true,true,true,true,true,true};
                    SuffixNewLine = new Boolean[]{false,false,true,true,true,true,true,true,false,false,false,false};
                    break;
                case "fi":
                    Prefixes = getResources().getStringArray(R.array.PrefixesFI);
                    Suffixes = getResources().getStringArray(R.array.SuffixesFI);
                    WeekDays = getResources().getStringArray(R.array.WeekDaysFI);
                    TimeShift = new Integer[]{0,0,0,0,1,1,1,1,1,1,1,1};
                    PrefixNewLine = new Boolean[]{true,true,true,true,true,true,true,true,true,true,true,true};
                    SuffixNewLine = new Boolean[]{false,false,false,false,false,false,false,false,false,false,false,false};
                    break;
                case "ru":
                    Prefixes = getResources().getStringArray(R.array.PrefixesRU);
                    Suffixes = getResources().getStringArray(R.array.SuffixesRU);
                    WeekDays = getResources().getStringArray(R.array.WeekDaysRU);
                    TimeShift = new Integer[]{0,0,0,0,0,0,0,0,1,1,1,1};
                    PrefixNewLine = new Boolean[]{false,false,true,false,true,false,false,false,true,true,true,false};
                    SuffixNewLine = new Boolean[]{false,true,true,true,true,true,true,true,false,false,false,false};
                    break;
                case "hu":
                    Prefixes = getResources().getStringArray(R.array.PrefixesHU);
                    Suffixes = getResources().getStringArray(R.array.SuffixesHU);
                    WeekDays = getResources().getStringArray(R.array.WeekDaysHU);
                    TimeShift = new Integer[]{0,0,1,1,1,1,1,1,1,1,1,1};
                    PrefixNewLine = new Boolean[]{false,false,true,true,true,true,true,true,true,true,false,true};
                    SuffixNewLine = new Boolean[]{true,true,true,false,false,true,false,true,false,true,true,false};
                    break;
                case "it":
                    Prefixes = getResources().getStringArray(R.array.PrefixesIT);
                    Suffixes = getResources().getStringArray(R.array.SuffixesIT);
                    WeekDays = getResources().getStringArray(R.array.WeekDaysIT);
                    TimeShift = new Integer[]{0,0,0,0,0,0,0,0,1,1,1,1};
                    PrefixNewLine = new Boolean[]{false,false,true,true,true,true,true,true,true,false,true,true};
                    SuffixNewLine = new Boolean[]{true,true,true,true,true,true,true,true,true,true,false,false};
                    break;
                default:
                    Prefixes = getResources().getStringArray(R.array.Prefixes);
                    Suffixes = getResources().getStringArray(R.array.Suffixes);
                    WeekDays = getResources().getStringArray(R.array.WeekDays);
                    TimeShift = new Integer[]{0,0,0,0,0,0,0,0,1,1,1,1};
                    PrefixNewLine = new Boolean[]{false,false,true,true,true,true,true,true,true,true,true,true};
                    SuffixNewLine = new Boolean[]{false,true,false,true,false,false,false,true,false,true,false,false};
                    break;

            }

            switch (font) {
                case "robotolight":
                    NORMAL_TYPEFACE = Typeface.create("sans-serif-light", Typeface.NORMAL);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE);
                    break;
                case "alegreya":
                    NORMAL_TYPEFACE = ResourcesCompat.getFont(getApplicationContext(),R.font.alegreya);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE);
                    break;
                case "cabin":
                    NORMAL_TYPEFACE = ResourcesCompat.getFont(getApplicationContext(),R.font.cabin);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE);
                    break;
                case "ibmplexsans":
                    NORMAL_TYPEFACE = ResourcesCompat.getFont(getApplicationContext(),R.font.ibmplexsans);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE);
                    break;
                case "inconsolata":
                    NORMAL_TYPEFACE = ResourcesCompat.getFont(getApplicationContext(),R.font.inconsolata);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE);
                    break;
                case "merriweather":
                    NORMAL_TYPEFACE = ResourcesCompat.getFont(getApplicationContext(),R.font.merriweather);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE);
                    break;
                case "nunito":
                    NORMAL_TYPEFACE = ResourcesCompat.getFont(getApplicationContext(),R.font.nunito);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE);
                    break;
                case "pacifico":
                    NORMAL_TYPEFACE = ResourcesCompat.getFont(getApplicationContext(),R.font.pacifico);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE);
                    break;
                case "quattrocento":
                    NORMAL_TYPEFACE = ResourcesCompat.getFont(getApplicationContext(),R.font.quattrocento);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE);
                    break;
                case "quicksand":
                    NORMAL_TYPEFACE = ResourcesCompat.getFont(getApplicationContext(),R.font.quicksand);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE);
                    break;
                case "rubik":
                    NORMAL_TYPEFACE = ResourcesCompat.getFont(getApplicationContext(),R.font.rubik);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE);
                    break;
                default:
                    NORMAL_TYPEFACE = Typeface.create("sans-serif-light", Typeface.NORMAL);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE);
                    break;
            }

            if (counter>=100 && !showedRateAlready) {
                prefs.edit().putBoolean(getString(R.string.showed_rate),true).apply();
                showRateNotification();
            }

            counter++;
            if (counter<102) {
                prefs.edit().putInt(getString(R.string.counter),counter).apply();
            }

            if (!showedTutorialAlready && counter>30){
                prefs.edit().putBoolean(getString(R.string.showed_tutorial),true).apply();
                showTutorialNotification();
            }
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                registerReceiver();
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            } else {
                unregisterReceiver();
            }
            loadPreferences();
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            MyWatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            MyWatchFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);
            Resources resources = MyWatchFace.this.getResources();
            isRound = insets.isRound();
            mChinSize = insets.getSystemWindowInsetBottom();
            float textSize = resources.getDimension(isRound ? R.dimen.digital_text_size_round : R.dimen.digital_text_size);
            float textSizeSmall = resources.getDimension(isRound ? R.dimen.digital_text_size_round : R.dimen.digital_text_size)/2.5f;
            mTextPaint.setTextSize(textSizeSmall);
            mTextPaint2.setTextSize(textSize);
            Rect bottomBounds = new Rect(screenWidthG/2-screenWidthG/4,
                    (int)(screenHeightG*3/4-mChinSize),
                    screenWidthG/2+screenWidthG/4,
                    (int)(screenHeightG-mChinSize));
            Rect leftBounds = new Rect(0,
                    screenHeightG*3/8,
                    screenWidthG/4,
                    screenHeightG*5/8);
            Rect rightBounds = new Rect(screenWidthG*3/4,
                    screenHeightG*3/8,
                    screenWidthG,
                    screenHeightG*5/8);
            if (android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.N) {
                ComplicationDrawable bottomComplicationDrawable = mComplicationDrawableSparseArray.get(BOTTOM_COMPLICATION_ID);
                bottomComplicationDrawable.setBounds(bottomBounds);
                ComplicationDrawable leftComplicationDrawable = mComplicationDrawableSparseArray.get(LEFT_COMPLICATION_ID);
                leftComplicationDrawable.setBounds(leftBounds);
                ComplicationDrawable rightComplicationDrawable = mComplicationDrawableSparseArray.get(RIGHT_COMPLICATION_ID);
                rightComplicationDrawable.setBounds(rightBounds);
            }
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            mAmbient = inAmbientMode;
            if (mLowBitAmbient) {
                mTextPaint.setAntiAlias(!inAmbientMode);
                mTextPaint2.setAntiAlias(!inAmbientMode);
            }
            if (android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.N) {
                ComplicationDrawable complicationDrawable;
                for (int COMPLICATION_ID : COMPLICATION_IDS) {
                    complicationDrawable = mComplicationDrawableSparseArray.get(COMPLICATION_ID);
                    complicationDrawable.setInAmbientMode(mAmbient);
                }
            }
            updateTimer();
        }

        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            switch (tapType) {
                case TAP_TYPE_TOUCH:
                    // The user has started touching the screen.
                    break;
                case TAP_TYPE_TOUCH_CANCEL:
                    // The user has started a different gesture or otherwise cancelled the tap.
                    break;
                case TAP_TYPE_TAP:
                    // The user has completed the tap gesture.
                    if (android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.N && !disableComplicationTap) {
                        int tappedComplicationId = getTappedComplicationId(x, y);
                        if (tappedComplicationId != -1) {
                            onComplicationTap(tappedComplicationId);
                        }
                    }
                    break;
            }
            invalidate();
        }

        private int getTappedComplicationId(int x, int y) {
            int complicationId;
            ComplicationData complicationData;
            ComplicationDrawable complicationDrawable;
            long currentTimeMillis = System.currentTimeMillis();
            for (int COMPLICATION_ID : COMPLICATION_IDS) {
                complicationId = COMPLICATION_ID;
                complicationData = mActiveComplicationDataSparseArray.get(complicationId);
                if ((complicationData != null)
                        && (complicationData.isActive(currentTimeMillis))
                        && (complicationData.getType() != ComplicationData.TYPE_NOT_CONFIGURED)
                        && (complicationData.getType() != ComplicationData.TYPE_EMPTY)) {
                    complicationDrawable = mComplicationDrawableSparseArray.get(complicationId);
                    Rect complicationBoundingRect = complicationDrawable.getBounds();
                    if (complicationBoundingRect.width() > 0) {
                        if (complicationBoundingRect.contains(x, y)) {
                            return complicationId;
                        }
                    } else {
                        Log.e(TAG, "Not a recognized complication id.");
                    }
                }
            }
            return -1;
        }

        private void onComplicationTap(int complicationId) {
            ComplicationData complicationData = mActiveComplicationDataSparseArray.get(complicationId);
            if (complicationData != null) {
                if (complicationData.getTapAction() != null) {
                    try {
                        complicationData.getTapAction().send();
                    } catch (PendingIntent.CanceledException e) {
                        Log.e(TAG, "onComplicationTap() tap action error: " + e);
                    }
                } else if (complicationData.getType() == ComplicationData.TYPE_NO_PERMISSION) {
                    ComponentName componentName =
                            new ComponentName(
                                    getApplicationContext(), MyWatchFace.class);
                    Intent permissionRequestIntent =
                            ComplicationHelperActivity.createPermissionRequestHelperIntent(
                                    getApplicationContext(), componentName);
                    startActivity(permissionRequestIntent);
                }
            } else {
                Log.d(TAG, "No PendingIntent for complication " + complicationId + ".");
            }
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            //Set colors
            if (isInAmbientMode()) {
                canvas.drawColor(Color.BLACK);
                mTextPaint.setColor(secondaryColorAmbient);
                mTextPaint2.setColor(mainColorAmbient);
            } else {
                canvas.drawColor(backgroundColor);
                mTextPaint.setColor(secondaryColor);
                mTextPaint2.setColor(mainColor);
            }

            //Get time
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);
            Integer seconds = mCalendar.get(Calendar.SECOND);
            Integer minutes = mCalendar.get(Calendar.MINUTE);
            Integer hourDigital = militaryTime
                    ? mCalendar.get(Calendar.HOUR_OF_DAY)
                    : mCalendar.get(Calendar.HOUR);
            if (hourDigital==0 && !militaryTime)
                hourDigital=12;
            Integer index = minutes / 5;
            Integer hourText = militaryTextTime
                    ? mCalendar.get(Calendar.HOUR_OF_DAY)+TimeShift[index]
                    : mCalendar.get(Calendar.HOUR)+TimeShift[index];
            if (hourText>=24 && militaryTextTime)
                hourText-=24;
            else if (hourText>12 && !militaryTextTime)
                hourText-=12;
            if (hourText==0 && !militaryTextTime)
                hourText = 12;

            //Get digital clock
            String ampmSymbols = (ampm) ? (mCalendar.get(Calendar.HOUR_OF_DAY)>=12 ? " pm" : " am") : "";
            String text = (mAmbient || !showSeconds)
                    ? String.format(Locale.UK, "%d:%02d"+ampmSymbols, hourDigital, minutes)
                    : String.format(Locale.UK,"%d:%02d:%02d"+ampmSymbols, hourDigital, minutes, seconds);

            //Get date
            Integer first, second, third;
            Boolean FourFirst;
            switch (dateOrder) {
                case 0:
                    first = mCalendar.get(Calendar.MONTH)+1; //MDY
                    second = mCalendar.get(Calendar.DAY_OF_MONTH);
                    third = mCalendar.get(Calendar.YEAR);
                    FourFirst = false;
                    break;
                case 1:
                    first = mCalendar.get(Calendar.DAY_OF_MONTH); //DMY
                    second = mCalendar.get(Calendar.MONTH)+1;
                    third = mCalendar.get(Calendar.YEAR);
                    FourFirst = false;
                    break;
                case 2:
                    first = mCalendar.get(Calendar.YEAR); //YMD
                    second = mCalendar.get(Calendar.MONTH)+1;
                    third = mCalendar.get(Calendar.DAY_OF_MONTH);
                    FourFirst = true;
                    break;
                case 3:
                    first = mCalendar.get(Calendar.YEAR); //YDM
                    second = mCalendar.get(Calendar.DAY_OF_MONTH);
                    third = mCalendar.get(Calendar.MONTH)+1;
                    FourFirst = true;
                    break;
                default:
                    first = mCalendar.get(Calendar.MONTH)+1;
                    second = mCalendar.get(Calendar.DAY_OF_MONTH);
                    third = mCalendar.get(Calendar.YEAR);
                    FourFirst = false;
                    break;
            }
            String text3;
            if (FourFirst)
                text3 = String.format(Locale.UK, "%04d"+dateSeparator+"%02d"+dateSeparator+"%02d", first, second, third);
            else
                text3 = String.format(Locale.UK, "%02d"+dateSeparator+"%02d"+dateSeparator+"%04d", first, second, third);

            //Get battery percentage
            String text1 = (batteryLevel+"%");

            //Get day of the week
            String dayOfTheWeek = "";
            if ((isInAmbientMode() && showDayAmbient) || (!isInAmbientMode() && showDay))
                dayOfTheWeek = WeekDays[mCalendar.get(Calendar.DAY_OF_WEEK)-1];

            //Draw digital clock, date, battery percentage and day of the week
            Float firstSeparator = 40.0f;
            if ((isInAmbientMode() && !showSecondary) || (!isInAmbientMode() && !showSecondaryActive)) {
                text = "";
            }
            if (!text.equals("") || !dayOfTheWeek.equals("")) {
                if (!text.equals("") && !dayOfTheWeek.equals("")) {
                    canvas.drawText(text + " • " + dayOfTheWeek, bounds.width()/2, firstSeparator-mTextPaint.ascent(), mTextPaint);
                    firstSeparator = 40-mTextPaint.ascent()+mTextPaint.descent();
                } else if (!text.equals("")) {
                    canvas.drawText(text, bounds.width()/2, firstSeparator-mTextPaint.ascent(), mTextPaint);
                    firstSeparator = 40-mTextPaint.ascent()+mTextPaint.descent();
                } else {
                    canvas.drawText(dayOfTheWeek, bounds.width()/2, firstSeparator-mTextPaint.ascent(), mTextPaint);
                    firstSeparator = 40-mTextPaint.ascent()+mTextPaint.descent();
                }
            }
            if (!((isInAmbientMode() && showSecondaryCalendar) || (!isInAmbientMode() && showSecondaryCalendarActive))) {
                text3="";
            }
            if (!((isInAmbientMode() && showBatteryAmbient) || (!isInAmbientMode() && showBattery))) {
                text1="";
            }
            if (!text3.equals("") || !text1.equals("")) {
                if (!text3.equals("") && !text1.equals("")) {
                    canvas.drawText(text3+" • "+text1, bounds.width() / 2, firstSeparator - mTextPaint.ascent(), mTextPaint);
                    firstSeparator = firstSeparator - mTextPaint.ascent() + mTextPaint.descent();
                } else if (!text3.equals("")) {
                    canvas.drawText(text3, bounds.width() / 2, firstSeparator - mTextPaint.ascent(), mTextPaint);
                    firstSeparator = firstSeparator - mTextPaint.ascent() + mTextPaint.descent();
                } else {
                    canvas.drawText(text1, bounds.width() / 2, firstSeparator - mTextPaint.ascent(), mTextPaint);
                    firstSeparator = firstSeparator - mTextPaint.ascent() + mTextPaint.descent();
                }
            }
            if (firstSeparator<bounds.height()/4)
                firstSeparator=(float)bounds.height()/4;

            //Draw text clock
            if ((isInAmbientMode() && showWordsAmbient) || (!isInAmbientMode() && showWords)) {
                String text2;
                switch (capitalisation) {
                    case 0:
                        text2 = capitalise0(hourText,minutes,index); //every word title case
                        break;
                    case 1:
                        text2 = capitalise1(hourText,minutes,index); //all caps
                        break;
                    case 2:
                        text2 = capitalise2(hourText,minutes,index); //all lowercase
                        break;
                    case 3:
                        text2 = capitalise3(hourText,minutes,index); //first word title case
                        break;
                    case 4:
                        text2 = capitalise4(hourText,minutes,index); //first word in every line title case
                        break;
                    default:
                        text2 = capitalise0(hourText,minutes,index);
                        break;
                }
                float textSize=0;
                float x = bounds.width() / 2;
                if (!complicationLeftSet && !complicationRightSet) {
                    textSize = getTextSizeForWidth(bounds.width() - 48,bounds.height()*3/4-mChinSize-firstSeparator-32, text2);
                    x = bounds.width() / 2;
                } else if (complicationLeftSet && !complicationRightSet) {
                    textSize = getTextSizeForWidth(bounds.width() * 3 / 4 - 24, bounds.height() * 3 / 4 - mChinSize - firstSeparator - 32, text2);
                    x = bounds.width()*5/8-16;
                } else if (!complicationLeftSet && complicationRightSet) {
                    textSize = getTextSizeForWidth(bounds.width() * 3 / 4 - 24, bounds.height() * 3 / 4 - mChinSize - firstSeparator - 32, text2);
                    x = bounds.width()*3/8+16;
                } else {
                    textSize = getTextSizeForWidth(bounds.width() / 2 - 16, bounds.height() * 3 / 4 - mChinSize - firstSeparator - 32, text2);
                    x = bounds.width() / 2;
                }
                mTextPaint2.setTextSize(textSize);
                float y = (bounds.height()*3/4-mChinSize+firstSeparator)/2;
                for (String line : text2.split("\n")) {
                    y += mTextPaint2.descent() - mTextPaint2.ascent();
                }
                float difference = y-(bounds.height()*3/4-mChinSize+firstSeparator)/2;
                y = (bounds.height()*3/4-mChinSize+firstSeparator)/2 - difference/2 -mTextPaint2.ascent();
                for (String line : text2.split("\n")) {
                    canvas.drawText(line, x, y, mTextPaint2);
                    y += mTextPaint2.descent() - mTextPaint2.ascent();
                }
            }

            //Draw complication
            if (((isInAmbientMode() && showComplicationAmbient) || (!isInAmbientMode() && showComplication)) && (android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.N))
                drawComplications(canvas,now);
        }

        private String capitalise0(Integer hours, Integer minutes, Integer index){
            //Prefix
            String mainPrefix = "";
            StringBuilder prefix;
            if ((minutes>0) && (!Prefixes[index].equals("")) && (Prefixes[index]!=null)) {
                String[] prefixArray = Prefixes[index].split(" ");
                prefix = new StringBuilder();
                for (String word : prefixArray) {
                    if (prefix.length()!=0)
                        prefix.append(" ");
                    String capitalised = word.substring(0,1).toUpperCase() + word.substring(1);
                    prefix.append(capitalised);
                }
                mainPrefix = prefix.toString();
            }

            //Time
            StringBuilder hoursInWords = new StringBuilder();
            String mainText;
            String[] mainArray;
            switch (language) {
                case "en":
                    mainArray = getResources().getStringArray(R.array.ExactTimes)[hours].split(" ");
                    break;
                case "de":
                    mainArray = getResources().getStringArray(R.array.ExactTimesDE)[hours].split(" ");
                    break;
                case "lt":
                    mainArray = getResources().getStringArray(R.array.ExactTimesLT)[hours].split(" ");
                    break;
                case "fi":
                    mainArray = getResources().getStringArray(R.array.ExactTimesFI)[hours].split(" ");
                    break;
                case "ru":
                    mainArray = getResources().getStringArray(R.array.ExactTimesRU)[hours].split(" ");
                    break;
                case "hu":
                    mainArray = getResources().getStringArray(R.array.ExactTimesHU)[hours].split(" ");
                    break;
                case "it":
                    mainArray = getResources().getStringArray(R.array.ExactTimesIT)[hours].split(" ");
                    break;
                default:
                    mainArray = getResources().getStringArray(R.array.ExactTimes)[hours].split(" ");

            }
            for (String word : mainArray) {
                if (hoursInWords.length()!=0) {
                    hoursInWords.append(" ");
                    hoursInWords.append(word.substring(0, 1).toUpperCase()).append(word.substring(1));
                }
                else {
                    if (!(mainPrefix.equals("") || PrefixNewLine[index]))
                        hoursInWords.append(word);
                    else
                        hoursInWords.append(word.substring(0, 1).toUpperCase()).append(word.substring(1));
                }
            }
            mainText = hoursInWords.toString();

            //Suffix
            String mainSuffix = "";
            if (showSuffixes) {
                StringBuilder suffix;
                if ((minutes > 0) && (!Suffixes[index].equals("")) && (Suffixes[index] != null)) {
                    if (SuffixNewLine[index]) {
                        String[] suffixArray = Suffixes[index].split(" ");
                        suffix = new StringBuilder();
                        for (String word : suffixArray) {
                            if (suffix.length() != 0)
                                suffix.append(" ");
                            String capitalised = word.substring(0, 1).toUpperCase() + word.substring(1);
                            suffix.append(capitalised);
                        }
                        mainSuffix = suffix.toString();
                    } else {
                        mainSuffix = Suffixes[index].toLowerCase();
                    }
                }
            }
            return mainPrefix + ((minutes>0) ? (PrefixNewLine[index] ? "\n" : "") : "") + mainText + ((minutes>0) ? (SuffixNewLine[index] ? "\n" : "") : "") + mainSuffix;
        }

        private String capitalise1(Integer hours, Integer minutes, Integer index) {
            String middle;
            switch (language) {
                case "en":
                    middle = getResources().getStringArray(R.array.ExactTimes)[hours];
                    break;
                case "de":
                    middle = getResources().getStringArray(R.array.ExactTimesDE)[hours];
                    break;
                case "lt":
                    middle = getResources().getStringArray(R.array.ExactTimesLT)[hours];
                    break;
                case "fi":
                    middle = getResources().getStringArray(R.array.ExactTimesFI)[hours];
                    break;
                case "ru":
                    middle = getResources().getStringArray(R.array.ExactTimesRU)[hours];
                    break;
                case "hu":
                    middle = getResources().getStringArray(R.array.ExactTimesHU)[hours];
                    break;
                case "it":
                    middle = getResources().getStringArray(R.array.ExactTimesIT)[hours];
                    break;
                default:
                    middle = getResources().getStringArray(R.array.ExactTimes)[hours];
            }
            String text =
                    ((minutes>0) ? Prefixes[index] : "")
                            + ((minutes>0) ? (PrefixNewLine[index] ? "\n" : "") : "")
                            + middle
                            + ((minutes>0) ? (SuffixNewLine[index] ? "\n" : "") : "")
                            + ((showSuffixes) ? ((minutes>0) ? Suffixes[index] : "") : "");
            return text.toUpperCase();
        }

        private String capitalise2(Integer hours, Integer minutes, Integer index) {
            String middle;
            switch (language) {
                case "en":
                    middle = getResources().getStringArray(R.array.ExactTimes)[hours];
                    break;
                case "de":
                    middle = getResources().getStringArray(R.array.ExactTimesDE)[hours];
                    break;
                case "lt":
                    middle = getResources().getStringArray(R.array.ExactTimesLT)[hours];
                    break;
                case "fi":
                    middle = getResources().getStringArray(R.array.ExactTimesFI)[hours];
                    break;
                case "ru":
                    middle = getResources().getStringArray(R.array.ExactTimesRU)[hours];
                    break;
                case "hu":
                    middle = getResources().getStringArray(R.array.ExactTimesHU)[hours];
                    break;
                case "it":
                    middle = getResources().getStringArray(R.array.ExactTimesIT)[hours];
                    break;
                default:
                    middle = getResources().getStringArray(R.array.ExactTimes)[hours];
            }
            String text =
                    ((minutes>0) ? Prefixes[index] : "")
                            + ((minutes>0) ? (PrefixNewLine[index] ? "\n" : "") : "")
                            + middle
                            + ((minutes>0) ? (SuffixNewLine[index] ? "\n" : "") : "")
                            + ((showSuffixes) ? ((minutes>0) ? Suffixes[index] : "") : "");

            return text.toLowerCase();
        }

        private String capitalise3(Integer hours, Integer minutes, Integer index) {
            String middle;
            switch (language) {
                case "en":
                    middle = getResources().getStringArray(R.array.ExactTimes)[hours];
                    break;
                case "de":
                    middle = getResources().getStringArray(R.array.ExactTimesDE)[hours];
                    break;
                case "lt":
                    middle = getResources().getStringArray(R.array.ExactTimesLT)[hours];
                    break;
                case "fi":
                    middle = getResources().getStringArray(R.array.ExactTimesFI)[hours];
                    break;
                case "ru":
                    middle = getResources().getStringArray(R.array.ExactTimesRU)[hours];
                    break;
                case "hu":
                    middle = getResources().getStringArray(R.array.ExactTimesHU)[hours];
                    break;
                case "it":
                    middle = getResources().getStringArray(R.array.ExactTimesIT)[hours];
                    break;
                default:
                    middle = getResources().getStringArray(R.array.ExactTimes)[hours];
            }
            String text20 =
                    ((minutes>0) ? Prefixes[index] : "")
                            + ((minutes>0) ? (PrefixNewLine[index] ? "\n" : "") : "")
                            + middle
                            + ((minutes>0) ? (SuffixNewLine[index] ? "\n" : "") : "")
                            + ((showSuffixes) ? ((minutes>0) ? Suffixes[index] : "") : "");
            return text20.substring(0,1).toUpperCase() + text20.substring(1).toLowerCase();
        }

        private String capitalise4(Integer hours, Integer minutes, Integer index) {
            //Prefix
            String mainPrefix = "";
            if ((minutes>0) && (!Prefixes[index].equals("")) && (Prefixes[index]!=null)) {
                mainPrefix = Prefixes[index].substring(0,1).toUpperCase() + Prefixes[index].substring(1).toLowerCase();
            }

            //Time
            String hoursInWords;
            switch (language) {
                case "en":
                    hoursInWords = getResources().getStringArray(R.array.ExactTimes)[hours];
                    break;
                case "de":
                    hoursInWords = getResources().getStringArray(R.array.ExactTimesDE)[hours];
                    break;
                case "lt":
                    hoursInWords = getResources().getStringArray(R.array.ExactTimesLT)[hours];
                    break;
                case "fi":
                    hoursInWords = getResources().getStringArray(R.array.ExactTimesFI)[hours];
                    break;
                case "ru":
                    hoursInWords = getResources().getStringArray(R.array.ExactTimesRU)[hours];
                    break;
                case "hu":
                    hoursInWords = getResources().getStringArray(R.array.ExactTimesHU)[hours];
                    break;
                case "it":
                    hoursInWords = getResources().getStringArray(R.array.ExactTimesIT)[hours];
                    break;
                default:
                    hoursInWords = getResources().getStringArray(R.array.ExactTimes)[hours];
            }
            String mainText;
            if (mainPrefix.equals("") || PrefixNewLine[index])
                mainText = hoursInWords.substring(0,1).toUpperCase() + hoursInWords.substring(1);
            else
                mainText = hoursInWords.toLowerCase();

            //Suffix
            String mainSuffix = "";
            if (showSuffixes) {
                if ((minutes > 0) && (!Suffixes[index].equals("")) && (Suffixes[index] != null)) {
                    if (SuffixNewLine[index]) {
                        mainSuffix = Suffixes[index].substring(0, 1).toUpperCase() + Suffixes[index].substring(1).toLowerCase();
                    } else {
                        mainSuffix = Suffixes[index].toLowerCase();
                    }
                }
            }
            return mainPrefix + ((mCalendar.get(Calendar.MINUTE)>0) ? (PrefixNewLine[index] ? "\n" : "") : "") + mainText + ((mCalendar.get(Calendar.MINUTE)>0) ? (SuffixNewLine[index] ? "\n" : "") : "") + mainSuffix;

        }

        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }

        private float getTextSizeForWidth(float desiredWidth, float desiredHeight, String text) {
            float min = Integer.MAX_VALUE, linecount=0;
            for (String line: text.split("\n")) {
                if (!line.equals(""))
                    linecount++;
                float testTextSize = 100.00f;
                mTextPaint2.setTextSize(testTextSize);
                Rect bounds = new Rect();
                mTextPaint2.getTextBounds(line, 0, line.length(), bounds);
                float desiredTextSize = testTextSize*desiredWidth/bounds.width();
                float desiredTextSize2 = testTextSize*(desiredHeight)/(bounds.height()+mTextPaint2.descent())/linecount;
                if (desiredTextSize<min)
                    min=desiredTextSize;
                if (desiredTextSize2<min)
                    min=desiredTextSize2;
            }
            Paint newPaint = mTextPaint2;
            newPaint.setTextSize(min);
            while(newPaint.measureText("|",0,"|".length())/5 > 6) { //6 is the burn in protection shifting limit in pixels
                min-=2;
                newPaint.setTextSize(min);
            }
            return min;
        }
    }
}
