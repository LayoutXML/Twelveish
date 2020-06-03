
package com.layoutxml.twelveish;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.Wearable;
import com.layoutxml.twelveish.activities.list_activities.ActivityImageViewActivity;
import com.layoutxml.twelveish.objects.WordClockTaskWrapper;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static android.view.Gravity.CENTER_HORIZONTAL;
import static android.view.Gravity.TOP;

public class WatchFace extends CanvasWatchFaceService {
    private PreferenceManager preferenceManager;
    private Communicator communicator;
    private ComplicationManager complicationManager;

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
    //Intent constants
    private static final String TRANSITION_TO_AMBIENT_MODE = "com.rokasjankunas.ticktock.TRANSITION_TO_AMBIENT_MODE";
    private static final String TRANSITION_TO_INTERACTIVE_MODE = "com.rokasjankunas.ticktock.TRANSITION_TO_INTERACTIVE_MODE";

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

    private static class EngineHandler extends Handler {
        private final WeakReference<WatchFace.Engine> mWeakReference;

        EngineHandler(WatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            WatchFace.Engine engine = mWeakReference.get();
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

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(WatchFace.this)
                    .setStatusBarGravity(CENTER_HORIZONTAL | TOP)
                    .setShowUnreadCountIndicator(true)
                    .setAcceptsTapEvents(true)
                    .build());

            mCalendar = Calendar.getInstance();

            preferenceManager = new PreferenceManager(getApplicationContext());
            communicator = new Communicator(getApplicationContext(), preferenceManager);
            complicationManager = new ComplicationManager(getApplicationContext(), preferenceManager);

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
            forceRefresh();

            complicationManager.initializeComplications();
            setActiveComplications(ComplicationManager.getComplicationIds());

            mTextPaint.setColor(preferenceManager.getSecondaryColor());
            mTextPaint2.setColor(preferenceManager.getMainColor());
        }

