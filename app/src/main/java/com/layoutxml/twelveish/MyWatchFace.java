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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationHelperActivity;
import android.support.wearable.complications.rendering.ComplicationDrawable;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.WindowInsets;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.layoutxml.twelveish.config.ComplicationConfigActivity;
import com.layoutxml.twelveish.activities.list_activities.ActivityImageViewActivity;
import com.layoutxml.twelveish.objects.WordClockTaskWrapper;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static android.view.Gravity.CENTER_HORIZONTAL;
import static android.view.Gravity.TOP;

public class MyWatchFace extends CanvasWatchFaceService {

    private static final String TAG = "MyWatchFace";
    private final String path = "/twelveish";
    private final String DATA_KEY = "rokas-twelveish";
    private final String HANDSHAKE_KEY = "rokas-twelveish-hs";
    private final String GOODBYE_KEY = "rokas-twelveish-gb";
    private final String DATA_REQUEST_KEY = "rokas-twelveish-dr";
    private final String DATA_REQUEST_KEY2 = "rokas-twelveish-dr2";
    private final String CONFIG_REQUEST_KEY = "rokas-twelveish-cr";
    private final String CONFIG_REQUEST_KEY2 = "rokas-twelveish-cr2";
    private final String PREFERENCES_KEY = "rokas-twelveish-pr";
    private static Typeface NORMAL_TYPEFACE = Typeface.create("sans-serif-light", Typeface.NORMAL);
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);
    private static final int MSG_UPDATE_TIME = 0;
    private String[] Prefixes;
    private String[] Suffixes;
    private String[] WeekDays;
    private int[] TimeShift = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1};
    private boolean[] PrefixNewLine = new boolean[]{false, false, true, true, true, true, true, true, true, true, true, true};
    private boolean[] SuffixNewLine = new boolean[]{false, true, false, true, false, false, false, true, false, true, false, false};
    private SharedPreferences prefs;
    private int batteryLevel = 100;
    private int screenWidthG;
    private int screenHeightG;
    private String text3 = "";
    private String dayOfTheWeek = "";
    private String text1 = "";
    private String text1backup = "";
    private boolean significantTimeChange = true;
    private String text2 = "";
    private float x;
    private float basey = -1;
    private int lastSignificantMinutes = -1;
    private int lastSignificantHours = -1;
    private WordClockTask wordClockTask;
    private int secondaryTextSizeDP = 14;
    //SharedPreferences:
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
    private int mainTextOffset=0;
    private int secondaryTextOffset=0;
    private String font;
    //Intent constants
    private static final String TRANSITION_TO_AMBIENT_MODE = "com.rokasjankunas.ticktock.TRANSITION_TO_AMBIENT_MODE";
    private static final String TRANSITION_TO_INTERACTIVE_MODE = "com.rokasjankunas.ticktock.TRANSITION_TO_INTERACTIVE_MODE";
    //Complications and their data
    private boolean complicationLeftSet;
    private boolean complicationRightSet;
    private static final int BOTTOM_COMPLICATION_ID = 0;
    private static final int LEFT_COMPLICATION_ID = 1;
    private static final int RIGHT_COMPLICATION_ID = 2;
    private static final int[] COMPLICATION_IDS = {BOTTOM_COMPLICATION_ID, LEFT_COMPLICATION_ID, RIGHT_COMPLICATION_ID};
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
                batteryLevel = (int) (100 * intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) / ((float) (intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1))));
                text1 = (batteryLevel + "%");
            }
        }, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return new Engine();
    }

    public static int getComplicationId(ComplicationConfigActivity.ComplicationLocation complicationLocation) {
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
                return new int[]{};
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

    private class Engine extends CanvasWatchFaceService.Engine implements DataClient.OnDataChangedListener, WordClockListener {

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
        private boolean mAmbient = false;

        /**
         * Creates watch face
         * @param holder holder
         */
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
            getDate();

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                initializeComplications();

            mTextPaint.setColor(secondaryColor);
            mTextPaint2.setColor(mainColor);

        }

        /**
         * Gets date
         */
        private void getDate() {
            //Get date
            int first, second, third;
            boolean FourFirst;
            switch (dateOrder) {
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
                text3 = String.format(Locale.UK, "%04d" + dateSeparator + "%02d" + dateSeparator + "%02d", first, second, third);
            else
                text3 = String.format(Locale.UK, "%02d" + dateSeparator + "%02d" + dateSeparator + "%04d", first, second, third);


            //Get day of the week
            if ((mAmbient && showDayAmbient) || (!mAmbient && showDay))
                dayOfTheWeek = WeekDays[mCalendar.get(Calendar.DAY_OF_WEEK) - 1];
            else
                dayOfTheWeek = "";
        }

        /**
         * Asks user to rate
         */
        private void showRateNotification() {
            int notificationId = 1;
            String id = "Main";
            Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.layoutxml.twelveish"));
            viewIntent.putExtra("Rate Twelveish", "Would you like to rate Twelveish? I won't ask again :)");
            PendingIntent viewPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, viewIntent, 0);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(getApplicationContext(), id)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Rate Twelveish")
                            .setContentText("Would you like to rate Twelveish? Tap to go to the Google Play store.")
                            .setContentIntent(viewPendingIntent);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            notificationManager.notify(notificationId, notificationBuilder.build());
        }

        /**
         * Shows notification to tweak settings for the first time
         */
        private void showTutorialNotification() {
            int notificationId = 2;
            String id = "Main";
            Intent viewIntent = new Intent(getApplicationContext(), ActivityImageViewActivity.class);
            viewIntent.putExtra("Open settings", "Don't forget to customize the watch");
            PendingIntent viewPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, viewIntent, 0);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(getApplicationContext(), id)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Open Twelveish Settings")
                            .setContentText("Don't forget to customize Twelveish directly on your watch")
                            .setContentIntent(viewPendingIntent);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            notificationManager.notify(notificationId, notificationBuilder.build());
        }

        /**
         * Asks user to donate
         */
        private void showDonateNotification() {
            int notificationId = 3;
            String id = "Main";
            Intent viewIntent = new Intent(getApplicationContext(), ActivityImageViewActivity.class);
            viewIntent.putExtra("Donate", "Don't forget to donate");
            PendingIntent viewPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, viewIntent, 0);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(getApplicationContext(), id)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Donate to LayoutXML")
                            .setContentText("Would you like to donate for Twelveish? Read more on Google Play.")
                            .setContentIntent(viewPendingIntent);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            notificationManager.notify(notificationId, notificationBuilder.build());
        }

        /**
         * Initializes complications
         */
        private void initializeComplications() {
            mActiveComplicationDataSparseArray = new SparseArray<>(COMPLICATION_IDS.length);
            ComplicationDrawable bottomComplicationDrawable = (ComplicationDrawable) getDrawable(R.drawable.custom_complication_styles);
            ComplicationDrawable leftComplicationDrawable = (ComplicationDrawable) getDrawable(R.drawable.custom_complication_styles);
            ComplicationDrawable rightComplicationDrawable = (ComplicationDrawable) getDrawable(R.drawable.custom_complication_styles);
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

        /**
         * Updates complication data
         * @param complicationId complication id
         * @param complicationData complication data
         */
        @Override
        public void onComplicationDataUpdate(
                int complicationId, ComplicationData complicationData) {
            mActiveComplicationDataSparseArray.put(complicationId, complicationData);
            ComplicationDrawable complicationDrawable = mComplicationDrawableSparseArray.get(complicationId);
            complicationDrawable.setComplicationData(complicationData);
            invalidate();
        }

        /**
         * Acts on surface change
         * @param holder holder
         * @param format format
         * @param width width
         * @param height height
         */
        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            screenHeightG = height;
            screenWidthG = width;
        }

        /**
         * Loads preferences
         */
        private void loadPreferences() {
            boolean showedRateAlready = prefs.getBoolean(getString(R.string.showed_rate), false);
            int counter = prefs.getInt(getString(R.string.counter), 0);
            boolean showedTutorialAlready = prefs.getBoolean(getString(R.string.showed_tutorial), false);
            boolean showedDonateAlready = prefs.getBoolean(getString(R.string.showed_donate), false);
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
            showSecondary = prefs.getBoolean(getString(R.string.preference_show_secondary), true);
            showSecondaryActive = prefs.getBoolean(getString(R.string.preference_show_secondary_active), true);
            showSecondaryCalendar = prefs.getBoolean(getString(R.string.preference_show_secondary_calendar), true);
            showSecondaryCalendarActive = prefs.getBoolean(getString(R.string.preference_show_secondary_calendar_active), true);
            showBattery = prefs.getBoolean(getString(R.string.preference_show_battery), true);
            showBatteryAmbient = prefs.getBoolean(getString(R.string.preference_show_battery_ambient), true);
            showDay = prefs.getBoolean(getString(R.string.preference_show_day), true);
            showDayAmbient = prefs.getBoolean(getString(R.string.preference_show_day_ambient), true);
            showSeconds = prefs.getBoolean(getString(R.string.preference_show_seconds), true);
            showComplication = prefs.getBoolean(getString(R.string.preference_show_complications), true);
            showComplicationAmbient = prefs.getBoolean(getString(R.string.preference_show_complications_ambient), true);
            language = prefs.getString(getString(R.string.preference_language), "en");
            font = prefs.getString(getString(R.string.preference_font), "robotolight");
            disableComplicationTap = prefs.getBoolean(getString(R.string.preference_tap), false);
            complicationLeftSet = prefs.getBoolean(getString(R.string.complication_left_set), false);
            complicationRightSet = prefs.getBoolean(getString(R.string.complication_right_set), false);
            mainTextOffset = prefs.getInt(getString(R.string.main_text_size_offset),0);
            secondaryTextOffset = prefs.getInt(getString(R.string.secondary_text_size_offset),0);

            mTextPaint.setTextSize(getResources().getDisplayMetrics().heightPixels * 0.06f +secondaryTextOffset); //secondary text

            //Work with given preferences
            switch (language) {
                case "nl":
                    Prefixes = getResources().getStringArray(R.array.PrefixesNL);
                    Suffixes = getResources().getStringArray(R.array.SuffixesNL);
                    WeekDays = getResources().getStringArray(R.array.WeekDaysNL);
                    TimeShift = new int[]{0,0,0,1,1,1,1,1,1,1,1,1};
                    PrefixNewLine = new boolean[]{true,true,true,true,true,true,true,true,true,true,true,true};
                    SuffixNewLine = new boolean[]{false,false,false,false,false,false,false,false,false,false,false,false};
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
                    TimeShift = new int[]{0,0,0,0,0,0,0,1,1,1,1,1};
                    PrefixNewLine = new boolean[]{true,false,true,true,true,true,true,true,true,true,true,true};
                    SuffixNewLine = new boolean[]{false,true,true,true,true,true,true,true,true,true,false,false};
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
                    TimeShift = new int[]{0,0,0,0,1,1,1,1,1,1,1,1};
                    PrefixNewLine = new boolean[]{false,true,true,true,true,true,true,true,true,true,true,true};
                    SuffixNewLine = new boolean[]{true,false,false,false,false,false,false,false,false,false,false,false};
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
                    TimeShift = new int[]{0,0,0,0,0,0,0,0,1,1,1,1};
                    PrefixNewLine = new boolean[]{false,true,false,false,false,false,false,true,false,false,false,false};
                    SuffixNewLine = new boolean[]{false,false,true,true,true,true,true,true,true,true,true,true};
                    break;
                case "pt":
                    Prefixes = getResources().getStringArray(R.array.PrefixesPT);
                    Suffixes = getResources().getStringArray(R.array.SuffixesPT);
                    WeekDays = getResources().getStringArray(R.array.WeekDaysPT);
                    TimeShift = new int[]{0,0,0,0,0,0,0,0,1,1,1,1};
                    PrefixNewLine = new boolean[]{false,false,true,false,true,true,false,true,true,false,false,true};
                    SuffixNewLine = new boolean[]{true,true,true,true,true,true,true,true,true,true,true,false};
                    break;
                case "sv":
                    Prefixes = getResources().getStringArray(R.array.PrefixesSV);
                    Suffixes = getResources().getStringArray(R.array.SuffixesSV);
                    WeekDays = getResources().getStringArray(R.array.WeekDaysSV);
                    TimeShift = new int[]{0,0,0,0,1,1,1,1,1,1,1,1};
                    PrefixNewLine = new boolean[]{false,true,true,true,true,true,true,true,true,true,true,true};
                    SuffixNewLine = new boolean[]{true,false,false,false,false,false,true,false,false,false,false,false};
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

            Typeface NORMAL_TYPEFACE2;
            switch (font) {
                case "robotolight":
                    NORMAL_TYPEFACE2 = Typeface.create("sans-serif-light", Typeface.NORMAL);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                    break;
                case "alegreya":
                    NORMAL_TYPEFACE2 = ResourcesCompat.getFont(getApplicationContext(), R.font.alegreya);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                    break;
                case "cabin":
                    NORMAL_TYPEFACE2 = ResourcesCompat.getFont(getApplicationContext(), R.font.cabin);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                    break;
                case "ibmplexsans":
                    NORMAL_TYPEFACE2 = ResourcesCompat.getFont(getApplicationContext(), R.font.ibmplexsans);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                    break;
                case "inconsolata":
                    NORMAL_TYPEFACE2 = ResourcesCompat.getFont(getApplicationContext(), R.font.inconsolata);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                    break;
                case "merriweather":
                    NORMAL_TYPEFACE2 = ResourcesCompat.getFont(getApplicationContext(), R.font.merriweather);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                    break;
                case "nunito":
                    NORMAL_TYPEFACE2 = ResourcesCompat.getFont(getApplicationContext(), R.font.nunito);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                    break;
                case "pacifico":
                    NORMAL_TYPEFACE2 = ResourcesCompat.getFont(getApplicationContext(), R.font.pacifico);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                    break;
                case "quattrocento":
                    NORMAL_TYPEFACE2 = ResourcesCompat.getFont(getApplicationContext(), R.font.quattrocento);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                    break;
                case "quicksand":
                    NORMAL_TYPEFACE2 = ResourcesCompat.getFont(getApplicationContext(), R.font.quicksand);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                    break;
                case "rubik":
                    NORMAL_TYPEFACE2 = ResourcesCompat.getFont(getApplicationContext(), R.font.rubik);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                    break;
                default:
                    NORMAL_TYPEFACE2 = Typeface.create("sans-serif-light", Typeface.NORMAL);
                    mTextPaint2.setTypeface(NORMAL_TYPEFACE2);
                    break;
            }

            invalidate();

            if (counter >= 100 && !showedRateAlready) {
                prefs.edit().putBoolean(getString(R.string.showed_rate), true).apply();
                showRateNotification();
            }

            counter++;
            if (counter < 202) {
                prefs.edit().putInt(getString(R.string.counter), counter).apply();
            }

            if (!showedTutorialAlready && counter > 30) {
                prefs.edit().putBoolean(getString(R.string.showed_tutorial), true).apply();
                showTutorialNotification();
            }

            if (counter >= 200 && !showedDonateAlready) {
                prefs.edit().putBoolean(getString(R.string.showed_donate), true).apply();
                showDonateNotification();
            }
        }

        /**
         * Destroys watch face
         */
        @Override
        public void onDestroy() {
            super.onDestroy();

            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);

            final PutDataMapRequest mPutDataMapRequest = PutDataMapRequest.create(path);
            mPutDataMapRequest.getDataMap().putBoolean(GOODBYE_KEY, true);
            mPutDataMapRequest.setUrgent();
            PutDataRequest mPutDataRequest = mPutDataMapRequest.asPutDataRequest();
            Wearable.getDataClient(getApplicationContext()).putDataItem(mPutDataRequest);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPutDataMapRequest.getDataMap().clear();
                }
            }, 5000);

            if (wordClockTask!=null) {
                if (wordClockTask.getStatus()!= AsyncTask.Status.FINISHED) {
                    wordClockTask.cancel(true);
                }
                wordClockTask = null;
            }
        }

        /**
         * Acts on visibility change
         * @param visible is visible
         */
        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                registerReceiver();
                mCalendar.setTimeZone(TimeZone.getDefault());
                loadPreferences();
                Wearable.getDataClient(getApplicationContext()).addListener(this);

                final PutDataMapRequest mPutDataMapRequest = PutDataMapRequest.create(path);
                mPutDataMapRequest.getDataMap().putLong("Timestamp", System.currentTimeMillis());
                mPutDataMapRequest.getDataMap().putBoolean(HANDSHAKE_KEY, true);
                mPutDataMapRequest.setUrgent();
                PutDataRequest mPutDataRequest = mPutDataMapRequest.asPutDataRequest();
                Wearable.getDataClient(getApplicationContext()).putDataItem(mPutDataRequest);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPutDataMapRequest.getDataMap().clear();
                    }
                }, 5000);
            } else {
                unregisterReceiver();
                Wearable.getDataClient(getApplicationContext()).removeListener(this);
            }
            updateTimer();
            significantTimeChange = true;
            getDate();
        }

        /**
         * Registers all receivers
         */
        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            MyWatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        /**
         * Unregisters all receivers
         */
        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            MyWatchFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        /**
         * Acts on screen size change
         * @param insets insets
         */
        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);
            mChinSize = insets.getSystemWindowInsetBottom();
            mTextPaint.setTextSize(getResources().getDisplayMetrics().heightPixels * 0.06f +secondaryTextOffset); //secondary text
            mTextPaint2.setTextSize(24+mainTextOffset); //word clock
            Rect bottomBounds = new Rect(screenWidthG / 2 - screenWidthG / 4,
                    (int) (screenHeightG * 3 / 4 - mChinSize),
                    screenWidthG / 2 + screenWidthG / 4,
                    (int) (screenHeightG - mChinSize));
            Rect leftBounds = new Rect(0,
                    screenHeightG * 3 / 8,
                    screenWidthG / 4,
                    screenHeightG * 5 / 8);
            Rect rightBounds = new Rect(screenWidthG * 3 / 4,
                    screenHeightG * 3 / 8,
                    screenWidthG,
                    screenHeightG * 5 / 8);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ComplicationDrawable bottomComplicationDrawable = mComplicationDrawableSparseArray.get(BOTTOM_COMPLICATION_ID);
                bottomComplicationDrawable.setBounds(bottomBounds);
                ComplicationDrawable leftComplicationDrawable = mComplicationDrawableSparseArray.get(LEFT_COMPLICATION_ID);
                leftComplicationDrawable.setBounds(leftBounds);
                ComplicationDrawable rightComplicationDrawable = mComplicationDrawableSparseArray.get(RIGHT_COMPLICATION_ID);
                rightComplicationDrawable.setBounds(rightBounds);
            }
        }

        /**
         * Automatically generated method
         */
        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        /**
         * Automatically generated method
         */
        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        /**
         * Reacts to ambiend mode changes
         * @param inAmbientMode is new mode ambient mode
         */
        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            mAmbient = inAmbientMode;
            if (mLowBitAmbient) {
                mTextPaint.setAntiAlias(!inAmbientMode);
                mTextPaint2.setAntiAlias(!inAmbientMode);
            }
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ComplicationDrawable complicationDrawable;
                for (int COMPLICATION_ID : COMPLICATION_IDS) {
                    complicationDrawable = mComplicationDrawableSparseArray.get(COMPLICATION_ID);
                    complicationDrawable.setInAmbientMode(mAmbient);
                }
            }
            if (mAmbient) {
                Intent intent = new Intent();
                intent.setAction(TRANSITION_TO_AMBIENT_MODE);
                intent.putExtra("package", getPackageName());
                sendBroadcast(intent, "com.rokasjankunas.ticktock.AMBIENT_INTERACTIVE_MODE_CHANGE");
            } else {
                Intent intent = new Intent();
                intent.setAction(TRANSITION_TO_INTERACTIVE_MODE);
                intent.putExtra("package", getPackageName());
                sendBroadcast(intent, "com.rokasjankunas.ticktock.AMBIENT_INTERACTIVE_MODE_CHANGE");
            }
            updateTimer();
            significantTimeChange = true;
            getDate();
        }

        /**
         * Performs user input actions
         * @param tapType tap type
         * @param x x
         * @param y y
         * @param eventTime time
         */
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
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !disableComplicationTap) {
                        int tappedComplicationId = getTappedComplicationId(x, y);
                        if (tappedComplicationId != -1) {
                            onComplicationTap(tappedComplicationId);
                        }
                    }
                    break;
            }
            invalidate();
        }

        /**
         * Return complication in given coordinates
         * @param x x
         * @param y y
         * @return complication id
         */
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
                    }
                }
            }
            return -1;
        }

        /**
         * Completes complication tap action
         * @param complicationId complication id
         */
        private void onComplicationTap(int complicationId) {
            ComplicationData complicationData = mActiveComplicationDataSparseArray.get(complicationId);
            if (complicationData != null) {
                if (complicationData.getTapAction() != null) {
                    try {
                        complicationData.getTapAction().send();
                    } catch (PendingIntent.CanceledException ignored) {
                    }
                } else {
                    if (complicationData.getType() == ComplicationData.TYPE_NO_PERMISSION) {
                        ComponentName componentName =
                                new ComponentName(
                                        getApplicationContext(), MyWatchFace.class);
                        Intent permissionRequestIntent =
                                ComplicationHelperActivity.createPermissionRequestHelperIntent(
                                        getApplicationContext(), componentName);
                        startActivity(permissionRequestIntent);
                    }
                }
            }
        }

        /**
         * Method that draws all elements on canvas
         * @param canvas canvas
         * @param bounds bounds
         */
        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            //Set colors
            if (mAmbient) {
                canvas.drawColor(Color.BLACK);
                mTextPaint.setColor(secondaryColorAmbient);
                mTextPaint2.setColor(mainColorAmbient);
            } else {
                canvas.drawColor(backgroundColor);
                mTextPaint.setColor(secondaryColor);
                mTextPaint2.setColor(mainColor);
            }

            //Get time
            mCalendar = Calendar.getInstance();
            int seconds = mCalendar.get(Calendar.SECOND);
            int minutes = mCalendar.get(Calendar.MINUTE);
            if ((minutes%5==0 || minutes==1) && (seconds<2)) {
                significantTimeChange = true;
                getDate();
            }
            int hourDigital = militaryTime ? mCalendar.get(Calendar.HOUR_OF_DAY) : mCalendar.get(Calendar.HOUR);
            if (hourDigital == 0 && !militaryTime)
                hourDigital = 12;
            if (hourDigital-lastSignificantHours!=0 || minutes-lastSignificantMinutes>5 || lastSignificantMinutes-minutes<-5) {
                significantTimeChange = true;
                getDate();
            }

            //Get digital clock
            String ampmSymbols = (!militaryTime) ? (mCalendar.get(Calendar.HOUR_OF_DAY) >= 12 ? " pm" : " am") : "";
            String text = (mAmbient || !showSeconds)
                    ? String.format(Locale.UK, "%d:%02d" + ampmSymbols, hourDigital, minutes)
                    : String.format(Locale.UK, "%d:%02d:%02d" + ampmSymbols, hourDigital, minutes, seconds);

            //Draw digital clock, date, battery percentage and day of the week
            float firstSeparator = 40.0f;
            if ((mAmbient && !showSecondary) || (!mAmbient && !showSecondaryActive)) {
                text = "";
            }
            if (!text.equals("") || !dayOfTheWeek.equals("")) {
                if (!text.equals("") && !dayOfTheWeek.equals("")) {
                    canvas.drawText(text + " • " + dayOfTheWeek, bounds.width() / 2.0f, firstSeparator - mTextPaint.ascent(), mTextPaint);
                    firstSeparator = 40 - mTextPaint.ascent() + mTextPaint.descent();
                } else if (!text.equals("")) {
                    canvas.drawText(text, bounds.width() / 2.0f, firstSeparator - mTextPaint.ascent(), mTextPaint);
                    firstSeparator = 40 - mTextPaint.ascent() + mTextPaint.descent();
                } else {
                    canvas.drawText(dayOfTheWeek, bounds.width() / 2.0f, firstSeparator - mTextPaint.ascent(), mTextPaint);
                    firstSeparator = 40 - mTextPaint.ascent() + mTextPaint.descent();
                }
            }
            if (!((mAmbient && showSecondaryCalendar) || (!mAmbient && showSecondaryCalendarActive))) {
                text3 = "";
            }
            if (!((mAmbient && showBatteryAmbient) || (!mAmbient && showBattery))) {
                if (!text1.equals("")) {
                    text1backup = text1;
                }
                text1 = "";
            } else if (text1.equals("")) {
                text1 = text1backup;
            }
            if (!text3.equals("") || !text1.equals("")) {
                if (!text3.equals("") && !text1.equals("")) {
                    canvas.drawText(text3 + " • " + text1, bounds.width() / 2.0f, firstSeparator - mTextPaint.ascent(), mTextPaint);
                    firstSeparator = firstSeparator - mTextPaint.ascent() + mTextPaint.descent();
                } else if (!text3.equals("")) {
                    canvas.drawText(text3, bounds.width() / 2.0f, firstSeparator - mTextPaint.ascent(), mTextPaint);
                    firstSeparator = firstSeparator - mTextPaint.ascent() + mTextPaint.descent();
                } else {
                    canvas.drawText(text1, bounds.width() / 2.0f, firstSeparator - mTextPaint.ascent(), mTextPaint);
                    firstSeparator = firstSeparator - mTextPaint.ascent() + mTextPaint.descent();
                }
            }
            if (firstSeparator < bounds.height() / 4)
                firstSeparator = (float) bounds.height() / 4;

            if (text2.equals(""))
                significantTimeChange = true;
            if (basey==-1)
                significantTimeChange = true;

            //Draw text clock
            if (significantTimeChange) {
                lastSignificantMinutes = minutes;
                lastSignificantHours = hourDigital;
                int index = minutes / 5;
                int hourText = militaryTextTime ? mCalendar.get(Calendar.HOUR_OF_DAY) + TimeShift[index] : mCalendar.get(Calendar.HOUR) + TimeShift[index];
                if (hourText >= 24 && militaryTextTime)
                    hourText -= 24;
                else if (hourText > 12 && !militaryTextTime)
                    hourText -= 12;
                if (hourText == 0 && !militaryTextTime)
                    hourText = 12;
                wordClockTask = new WordClockTask(new WeakReference<Context>(getApplicationContext()),font,capitalisation,hourText,minutes,index,Prefixes,Suffixes,
                        PrefixNewLine,SuffixNewLine,language,true,false,complicationLeftSet,complicationRightSet,bounds.width(),
                        bounds.height(),firstSeparator,mChinSize,mainTextOffset,new WeakReference<WordClockListener>(this));
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
            if (((mAmbient && showComplicationAmbient) || (!mAmbient && showComplication)) && (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)) {
                long now = System.currentTimeMillis();
                drawComplications(canvas, now);
            }
        }

        /**
         * Receives word clock text
         * @param wordClockTaskWrapper wordClockTaskWrapper
         */
        @Override
        public void wordClockListener(WordClockTaskWrapper wordClockTaskWrapper) {
            basey = wordClockTaskWrapper.getBasey();
            text2 = wordClockTaskWrapper.getText();
            prefs.edit().putInt(getString(R.string.main_text_size_real),(int)wordClockTaskWrapper.getTextSize()).apply();
            mTextPaint2.setTextSize(wordClockTaskWrapper.getTextSize()+mainTextOffset);
            x = wordClockTaskWrapper.getX();
            invalidate();
        }

        /**
         * Automatically generated method
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Automatically generated method
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !mAmbient;
        }

        /**
         * Automatically generated method
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }

        /**
         * Receives data shared between phone and wearable
         * @param dataEventBuffer dataEventBuffer
         */
        @Override
        public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
            for (DataEvent event: dataEventBuffer) {
                if (event.getType()==DataEvent.TYPE_CHANGED && event.getDataItem().getUri().getPath()!=null && event.getDataItem().getUri().getPath().equals(path)) {
                    processData(event.getDataItem());
                }
            }
        }

        /**
         * Processes received data between phone and wearable
         * @param dataItem dataItem
         */
        private void processData(DataItem dataItem) {
            DataMapItem mDataMapItem = DataMapItem.fromDataItem(dataItem);
            String[] array = mDataMapItem.getDataMap().getStringArray(DATA_KEY);
            if (array!=null && array.length==3) {
                Toast.makeText(getApplicationContext(),"Preference received",Toast.LENGTH_SHORT).show();
                switch (array[2]) {
                    case "String":
                        prefs.edit().putString(array[0],array[1]).apply();
                        break;
                    case "Integer":
                        try {
                            int newPref = Integer.parseInt(array[1]);
                            prefs.edit().putInt(array[0],newPref).apply();
                        } catch (NumberFormatException e) {
                            Toast.makeText(getApplicationContext(), "Preference error", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Boolean":
                        if (array[1].equalsIgnoreCase("true") || array[1].equalsIgnoreCase("false")) {
                            boolean newPref2 = Boolean.parseBoolean(array[1]);
                            prefs.edit().putBoolean(array[0],newPref2).apply();
                        } else {
                            Toast.makeText(getApplicationContext(), "Preference error", Toast.LENGTH_SHORT).show();
                        }
                    default:
                        Log.d(TAG,"Unknown type in processData");
                        break;
                }
                loadPreferences();
                getDate(); //forces date refresh in case it is changed
                significantTimeChange = true;
            }
            if (array!=null && array.length > 3){ // We're receiving multiple preferences at once
                for(int i = 0; i < array.length; i+=3){
                    switch(array[i + 2]){
                        case "String":
                            prefs.edit().putString(array[i], array[i+1]).apply();
                            break;
                        case "Integer":
                            int newPref = Integer.parseInt(array[i+1]);
                            prefs.edit().putInt(array[i], newPref).apply();
                            break;
                        case "Boolean":
                            if (array[i + 1].equalsIgnoreCase("true") || array[i+1].equalsIgnoreCase("false")){
                                boolean newPref2 = Boolean.parseBoolean(array[i+1]);
                                prefs.edit().putBoolean(array[i], newPref2).apply();
                            } else {
                                Toast.makeText(getApplicationContext(), "Preference error", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "processData: Boolean was neither true nor false! Key: " + array[i]);
                            }
                            break;
                        default:
                            Log.d(TAG, "Unkown type in processData. Index: " + i);
                    }
                }

                loadPreferences();
                getDate(); //forces date refresh in case it is changed
                significantTimeChange = true;

            }
            boolean handshake = mDataMapItem.getDataMap().getBoolean(HANDSHAKE_KEY);
            if (!handshake) {
                final PutDataMapRequest mPutDataMapRequest = PutDataMapRequest.create(path);
                mPutDataMapRequest.getDataMap().putLong("Timestamp", System.currentTimeMillis());
                mPutDataMapRequest.getDataMap().putBoolean(HANDSHAKE_KEY, true);
                mPutDataMapRequest.setUrgent();
                PutDataRequest mPutDataRequest = mPutDataMapRequest.asPutDataRequest();
                Wearable.getDataClient(getApplicationContext()).putDataItem(mPutDataRequest);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPutDataMapRequest.getDataMap().clear();
                    }
                }, 5000);
            }
            boolean preferences = mDataMapItem.getDataMap().getBoolean(DATA_REQUEST_KEY);
            if (preferences) {
                Map<String, ?> prefMap = prefs.getAll();
                int index = 0;
                String[] preferencesToSend = new String[prefMap.size() * 2];

                for(Map.Entry<String, ?> entry : prefMap.entrySet()){
                    String key = entry.getKey();
                    String value = entry.getValue().toString();

                    preferencesToSend[index] = key;
                    preferencesToSend[index+1] = value;
                    index += 2;
                }

                final PutDataMapRequest mPutDataMapRequest = PutDataMapRequest.create(path);
                mPutDataMapRequest.getDataMap().putLong("Timestamp", System.currentTimeMillis());
                mPutDataMapRequest.getDataMap().putStringArray(PREFERENCES_KEY, preferencesToSend);
                mPutDataMapRequest.getDataMap().putBoolean(DATA_REQUEST_KEY, false);
                mPutDataMapRequest.getDataMap().putBoolean(DATA_REQUEST_KEY2, true);
                mPutDataMapRequest.setUrgent();
                PutDataRequest mPutDataRequest = mPutDataMapRequest.asPutDataRequest();
                Wearable.getDataClient(getApplicationContext()).putDataItem(mPutDataRequest);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPutDataMapRequest.getDataMap().clear();
                    }
                }, 5000);
            }
            boolean config = mDataMapItem.getDataMap().getBoolean(CONFIG_REQUEST_KEY);
            if (config) {
                Log.d(TAG, "processData: config");
                String[] configToSend = new String[3];
                configToSend[0] = (int)mChinSize + "";
                configToSend[1] = complicationLeftSet ? "true" : "false";
                configToSend[2] = complicationRightSet ? "true" : "false";

                final PutDataMapRequest mPutDataMapRequest = PutDataMapRequest.create(path);
                mPutDataMapRequest.getDataMap().putLong("Timestamp", System.currentTimeMillis());
                mPutDataMapRequest.getDataMap().putStringArray(PREFERENCES_KEY, configToSend);
                mPutDataMapRequest.getDataMap().putBoolean(CONFIG_REQUEST_KEY, false);
                mPutDataMapRequest.getDataMap().putBoolean(CONFIG_REQUEST_KEY2, true);
                mPutDataMapRequest.setUrgent();
                PutDataRequest mPutDataRequest = mPutDataMapRequest.asPutDataRequest();
                Wearable.getDataClient(getApplicationContext()).putDataItem(mPutDataRequest);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPutDataMapRequest.getDataMap().clear();
                    }
                }, 5000);
            }
        }
    }
}
