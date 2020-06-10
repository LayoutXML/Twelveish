
package com.layoutxml.twelveish;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
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

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.Wearable;
import com.layoutxml.twelveish.objects.TextGeneratorDataWrapper;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static android.view.Gravity.CENTER_HORIZONTAL;
import static android.view.Gravity.TOP;

public class WatchFace extends CanvasWatchFaceService {
    private static final long INTERACTIVE_UPDATE_RATE_MS = 1000;
    private static final int MESSAGE_UPDATE_TIME = 0;
    private static final String TRANSITION_TO_AMBIENT_MODE = "com.rokasjankunas.ticktock.TRANSITION_TO_AMBIENT_MODE";
    private static final String TRANSITION_TO_INTERACTIVE_MODE = "com.rokasjankunas.ticktock.TRANSITION_TO_INTERACTIVE_MODE";
    private static final Typeface DEFAULT_TYPEFACE = Typeface.create("sans-serif-light", Typeface.NORMAL);
    private static final int DEFAULT_TEXT_COLOR = 0xFFFFFFFF;

    private PreferenceManager preferenceManager;
    private LanguageManager languageManager;
    private Communicator communicator;
    private ComplicationManager complicationManager;
    private TextGenerator textGenerator;
    private PromotionalNotifications promotionalNotifications;

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
        public void handleMessage(@NonNull Message message) {
            WatchFace.Engine engine = mWeakReference.get();
            if (engine != null && message.what == MESSAGE_UPDATE_TIME) {
                engine.handleUpdateTimeMessage();
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine implements DataClient.OnDataChangedListener, TextGeneratorListener {
        private final Handler updateTimeHandler = new EngineHandler(this);
        private Calendar calendar;
        private boolean registeredTimeZoneReceiver = false;
        private final BroadcastReceiver timeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                calendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };

        private float chinSize;
        private Paint secondaryTextPaint;
        private Paint mainTextPaint;
        private boolean ambientMode = false;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(WatchFace.this)
                    .setStatusBarGravity(CENTER_HORIZONTAL | TOP)
                    .setShowUnreadCountIndicator(true)
                    .setAcceptsTapEvents(true)
                    .build());

            calendar = Calendar.getInstance();

            secondaryTextPaint = new Paint();
            secondaryTextPaint.setTypeface(DEFAULT_TYPEFACE);
            secondaryTextPaint.setTextAlign(Paint.Align.CENTER);
            secondaryTextPaint.setColor(DEFAULT_TEXT_COLOR);

            mainTextPaint = new Paint();
            mainTextPaint.setTypeface(DEFAULT_TYPEFACE);
            mainTextPaint.setTextAlign(Paint.Align.CENTER);
            mainTextPaint.setColor(DEFAULT_TEXT_COLOR);

            preferenceManager = new PreferenceManager(getApplicationContext());
            languageManager = new LanguageManager(getApplicationContext());
            communicator = new Communicator(getApplicationContext(), preferenceManager);
            complicationManager = new ComplicationManager(getApplicationContext(), preferenceManager);
            promotionalNotifications = new PromotionalNotifications(getApplicationContext());
            forceRefresh();

            complicationManager.initializeComplications();
            setActiveComplications(ComplicationManager.getComplicationIds());
        }

        private void getDate() {
            int first, second, third;
            boolean FourFirst;
            switch (preferenceManager.getDateOrder()) {
                case DMY:
                    first = calendar.get(Calendar.DAY_OF_MONTH);
                    second = calendar.get(Calendar.MONTH) + 1;
                    third = calendar.get(Calendar.YEAR);
                    FourFirst = false;
                    break;
                case YMD:
                    first = calendar.get(Calendar.YEAR);
                    second = calendar.get(Calendar.MONTH) + 1;
                    third = calendar.get(Calendar.DAY_OF_MONTH);
                    FourFirst = true;
                    break;
                case YDM:
                    first = calendar.get(Calendar.YEAR);
                    second = calendar.get(Calendar.DAY_OF_MONTH);
                    third = calendar.get(Calendar.MONTH) + 1;
                    FourFirst = true;
                    break;
                default:
                    first = calendar.get(Calendar.MONTH) + 1;
                    second = calendar.get(Calendar.DAY_OF_MONTH);
                    third = calendar.get(Calendar.YEAR);
                    FourFirst = false;
                    break;
            }
            if (FourFirst)
                secondaryTextFirstLine = String.format(Locale.UK, "%04d" + preferenceManager.getDateSeparator() + "%02d" + preferenceManager.getDateSeparator() + "%02d", first, second, third);
            else
                secondaryTextFirstLine = String.format(Locale.UK, "%02d" + preferenceManager.getDateSeparator() + "%02d" + preferenceManager.getDateSeparator() + "%04d", first, second, third);


            //Get day of the week
            if ((ambientMode && preferenceManager.isShowDayAmbient()) || (!ambientMode && preferenceManager.isShowDayActive()))
                dayOfTheWeek = languageManager.getWeekday(calendar.get(Calendar.DAY_OF_WEEK) - 1);
            else
                dayOfTheWeek = "";
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

        private void adjustToPreferenceChanges() {
            mainTextPaint.setTypeface(preferenceManager.getFont());
            mainTextPaint.setTextAlign(Paint.Align.CENTER);
            mainTextPaint.setColor(preferenceManager.getMainTextColorActive());

            secondaryTextPaint.setTypeface(preferenceManager.getFont());
            secondaryTextPaint.setTextAlign(Paint.Align.CENTER);
            secondaryTextPaint.setColor(preferenceManager.getSecondaryTextColorActive());
            secondaryTextPaint.setTextSize(getResources().getDisplayMetrics().heightPixels * 0.06f + preferenceManager.getSecondaryTextSizeOffset());
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            updateTimeHandler.removeMessages(MESSAGE_UPDATE_TIME);

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
                forceRefresh();
                registerReceiver();
                calendar.setTimeZone(TimeZone.getDefault());
                Wearable.getDataClient(getApplicationContext()).addListener(this);

                communicator.performHandshake();
            } else {
                unregisterReceiver();
                Wearable.getDataClient(getApplicationContext()).removeListener(this);
            }
            updateTimer();
        }

