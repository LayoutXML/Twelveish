package com.layoutxml.twelveish.objects;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;

public class WatchPreviewView extends View {

    private int height = 0;
    private Paint paint;

    public WatchPreviewView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
    }
/*
    @Override
    protected void onDraw(Canvas canvas) {
//        int x = getWidth()/2;
//        int y = getHeight()/2;
//        paint.setColor(Color.parseColor("#000000"));
//
//        canvas.drawCircle(x,y,y,paint);

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
        String ampmSymbols = (ampm) ? (mCalendar.get(Calendar.HOUR_OF_DAY) >= 12 ? " pm" : " am") : "";
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
            text1 = "";
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
            if ((mAmbient && showWordsAmbient) || (!mAmbient && showWords)) {
                wordClockTask = new WordClockTask(new WeakReference<Context>(getApplicationContext()),font,capitalisation,hourText,minutes,index,Prefixes,Suffixes,
                        PrefixNewLine,SuffixNewLine,language,showSuffixes,legacyWords,complicationLeftSet,complicationRightSet,bounds.width(),
                        bounds.height(),firstSeparator,mChinSize,mainTextOffset,new WeakReference<WordClockListener>(this));
                wordClockTask.execute();
            } else {
                text2 = "";
            }
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
    */
}
