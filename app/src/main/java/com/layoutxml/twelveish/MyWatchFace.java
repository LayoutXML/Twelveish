/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 * This product is protected by copyright and distributed under licenses restricting copying, distribution and decompilation.
 */

package com.layoutxml.twelveish;

import android.content.BroadcastReceiver;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static android.view.Gravity.CENTER_HORIZONTAL;
import static android.view.Gravity.TOP;


public class MyWatchFace extends CanvasWatchFaceService {
    private static final Typeface NORMAL_TYPEFACE = Typeface.create("sans-serif-light", Typeface.NORMAL);

    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);
    private static final int MSG_UPDATE_TIME = 0;
    private static final String TAG = "MyWatchFace";
    private String[] Prefixes;
    private String[] Suffixes;
    private Integer[] TimeShift = new Integer[]{0,0,0,0,0,0,0,0,1,1,1,1};
    private Boolean[] PrefixNewLine = new Boolean[]{false,false,true,true,true,true,true,true,true,true,true,true};
    private Boolean[] SuffixNewLine = new Boolean[]{false,true,false,true,false,false,false,true,false,true,false,false};
    private Boolean isRound = true;
    private Boolean contrastingBlack=false;
    private SharedPreferences prefs;
    //SharedPreferences:
    private Integer backgroundColor;
    private Boolean militaryTime;
    private Integer dateOrder;
    private String dateSeparator;
    private Integer capitalisation;
    private Boolean ampm;
    private Boolean showSecondary;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
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
        private float mXOffset;
        private float mYOffset;
        private float mChinSize;
        private Paint mBackgroundPaint;
        private Paint mTextPaint;
        private Paint mTextPaint2;
        private boolean mLowBitAmbient;
        private boolean mBurnInProtection;
        private boolean mAmbient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            prefs = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            loadPreferences();

            setWatchFaceStyle(new WatchFaceStyle.Builder(MyWatchFace.this)
                    .setStatusBarGravity(CENTER_HORIZONTAL | TOP)
                    .setShowUnreadCountIndicator(true)
                    .setAcceptsTapEvents(true)
                    .build());

            mCalendar = Calendar.getInstance();

            Resources resources = MyWatchFace.this.getResources();
            mYOffset = resources.getDimension(R.dimen.digital_y_offset);

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.background));


            mTextPaint = new Paint();
            mTextPaint.setTypeface(NORMAL_TYPEFACE);
            mTextPaint.setAntiAlias(true);
            mTextPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.digital_text));
            mTextPaint.setTextAlign(Paint.Align.CENTER);

            mTextPaint2 = new Paint();
            mTextPaint2.setTypeface(NORMAL_TYPEFACE);
            mTextPaint2.setAntiAlias(true);
            mTextPaint2.setColor(ContextCompat.getColor(getApplicationContext(), R.color.digital_text));
            mTextPaint2.setTextAlign(Paint.Align.CENTER);

            Prefixes = getResources().getStringArray(R.array.Prefixes);
            Suffixes = getResources().getStringArray(R.array.Suffixes);
        }

        private void loadPreferences(){
            backgroundColor = prefs.getInt(getString(R.string.preference_background_color),android.graphics.Color.parseColor("#000000"));
            contrastingBlack = Color.red(backgroundColor) * 0.299 + Color.green(backgroundColor) * 0.587 + Color.blue(backgroundColor) * 0.114 > 186;
            militaryTime = prefs.getBoolean(getString(R.string.preference_military_time),false);
            dateOrder = prefs.getInt(getString(R.string.preference_date_order),0);
            dateSeparator = prefs.getString(getString(R.string.preference_date_separator),"/");
            capitalisation = prefs.getInt(getString(R.string.preference_capitalisation),0);
            ampm = prefs.getBoolean(getString(R.string.preference_ampm),true);
            showSecondary = prefs.getBoolean(getString(R.string.preference_show_secondary),true);
            Log.d(TAG,"loadPreferences: backgroundColor: "+backgroundColor);
            Log.d(TAG,"loadPreferences: militaryTime: "+militaryTime);
            Log.d(TAG,"loadPreferences: dateOrder: "+dateOrder);
            Log.d(TAG,"loadPreferences: capitalisation: "+capitalisation);
            Log.d(TAG,"loadPreferences: am/pm: "+ampm);
            Log.d(TAG,"loadPreferences: showSecondary: "+showSecondary);
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
            mXOffset = resources.getDimension(isRound ? R.dimen.digital_x_offset_round : R.dimen.digital_x_offset);
            mChinSize = insets.getSystemWindowInsetBottom();
            float textSize = resources.getDimension(isRound ? R.dimen.digital_text_size_round : R.dimen.digital_text_size);
            float textSizeSmall = resources.getDimension(isRound ? R.dimen.digital_text_size_round : R.dimen.digital_text_size)/2.5f;

            mTextPaint.setTextSize(textSizeSmall);
            mTextPaint2.setTextSize(textSize);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            mBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);
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
                    break;
            }
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            if (isInAmbientMode()) {
                canvas.drawColor(Color.BLACK);
                mTextPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.digital_text));
                mTextPaint2.setColor(ContextCompat.getColor(getApplicationContext(), R.color.digital_text));
            } else {
                mBackgroundPaint = new Paint();
                mBackgroundPaint.setColor(backgroundColor);
                canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
                if (contrastingBlack) {
                    mTextPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    mTextPaint2.setColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                } else
                {
                    mTextPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.digital_text));
                    mTextPaint2.setColor(ContextCompat.getColor(getApplicationContext(), R.color.digital_text));
                }
            }

            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);
            Integer hour = militaryTime ? mCalendar.get(Calendar.HOUR_OF_DAY) : mCalendar.get(Calendar.HOUR);

            //Draw digital clock
            String ampmSymbols = (ampm&&!militaryTime) ? (mCalendar.get(Calendar.HOUR_OF_DAY)>=12 ? " pm" : " am") : "";
            String text = mAmbient
                    ? String.format(Locale.UK, "%d:%02d"+ampmSymbols, hour, mCalendar.get(Calendar.MINUTE))
                    : String.format(Locale.UK,"%d:%02d:%02d"+ampmSymbols, hour, mCalendar.get(Calendar.MINUTE), mCalendar.get(Calendar.SECOND));
            if (!isInAmbientMode() || showSecondary)
                canvas.drawText(text, bounds.width()/2, 40-mTextPaint.ascent(), mTextPaint);

            //Draw text clock
            /*
            -----------------------------------------
            | Min.  | Prefix               | Suffix |
            -----------------------------------------
            | 0     | -                    | -      |
            | 1-4   | -                    | ish    |
            | 5-9   | -                    | or so  |
            | 10-14 | almost a quarter past| -      |
            | 15-19 | quarter past         | or so  |
            | 20-24 | almost half past     | -      |
            | 25-29 | around half past     | -      |
            | 30-34 | half past            | ish    |
            | 35-39 | half past            | or so  |
            | 40-44 | almost a quarter to  | -      | Hours+1
            | 45-49 | quarter to           | or so  | Hours+1
            | 50-54 | almost               | -      | Hours+1
            | 55-59 | around               | -      | Hours+1
            ----------------------------------------

            0 - every word title case
            1 - all caps
            2 - all lowercase
            3 - first word title case
            4 - first word in every line title case
             */
            int index = mCalendar.get(Calendar.MINUTE)/5;
            String text2;
            switch(capitalisation) {
                case 0:
                    text2 = capitalise0(index);
                    break;
                case 1:
                    text2 = capitalise1(index);
                    break;
                case 2:
                    text2 = capitalise2(index);
                    break;
                case 3:
                    text2 = capitalise3(index);
                    break;
                case 4:
                    text2 = capitalise4(index);
                    break;
                default:
                    text2 = capitalise0(index);
                    break;
            }

            mTextPaint2.setTextSize(getTextSizeForWidth(bounds.width()-32, text2));
            float x = bounds.width()/2, y = ((bounds.height()/2) - ((mTextPaint2.descent() + mTextPaint2.ascent())/2));
            for (String line: text2.split("\n")) {
                y += mTextPaint2.descent() - mTextPaint2.ascent();
            }
            y = 1.5f*((bounds.height()/2) - ((mTextPaint2.descent() + mTextPaint2.ascent())/2)) - 0.5f*y - mTextPaint2.ascent() - mTextPaint2.descent();
            for (String line: text2.split("\n")) {
                canvas.drawText(line, x, y, mTextPaint2);
                y += mTextPaint2.descent() - mTextPaint2.ascent();
            }

            //Draw date
            /*
            0 - MDY
            1 - DMY
            2 - YMD
            3 - YDM
             */
            Integer first, second, third;
            Boolean FourFirst;
            switch (dateOrder) {
                case 0:
                    first = mCalendar.get(Calendar.MONTH)+1;
                    second = mCalendar.get(Calendar.DAY_OF_MONTH);
                    third = mCalendar.get(Calendar.YEAR);
                    FourFirst = false;
                    break;
                case 1:
                    first = mCalendar.get(Calendar.DAY_OF_MONTH);
                    second = mCalendar.get(Calendar.MONTH)+1;
                    third = mCalendar.get(Calendar.YEAR);
                    FourFirst = false;
                    break;
                case 2:
                    first = mCalendar.get(Calendar.YEAR);
                    second = mCalendar.get(Calendar.MONTH)+1;
                    third = mCalendar.get(Calendar.DAY_OF_MONTH);
                    FourFirst = true;
                    break;
                case 3:
                    first = mCalendar.get(Calendar.YEAR);
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
            if (FourFirst) {
                String text3 = String.format(Locale.UK, "%04d"+dateSeparator+"%02d"+dateSeparator+"%02d", first, second, third);
                if (!isInAmbientMode() || showSecondary)
                    canvas.drawText(text3,bounds.width()/2, bounds.height()-16-mTextPaint.descent()-((mChinSize>0) ? mChinSize-16 : 0), mTextPaint);
            } else {
                String text3 = String.format(Locale.UK, "%02d"+dateSeparator+"%02d"+dateSeparator+"%04d", first, second, third);
                if (!isInAmbientMode() || showSecondary)
                    canvas.drawText(text3,bounds.width()/2, bounds.height()-16-mTextPaint.descent()-((mChinSize>0) ? mChinSize-16 : 0), mTextPaint);
            }
        }

        private String capitalise0(Integer index){
            //Prefix
            String mainPrefix = "";
            StringBuilder prefix;
            if ((mCalendar.get(Calendar.MINUTE)>0) && (!Prefixes[index].equals("")) && (Prefixes[index]!=null)) {
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
            Integer hourIndex = mCalendar.get(Calendar.HOUR);
            hourIndex+=TimeShift[index];
            if (hourIndex>=12)
                hourIndex-=12;
            String hoursInWords = getResources().getStringArray(R.array.ExactTimes)[hourIndex];
            String mainText;
            if (mainPrefix.equals("") || PrefixNewLine[index])
                mainText = hoursInWords.substring(0,1).toUpperCase() + hoursInWords.substring(1);
            else
                mainText = hoursInWords.toLowerCase();

            //Suffix
            String mainSuffix = "";
            StringBuilder suffix;
            if ((mCalendar.get(Calendar.MINUTE)>0) && (!Suffixes[index].equals("")) && (Suffixes[index]!=null)) {
                if (SuffixNewLine[index]) {
                    String[] suffixArray = Suffixes[index].split(" ");
                    suffix = new StringBuilder();
                    for (String word : suffixArray) {
                        if (suffix.length()!=0)
                            suffix.append(" ");
                        String capitalised = word.substring(0,1).toUpperCase() + word.substring(1);
                        suffix.append(capitalised);
                    }
                    mainSuffix = suffix.toString();
                } else {
                    mainSuffix = Suffixes[index].toLowerCase();
                }
            }

            return mainPrefix + ((mCalendar.get(Calendar.MINUTE)>0) ? (PrefixNewLine[index] ? "\n" : "") : "") + mainText + ((mCalendar.get(Calendar.MINUTE)>0) ? (SuffixNewLine[index] ? "\n" : "") : "") + mainSuffix;

        }

        private String capitalise1(Integer index) {
            String text20 =
                    ((mCalendar.get(Calendar.MINUTE)>0) ? Prefixes[index] : "")
                            + ((mCalendar.get(Calendar.MINUTE)>0) ? (PrefixNewLine[index] ? "\n" : "") : "")
                            + getResources().getStringArray(R.array.ExactTimes)[(mCalendar.get(Calendar.HOUR) + TimeShift[index])<12 ? (mCalendar.get(Calendar.HOUR) + TimeShift[index]) : (mCalendar.get(Calendar.HOUR) + TimeShift[index])-12]
                            + ((mCalendar.get(Calendar.MINUTE)>0) ? (SuffixNewLine[index] ? "\n" : "") : "")
                            + ((mCalendar.get(Calendar.MINUTE)>0) ? Suffixes[index] : "");

            return text20.toUpperCase();
        }

        private String capitalise2(Integer index) {
            String text20 =
                    ((mCalendar.get(Calendar.MINUTE)>0) ? Prefixes[index] : "")
                            + ((mCalendar.get(Calendar.MINUTE)>0) ? (PrefixNewLine[index] ? "\n" : "") : "")
                            + getResources().getStringArray(R.array.ExactTimes)[(mCalendar.get(Calendar.HOUR) + TimeShift[index])<12 ? (mCalendar.get(Calendar.HOUR) + TimeShift[index]) : (mCalendar.get(Calendar.HOUR) + TimeShift[index])-12]
                            + ((mCalendar.get(Calendar.MINUTE)>0) ? (SuffixNewLine[index] ? "\n" : "") : "")
                            + ((mCalendar.get(Calendar.MINUTE)>0) ? Suffixes[index] : "");

            return text20.toLowerCase();
        }

        private String capitalise3(Integer index) {
            String text20 =
                    ((mCalendar.get(Calendar.MINUTE)>0) ? Prefixes[index] : "")
                            + ((mCalendar.get(Calendar.MINUTE)>0) ? (PrefixNewLine[index] ? "\n" : "") : "")
                            + getResources().getStringArray(R.array.ExactTimes)[(mCalendar.get(Calendar.HOUR) + TimeShift[index])<12 ? (mCalendar.get(Calendar.HOUR) + TimeShift[index]) : (mCalendar.get(Calendar.HOUR) + TimeShift[index])-12]
                            + ((mCalendar.get(Calendar.MINUTE)>0) ? (SuffixNewLine[index] ? "\n" : "") : "")
                            + ((mCalendar.get(Calendar.MINUTE)>0) ? Suffixes[index] : "");

            return text20.substring(0,1).toUpperCase() + text20.substring(1).toLowerCase();
        }

        private String capitalise4(Integer index) {
            //Prefix
            String mainPrefix = "";
            if ((mCalendar.get(Calendar.MINUTE)>0) && (!Prefixes[index].equals("")) && (Prefixes[index]!=null)) {
                mainPrefix = Prefixes[index].substring(0,1).toUpperCase() + Prefixes[index].substring(1).toLowerCase();
            }

            //Time
            Integer hourIndex = mCalendar.get(Calendar.HOUR);
            hourIndex+=TimeShift[index];
            if (hourIndex>=12)
                hourIndex-=12;
            String hoursInWords = getResources().getStringArray(R.array.ExactTimes)[hourIndex];
            String mainText;
            if (mainPrefix.equals("") || PrefixNewLine[index])
                mainText = hoursInWords.substring(0,1).toUpperCase() + hoursInWords.substring(1);
            else
                mainText = hoursInWords.toLowerCase();

            //Suffix
            String mainSuffix = "";
            StringBuilder suffix;
            if ((mCalendar.get(Calendar.MINUTE)>0) && (!Suffixes[index].equals("")) && (Suffixes[index]!=null)) {
                if (SuffixNewLine[index]) {
                    mainSuffix = Suffixes[index].substring(0,1).toUpperCase() + Suffixes[index].substring(1).toLowerCase();
                } else {
                    mainSuffix = Suffixes[index].toLowerCase();
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

        private float getTextSizeForWidth(float desiredWidth, String text) {
            float min = Integer.MAX_VALUE, linecount=0;
            float size, size2;
            for (String line: text.split("\n")) {
                linecount++;
                float testTextSize = 48f;
                mTextPaint2.setTextSize(testTextSize);
                Rect bounds = new Rect();
                mTextPaint2.getTextBounds(line, 0, line.length(), bounds);
                size = (testTextSize * desiredWidth / bounds.width());
                size2 = testTextSize*desiredWidth/bounds.height()/3.5f/linecount;
                if (size2<size)
                    size=size2;
                if (linecount==1) {
                    size2 = testTextSize * desiredWidth / bounds.height() / 7f;
                    if (size2 < size)
                        size = size2;
                }
                if (size<min)
                    min=size;
            }
            if (min!=Integer.MAX_VALUE) {
                return min;
            }
            else {
                Resources resources = MyWatchFace.this.getResources();
                return resources.getDimension(isRound ? R.dimen.digital_text_size_round : R.dimen.digital_text_size);
            }
        }
    }
}