        private void registerReceiver() {
            if (registeredTimeZoneReceiver) {
                return;
            }
            registeredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            WatchFace.this.registerReceiver(timeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!registeredTimeZoneReceiver) {
                return;
            }
            registeredTimeZoneReceiver = false;
            WatchFace.this.unregisterReceiver(timeZoneReceiver);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);
            chinSize = insets.getSystemWindowInsetBottom();
            secondaryTextPaint.setTextSize(getResources().getDisplayMetrics().heightPixels * 0.06f + preferenceManager.getSecondaryTextSizeOffset()); //secondary text
            mainTextPaint.setTextSize(24 + preferenceManager.getMainTextSizeOffset()); //word clock
            complicationManager.setBounds(screenWidth, screenHeight, chinSize);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            boolean lowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            secondaryTextPaint.setAntiAlias(!ambientMode && !lowBitAmbient);
            mainTextPaint.setAntiAlias(!ambientMode && !lowBitAmbient);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            ambientMode = inAmbientMode;
            complicationManager.changeAmbientMode(inAmbientMode);
            if (ambientMode) {
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
            invalidate();
        }

        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            if (tapType == TAP_TYPE_TAP) {
                complicationManager.handleTap(x, y);
            }
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            setColors(canvas);

            //Get time
            calendar = Calendar.getInstance();
            int seconds = calendar.get(Calendar.SECOND);
            int minutes = calendar.get(Calendar.MINUTE);
            if ((minutes % 5 == 0 || minutes == 1) && (seconds < 2)) {
                fetchMainText = true;
                getDate();
            }
            int hourDigital = preferenceManager.isMilitaryFormatDigital() ? calendar.get(Calendar.HOUR_OF_DAY) : calendar.get(Calendar.HOUR);
            if (hourDigital == 0 && !preferenceManager.isMilitaryFormatDigital())
                hourDigital = 12;
            if (hourDigital - lastMainTextFetchTimeHours != 0 || minutes - lastMainTextFetchTimeMinutes > 5 || lastMainTextFetchTimeMinutes - minutes < -5) {
                fetchMainText = true;
                getDate();
            }

            //Get digital clock
            String ampmSymbols = (!preferenceManager.isMilitaryFormatDigital()) ? (calendar.get(Calendar.HOUR_OF_DAY) >= 12 ? " pm" : " am") : "";
            String text = (ambientMode || !preferenceManager.isShowSeconds())
                    ? String.format(Locale.UK, "%d:%02d" + ampmSymbols, hourDigital, minutes)
                    : String.format(Locale.UK, "%d:%02d:%02d" + ampmSymbols, hourDigital, minutes, seconds);