        private void getDate() {
            //Get date
            int first, second, third;
            boolean FourFirst;
            switch (preferenceManager.getDateOrder()) {
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
                text3 = String.format(Locale.UK, "%04d" + preferenceManager.getDateSeparator() + "%02d" + preferenceManager.getDateSeparator() + "%02d", first, second, third);
            else
                text3 = String.format(Locale.UK, "%02d" + preferenceManager.getDateSeparator() + "%02d" + preferenceManager.getDateSeparator() + "%04d", first, second, third);


            //Get day of the week
            if ((mAmbient && preferenceManager.isShowDayAmbient()) || (!mAmbient && preferenceManager.isShowDay()))
                dayOfTheWeek = WeekDays[mCalendar.get(Calendar.DAY_OF_WEEK) - 1];
            else
                dayOfTheWeek = "";
        }

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

        @Override
        public void onComplicationDataUpdate(int complicationId, ComplicationData complicationData) {
            complicationManager.updateData(complicationId, complicationData);
            invalidate();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            screenHeightG = height;
            screenWidthG = width;
        }

        private void loadPreferences() {
            boolean showedRateAlready = prefs.getBoolean(getString(R.string.showed_rate), false);
            int counter = prefs.getInt(getString(R.string.counter), 0);
            boolean showedTutorialAlready = prefs.getBoolean(getString(R.string.showed_tutorial), false);
            boolean showedDonateAlready = prefs.getBoolean(getString(R.string.showed_donate), false);

            mTextPaint.setTextSize(getResources().getDisplayMetrics().heightPixels * 0.06f + preferenceManager.getSecondaryTextOffset()); //secondary text

            //Work with given preferences
            switch (preferenceManager.getLanguage()) {
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

            Typeface NORMAL_TYPEFACE2;
            switch (preferenceManager.getFont()) {
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

        @Override
        public void onDestroy() {
            super.onDestroy();

            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);

            communicator.disconnect();

            if (wordClockTask != null) {
                if (wordClockTask.getStatus() != AsyncTask.Status.FINISHED) {
                    wordClockTask.cancel(true);
                }
                wordClockTask = null;
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                registerReceiver();
                mCalendar.setTimeZone(TimeZone.getDefault());
                Wearable.getDataClient(getApplicationContext()).addListener(this);

                communicator.performHandshake();
            } else {
                unregisterReceiver();
                Wearable.getDataClient(getApplicationContext()).removeListener(this);
            }
            updateTimer();
            forceRefresh();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            WatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            WatchFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);
            mChinSize = insets.getSystemWindowInsetBottom();
            mTextPaint.setTextSize(getResources().getDisplayMetrics().heightPixels * 0.06f + preferenceManager.getSecondaryTextOffset()); //secondary text
            mTextPaint2.setTextSize(24 + preferenceManager.getMainTextOffset()); //word clock
            complicationManager.setBounds(screenWidthG, screenHeightG, mChinSize);
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
            complicationManager.changeAmbientMode(inAmbientMode);
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
            forceRefresh();
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
                    complicationManager.handleTap(x, y);
                    break;
            }
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            //Set colors
            if (mAmbient) {
                canvas.drawColor(Color.BLACK);
                mTextPaint.setColor(preferenceManager.getSecondaryColorAmbient());
                mTextPaint2.setColor(preferenceManager.getMainColorAmbient());
            } else {
                canvas.drawColor(preferenceManager.getBackgroundColor());
                mTextPaint.setColor(preferenceManager.getSecondaryColor());
                mTextPaint2.setColor(preferenceManager.getMainColor());
            }

            //Get time
            mCalendar = Calendar.getInstance();
            int seconds = mCalendar.get(Calendar.SECOND);
            int minutes = mCalendar.get(Calendar.MINUTE);
            if ((minutes % 5 == 0 || minutes == 1) && (seconds < 2)) {
                significantTimeChange = true;
                getDate();
            }
            int hourDigital = preferenceManager.isMilitaryTime() ? mCalendar.get(Calendar.HOUR_OF_DAY) : mCalendar.get(Calendar.HOUR);
            if (hourDigital == 0 && !preferenceManager.isMilitaryTime())
                hourDigital = 12;
            if (hourDigital - lastSignificantHours != 0 || minutes - lastSignificantMinutes > 5 || lastSignificantMinutes - minutes < -5) {
                significantTimeChange = true;
                getDate();
            }

            //Get digital clock
            String ampmSymbols = (!preferenceManager.isMilitaryTime()) ? (mCalendar.get(Calendar.HOUR_OF_DAY) >= 12 ? " pm" : " am") : "";
            String text = (mAmbient || !preferenceManager.isShowSeconds())
                    ? String.format(Locale.UK, "%d:%02d" + ampmSymbols, hourDigital, minutes)
                    : String.format(Locale.UK, "%d:%02d:%02d" + ampmSymbols, hourDigital, minutes, seconds);

            //Draw digital clock, date, battery percentage and day of the week
            float firstSeparator = 40.0f;
            if ((mAmbient && !preferenceManager.isShowSecondary()) || (!mAmbient && !preferenceManager.isShowSecondaryActive())) {
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
            if (!((mAmbient && preferenceManager.isShowSecondaryCalendar()) || (!mAmbient && preferenceManager.isShowSecondaryCalendarActive()))) {
                text3 = "";
            }
            if (!((mAmbient && preferenceManager.isShowBatteryAmbient()) || (!mAmbient && preferenceManager.isShowBattery()))) {
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
            if (basey == -1)
                significantTimeChange = true;

            //Draw text clock
            if (significantTimeChange) {
                lastSignificantMinutes = minutes;
                lastSignificantHours = hourDigital;
                int index = minutes / 5;
                int hourText = preferenceManager.isMilitaryTextTime() ? mCalendar.get(Calendar.HOUR_OF_DAY) + TimeShift[index] : mCalendar.get(Calendar.HOUR) + TimeShift[index];
                if (hourText >= 24 && preferenceManager.isMilitaryTextTime())
                    hourText -= 24;
                else if (hourText > 12 && !preferenceManager.isMilitaryTextTime())
                    hourText -= 12;
                if (hourText == 0 && !preferenceManager.isMilitaryTextTime())
                    hourText = 12;
                wordClockTask = new WordClockTask(new WeakReference<Context>(getApplicationContext()), preferenceManager.getFont(), preferenceManager.getCapitalisation(), hourText, minutes, index, Prefixes, Suffixes,
                        PrefixNewLine, SuffixNewLine, preferenceManager.getLanguage(), true, false, preferenceManager.isComplicationLeftSet(), preferenceManager.isComplicationRightSet(), bounds.width(),
                        bounds.height(), firstSeparator, mChinSize, preferenceManager.getMainTextOffset(), new WeakReference<WordClockListener>(this));
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
            if ((mAmbient && preferenceManager.isShowComplicationAmbient()) || (!mAmbient && preferenceManager.isShowComplication())) {
                long now = System.currentTimeMillis();
                complicationManager.drawComplications(canvas, now);
            }
        }

        @Override
        public void wordClockListener(WordClockTaskWrapper wordClockTaskWrapper) {
            basey = wordClockTaskWrapper.getBasey();
            text2 = wordClockTaskWrapper.getText();
            prefs.edit().putInt(getString(R.string.main_text_size_real), (int) wordClockTaskWrapper.getTextSize()).apply();
            mTextPaint2.setTextSize(wordClockTaskWrapper.getTextSize() + preferenceManager.getMainTextOffset());
            x = wordClockTaskWrapper.getX();
            invalidate();
        }

        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        private boolean shouldTimerBeRunning() {
            return isVisible() && !mAmbient;
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

        @Override
        public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
            for (DataEvent event : dataEventBuffer) {
                if (event.getType() == DataEvent.TYPE_CHANGED && event.getDataItem().getUri().getPath() != null && event.getDataItem().getUri().getPath().equals(communicator.getPath())) {
                    communicator.processData(event.getDataItem());
                    forceRefresh();
                }
            }
        }

        private void forceRefresh() {
            preferenceManager.loadPreferences();
            loadPreferences();
            getDate();
            significantTimeChange = true;
        }
    }
}
