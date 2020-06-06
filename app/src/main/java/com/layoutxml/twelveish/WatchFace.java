
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

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.Wearable;
import com.layoutxml.twelveish.activities.list_activities.ActivityImageViewActivity;
import com.layoutxml.twelveish.objects.TextGeneratorDataWrapper;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static android.view.Gravity.CENTER_HORIZONTAL;
import static android.view.Gravity.TOP;

public class WatchFace extends CanvasWatchFaceService {
    private static final Typeface DEFAULT_TYPEFACE = Typeface.create("sans-serif-light", Typeface.NORMAL);
    private static final long INTERACTIVE_UPDATE_RATE_MS = 1000;
    private static final int MESSAGE_UPDATE_TIME = 0;
    private static final String TRANSITION_TO_AMBIENT_MODE = "com.rokasjankunas.ticktock.TRANSITION_TO_AMBIENT_MODE";
    private static final String TRANSITION_TO_INTERACTIVE_MODE = "com.rokasjankunas.ticktock.TRANSITION_TO_INTERACTIVE_MODE";

    private PreferenceManager preferenceManager;
    private LanguageManager languageManager;
    private Communicator communicator;
    private ComplicationManager complicationManager;
    private SharedPreferences preferences;
    private TextGenerator textGenerator;

    private int batteryLevel = 100;
    private int screenWidth;
    private int screenHeight;
    private String secondaryTextFirstLine = "";
    private String secondaryTextSecondLine = "";
    private String secondaryTextSecondLineCopy = "";
    private String mainText = "";
    private String dayOfTheWeek = "";
    private boolean fetchMainText = true;
    private float baseXCoordinate = 0;
    private float baseYCoordinate = 0;
    private int lastMainTextFetchTimeHours = -1;
    private int lastMainTextFetchTimeMinutes = -1;