            //Draw digital clock, date, battery percentage and day of the week
            float firstSeparator = 40.0f;
            if ((ambientMode && !preferenceManager.isShowSecondaryTextAmbient()) || (!ambientMode && !preferenceManager.isShowSecondaryTextActive())) {
                text = "";
            }
            if (!text.equals("") || !dayOfTheWeek.equals("")) {
                if (!text.equals("") && !dayOfTheWeek.equals("")) {
                    canvas.drawText(text + " • " + dayOfTheWeek, bounds.width() / 2.0f, firstSeparator - secondaryTextPaint.ascent(), secondaryTextPaint);
                    firstSeparator = 40 - secondaryTextPaint.ascent() + secondaryTextPaint.descent();
                } else if (!text.equals("")) {
                    canvas.drawText(text, bounds.width() / 2.0f, firstSeparator - secondaryTextPaint.ascent(), secondaryTextPaint);
                    firstSeparator = 40 - secondaryTextPaint.ascent() + secondaryTextPaint.descent();
                } else {
                    canvas.drawText(dayOfTheWeek, bounds.width() / 2.0f, firstSeparator - secondaryTextPaint.ascent(), secondaryTextPaint);
                    firstSeparator = 40 - secondaryTextPaint.ascent() + secondaryTextPaint.descent();
                }
            }
            if (!((ambientMode && preferenceManager.isShowSecondaryCalendarAmbient()) || (!ambientMode && preferenceManager.isShowSecondaryCalendarActive()))) {
                secondaryTextFirstLine = "";
            }
            if (!((ambientMode && preferenceManager.isShowBatteryAmbient()) || (!ambientMode && preferenceManager.isShowBatteryActive()))) {
                if (!secondaryTextSecondLine.equals("")) {
                    secondaryTextSecondLineCopy = secondaryTextSecondLine;
                }
                secondaryTextSecondLine = "";
            } else if (secondaryTextSecondLine.equals("")) {
                secondaryTextSecondLine = secondaryTextSecondLineCopy;
            }
            if (!secondaryTextFirstLine.equals("") || !secondaryTextSecondLine.equals("")) {
                if (!secondaryTextFirstLine.equals("") && !secondaryTextSecondLine.equals("")) {
                    canvas.drawText(secondaryTextFirstLine + " • " + secondaryTextSecondLine, bounds.width() / 2.0f, firstSeparator - secondaryTextPaint.ascent(), secondaryTextPaint);
                    firstSeparator = firstSeparator - secondaryTextPaint.ascent() + secondaryTextPaint.descent();
                } else if (!secondaryTextFirstLine.equals("")) {
                    canvas.drawText(secondaryTextFirstLine, bounds.width() / 2.0f, firstSeparator - secondaryTextPaint.ascent(), secondaryTextPaint);
                    firstSeparator = firstSeparator - secondaryTextPaint.ascent() + secondaryTextPaint.descent();
                } else {
                    canvas.drawText(secondaryTextSecondLine, bounds.width() / 2.0f, firstSeparator - secondaryTextPaint.ascent(), secondaryTextPaint);
                    firstSeparator = firstSeparator - secondaryTextPaint.ascent() + secondaryTextPaint.descent();
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
                textGenerator = new TextGenerator(preferenceManager, languageManager, this, bounds.width(), bounds.height(), chinSize, firstSeparator);
                textGenerator.execute();

                fetchMainText = false;
            }

            //Draw text
            float t = baseYCoordinate;
            for (String line : mainText.split("\n")) {
                canvas.drawText(line, baseXCoordinate, t, mainTextPaint);
                t += mainTextPaint.descent() - mainTextPaint.ascent();
            }

            //Draw complication
            if ((ambientMode && preferenceManager.isShowComplicationAmbient()) || (!ambientMode && preferenceManager.isShowComplicationActive())) {
                long now = System.currentTimeMillis();
                complicationManager.drawComplications(canvas, now);
            }
        }

        private void setColors(Canvas canvas) {
            if (ambientMode) {
                canvas.drawColor(Color.BLACK);
                secondaryTextPaint.setColor(preferenceManager.getSecondaryTextColorAmbient());
                mainTextPaint.setColor(preferenceManager.getMainTextColorAmbient());
            } else {
                canvas.drawColor(preferenceManager.getBackgroundColor());
                secondaryTextPaint.setColor(preferenceManager.getSecondaryTextColorActive());
                mainTextPaint.setColor(preferenceManager.getMainTextColorActive());
            }
        }

        @Override
        public void textGeneratorListener(TextGeneratorDataWrapper textGeneratorDataWrapper) {
            mainText = textGeneratorDataWrapper.getMainText();
            baseXCoordinate = textGeneratorDataWrapper.getBaseXCoordinate();
            baseYCoordinate = textGeneratorDataWrapper.getBaseYCoordinate();
            mainTextPaint.setTextSize(textGeneratorDataWrapper.getTextSize() + preferenceManager.getMainTextSizeOffset());
            invalidate();
        }

        private void updateTimer() {
            updateTimeHandler.removeMessages(MESSAGE_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                updateTimeHandler.sendEmptyMessage(MESSAGE_UPDATE_TIME);
            }
        }

        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                updateTimeHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_TIME, delayMs);
            }
        }

        private boolean shouldTimerBeRunning() {
            return isVisible() && !ambientMode;
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
            adjustToPreferenceChanges();
            getDate();
            fetchMainText = true;
            invalidate();
            promotionalNotifications.doPromotion();
        }
    }
}