    @Override
    public Engine onCreateEngine() {
        getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                batteryLevel = (int) (100 * intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) / ((float) (intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1))));
                secondaryTextSecondLine = (batteryLevel + "%");
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
                    case MESSAGE_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine implements DataClient.OnDataChangedListener, TextGeneratorListener {

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
            languageManager = new LanguageManager(getApplicationContext());
            communicator = new Communicator(getApplicationContext(), preferenceManager);
            complicationManager = new ComplicationManager(getApplicationContext(), preferenceManager);

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.background));

            mTextPaint = new Paint();
            mTextPaint.setTypeface(DEFAULT_TYPEFACE);
            mTextPaint.setAntiAlias(true);
            mTextPaint.setTextAlign(Paint.Align.CENTER);

            mTextPaint2 = new Paint();
            mTextPaint2.setTypeface(DEFAULT_TYPEFACE);
            mTextPaint2.setAntiAlias(true);
            mTextPaint2.setTextAlign(Paint.Align.CENTER);

            preferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            forceRefresh();

            complicationManager.initializeComplications();
            setActiveComplications(ComplicationManager.getComplicationIds());

            mTextPaint.setColor(preferenceManager.getSecondaryTextColorActive());
            mTextPaint2.setColor(preferenceManager.getMainTextColorActive());
        }

        private void getDate() {
            //Get date
            int first, second, third;
            boolean FourFirst;
            switch (preferenceManager.getDateOrder()) {
                case DMY:
                    first = mCalendar.get(Calendar.DAY_OF_MONTH);
                    second = mCalendar.get(Calendar.MONTH) + 1;
                    third = mCalendar.get(Calendar.YEAR);
                    FourFirst = false;
                    break;
                case YMD:
                    first = mCalendar.get(Calendar.YEAR);
                    second = mCalendar.get(Calendar.MONTH) + 1;
                    third = mCalendar.get(Calendar.DAY_OF_MONTH);
                    FourFirst = true;
                    break;
                case YDM:
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
                secondaryTextFirstLine = String.format(Locale.UK, "%04d" + preferenceManager.getDateSeparator() + "%02d" + preferenceManager.getDateSeparator() + "%02d", first, second, third);
            else
                secondaryTextFirstLine = String.format(Locale.UK, "%02d" + preferenceManager.getDateSeparator() + "%02d" + preferenceManager.getDateSeparator() + "%04d", first, second, third);


            //Get day of the week
            if ((mAmbient && preferenceManager.isShowDayAmbient()) || (!mAmbient && preferenceManager.isShowDayActive()))
                dayOfTheWeek = languageManager.getWeekday(mCalendar.get(Calendar.DAY_OF_WEEK) - 1);
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
            screenHeight = height;
            screenWidth = width;
        }

        private void loadPreferences() {
            boolean showedRateAlready = preferences.getBoolean(getString(R.string.showed_rate), false);
            int counter = preferences.getInt(getString(R.string.counter), 0);
            boolean showedTutorialAlready = preferences.getBoolean(getString(R.string.showed_tutorial), false);
            boolean showedDonateAlready = preferences.getBoolean(getString(R.string.showed_donate), false);

            mTextPaint.setTextSize(getResources().getDisplayMetrics().heightPixels * 0.06f + preferenceManager.getSecondaryTextSizeOffset()); //secondary text
            mTextPaint2.setTypeface(preferenceManager.getFont());

            invalidate();

            if (counter >= 100 && !showedRateAlready) {
                preferences.edit().putBoolean(getString(R.string.showed_rate), true).apply();
                showRateNotification();
            }

            counter++;
            if (counter < 202) {
                preferences.edit().putInt(getString(R.string.counter), counter).apply();
            }

            if (!showedTutorialAlready && counter > 30) {
                preferences.edit().putBoolean(getString(R.string.showed_tutorial), true).apply();
                showTutorialNotification();
            }

            if (counter >= 200 && !showedDonateAlready) {
                preferences.edit().putBoolean(getString(R.string.showed_donate), true).apply();
                showDonateNotification();
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            mUpdateTimeHandler.removeMessages(MESSAGE_UPDATE_TIME);

            communicator.disconnect();

            if (textGenerator != null) {
                if (textGenerator.getStatus() != AsyncTask.Status.FINISHED) {
                    textGenerator.cancel(true);
                }
                textGenerator = null;
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
            mTextPaint.setTextSize(getResources().getDisplayMetrics().heightPixels * 0.06f + preferenceManager.getSecondaryTextSizeOffset()); //secondary text
            mTextPaint2.setTextSize(24 + preferenceManager.getMainTextSizeOffset()); //word clock
            complicationManager.setBounds(screenWidth, screenHeight, mChinSize);
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
                mTextPaint.setColor(preferenceManager.getSecondaryTextColorAmbient());
                mTextPaint2.setColor(preferenceManager.getMainTextColorAmbient());
            } else {
                canvas.drawColor(preferenceManager.getBackgroundColor());
                mTextPaint.setColor(preferenceManager.getSecondaryTextColorActive());
                mTextPaint2.setColor(preferenceManager.getMainTextColorActive());
            }

            //Get time
            mCalendar = Calendar.getInstance();
            int seconds = mCalendar.get(Calendar.SECOND);
            int minutes = mCalendar.get(Calendar.MINUTE);
            if ((minutes % 5 == 0 || minutes == 1) && (seconds < 2)) {
                fetchMainText = true;
                getDate();
            }
            int hourDigital = preferenceManager.isMilitaryFormatDigital() ? mCalendar.get(Calendar.HOUR_OF_DAY) : mCalendar.get(Calendar.HOUR);
            if (hourDigital == 0 && !preferenceManager.isMilitaryFormatDigital())
                hourDigital = 12;
            if (hourDigital - lastMainTextFetchTimeHours != 0 || minutes - lastMainTextFetchTimeMinutes > 5 || lastMainTextFetchTimeMinutes - minutes < -5) {
                fetchMainText = true;
                getDate();
            }

            //Get digital clock
            String ampmSymbols = (!preferenceManager.isMilitaryFormatDigital()) ? (mCalendar.get(Calendar.HOUR_OF_DAY) >= 12 ? " pm" : " am") : "";
            String text = (mAmbient || !preferenceManager.isShowSeconds())
                    ? String.format(Locale.UK, "%d:%02d" + ampmSymbols, hourDigital, minutes)
                    : String.format(Locale.UK, "%d:%02d:%02d" + ampmSymbols, hourDigital, minutes, seconds);

            //Draw digital clock, date, battery percentage and day of the week
            float firstSeparator = 40.0f;
            if ((mAmbient && !preferenceManager.isShowSecondaryTextAmbient()) || (!mAmbient && !preferenceManager.isShowSecondaryTextActive())) {
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
            if (!((mAmbient && preferenceManager.isShowSecondaryCalendarAmbient()) || (!mAmbient && preferenceManager.isShowSecondaryCalendarActive()))) {
                secondaryTextFirstLine = "";
            }
            if (!((mAmbient && preferenceManager.isShowBatteryAmbient()) || (!mAmbient && preferenceManager.isShowBatteryActive()))) {
                if (!secondaryTextSecondLine.equals("")) {
                    secondaryTextSecondLineCopy = secondaryTextSecondLine;
                }
                secondaryTextSecondLine = "";
            } else if (secondaryTextSecondLine.equals("")) {
                secondaryTextSecondLine = secondaryTextSecondLineCopy;
            }
            if (!secondaryTextFirstLine.equals("") || !secondaryTextSecondLine.equals("")) {
                if (!secondaryTextFirstLine.equals("") && !secondaryTextSecondLine.equals("")) {
                    canvas.drawText(secondaryTextFirstLine + " • " + secondaryTextSecondLine, bounds.width() / 2.0f, firstSeparator - mTextPaint.ascent(), mTextPaint);
                    firstSeparator = firstSeparator - mTextPaint.ascent() + mTextPaint.descent();
                } else if (!secondaryTextFirstLine.equals("")) {
                    canvas.drawText(secondaryTextFirstLine, bounds.width() / 2.0f, firstSeparator - mTextPaint.ascent(), mTextPaint);
                    firstSeparator = firstSeparator - mTextPaint.ascent() + mTextPaint.descent();
                } else {
                    canvas.drawText(secondaryTextSecondLine, bounds.width() / 2.0f, firstSeparator - mTextPaint.ascent(), mTextPaint);
                    firstSeparator = firstSeparator - mTextPaint.ascent() + mTextPaint.descent();
                }
            }
            if (firstSeparator < bounds.height() / 4)
                firstSeparator = (float) bounds.height() / 4;

            if (mainText.equals(""))
                fetchMainText = true;
            if (baseYCoordinate == -1)
                fetchMainText = true;

            //Draw text clock
            if (fetchMainText) {
                lastMainTextFetchTimeMinutes = minutes;
                lastMainTextFetchTimeHours = hourDigital;
                textGenerator = new TextGenerator(preferenceManager, languageManager, this, bounds.width(), bounds.height(), mChinSize, firstSeparator);
                textGenerator.execute();

                fetchMainText = false;
            }

            //Draw text
            float t = baseYCoordinate;
            for (String line : mainText.split("\n")) {
                canvas.drawText(line, baseXCoordinate, t, mTextPaint2);
                t += mTextPaint2.descent() - mTextPaint2.ascent();
            }

            //Draw complication
            if ((mAmbient && preferenceManager.isShowComplicationAmbient()) || (!mAmbient && preferenceManager.isShowComplicationActive())) {
                long now = System.currentTimeMillis();
                complicationManager.drawComplications(canvas, now);
            }
        }

        @Override
        public void textGeneratorListener(TextGeneratorDataWrapper textGeneratorDataWrapper) {
            mainText = textGeneratorDataWrapper.getMainText();
            baseXCoordinate = textGeneratorDataWrapper.getBaseXCoordinate();
            baseYCoordinate = textGeneratorDataWrapper.getBaseYCoordinate();
            preferences.edit().putInt(getString(R.string.main_text_size_real), (int) textGeneratorDataWrapper.getTextSize()).apply();
            mTextPaint2.setTextSize(textGeneratorDataWrapper.getTextSize() + preferenceManager.getMainTextSizeOffset());
            invalidate();
        }

        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MESSAGE_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MESSAGE_UPDATE_TIME);
            }
        }

        private boolean shouldTimerBeRunning() {
            return isVisible() && !mAmbient;
        }

        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_TIME, delayMs);
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
            languageManager.loadPreferences();
            loadPreferences();
            getDate();
            fetchMainText = true;
            invalidate();
        }
    }
}
