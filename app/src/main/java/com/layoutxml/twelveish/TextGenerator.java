package com.layoutxml.twelveish;

import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;

import com.layoutxml.twelveish.objects.TextGeneratorDataWrapper;

import java.lang.ref.WeakReference;
import java.util.Calendar;

public class TextGenerator extends AsyncTask<Void, Void, TextGeneratorDataWrapper> {
    private final PreferenceManager preferenceManager;
    private final LanguageManager languageManager;
    private final WeakReference<TextGeneratorListener> wordClockListenerWeakReference;

    private final int boundsWidth;
    private final int boundsHeight;
    private final float chinSize;
    private final float firstSeparator;

    private Calendar calendar;
    private int hourIndex;
    private int minuteIndex;
    private boolean roundTime;

    public TextGenerator(PreferenceManager preferenceManager,
                         LanguageManager languageManager,
                         TextGeneratorListener textGeneratorListener,
                         int boundsWidth,
                         int boundsHeight,
                         float chinSize,
                         float firstSeparator) {
        this.preferenceManager = preferenceManager;
        this.languageManager = languageManager;
        this.wordClockListenerWeakReference = new WeakReference<>(textGeneratorListener);

        calendar = Calendar.getInstance();
        minuteIndex = calendar.get(Calendar.MINUTE) / 5;
        roundTime = calendar.get(Calendar.MINUTE) == 0;
        hourIndex = getHourIndex();

        this.boundsWidth = boundsWidth;
        this.boundsHeight = boundsHeight;
        this.chinSize = chinSize;
        this.firstSeparator = firstSeparator;
    }

    @Override
    protected TextGeneratorDataWrapper doInBackground(Void... voids) {
        String text = generateText();
        float textSize = calculateOptimalFontSizeForString(text);
        float baseXCoordinate = calculateBaseXCoordinate();
        float baseYCoordinate = calculateBaseYCoordinate(text, textSize);

        return new TextGeneratorDataWrapper(text, baseXCoordinate, baseYCoordinate, textSize);
    }

    @Override
    protected void onPostExecute(TextGeneratorDataWrapper textGeneratorDataWrapper) {
        super.onPostExecute(textGeneratorDataWrapper);
        final TextGeneratorListener listener = wordClockListenerWeakReference.get();
        if (listener != null) {
            listener.textGeneratorListener(textGeneratorDataWrapper);
        }
    }

    private String generateText() {
        String text;
        switch (preferenceManager.getCapitalisation()) {
            case UPPERCASE:
                text = formatUppercaseText();
                break;
            case LOWERCASE:
                text = formatLowercaseText();
                break;
            case FIRST_TITLE_CASE:
                text = formatFirstTitleCaseText();
                break;
            case LINE_TITLE_CASE:
                text = formatLineTitleCaseText();
                break;
            default:
                text = formatTitleCaseText();
                break;
        }
        return text;
    }

    private float calculateOptimalFontSizeForString(String text) {
        Paint paint = new Paint();
        paint.setTypeface(preferenceManager.getFont());
        paint.setTextAlign(Paint.Align.CENTER);

        float initialSize = 100f;
        paint.setTextSize(initialSize);
        Rect bounds = new Rect();

        String[] splitTextByLines = text.split("\n");

        int desiredHeight = (int) ((boundsHeight - firstSeparator - chinSize - 16) / splitTextByLines.length - (paint.descent() + paint.ascent()) * (splitTextByLines.length - 1));
        int desiredWidth = boundsWidth - 16;
        if (preferenceManager.isComplicationLeftSet()) {
            desiredWidth -= boundsWidth / 4;
        }
        if (preferenceManager.isComplicationRightSet()) {
            desiredWidth -= boundsWidth / 4;
        }

        float minSize = Float.MAX_VALUE;
        for (String line : splitTextByLines) {
            paint.getTextBounds(line, 0, line.length(), bounds);
            float maxSizeByWidth = initialSize * desiredWidth / bounds.width();
            float maxSizeByHeight = initialSize * desiredHeight / bounds.height();
            minSize = Math.min(Math.min(maxSizeByWidth, maxSizeByHeight), minSize);
        }
        return minSize;
    }

    private float calculateBaseXCoordinate() {
        if ((preferenceManager.isComplicationLeftSet() && preferenceManager.isComplicationRightSet()) || (!preferenceManager.isComplicationLeftSet() && !preferenceManager.isComplicationRightSet())) {
            return (float) boundsWidth / 2;
        } else if (preferenceManager.isComplicationLeftSet()) {
            return (float) boundsWidth * 5 / 8;
        } else {
            return (float) boundsWidth * 3 / 8;
        }
    }

    private float calculateBaseYCoordinate(String text, float textSize) {
        Paint paint = new Paint();
        paint.setTypeface(preferenceManager.getFont());
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(textSize + preferenceManager.getMainTextSizeOffset());

        String[] splitTextByLines = text.split("\n");
        return boundsHeight / 2f - paint.ascent() - (paint.descent() - paint.ascent() * splitTextByLines.length) / 2;
    }

    private String formatTitleCaseText() {
        String unprocessedText = formatUnprocessedArrangedText();

        String[] splitTextByLines = unprocessedText.split("\n");
        StringBuilder finalText = new StringBuilder();
        for (String line : splitTextByLines) {
            String[] splitLineBySpaces = line.split(" ");
            boolean firstWordInLine = true;
            for (String word : splitLineBySpaces) {
                if (firstWordInLine) {
                    firstWordInLine = false;
                } else {
                    finalText.append(' ');
                }
                finalText.append(word.substring(0, 1).toUpperCase());
                if (word.length() > 1) {
                    finalText.append(word.substring(1).toLowerCase());
                }
            }
            finalText.append('\n');
        }

        return finalText.toString().substring(0, finalText.length() - 1);
    }

    private String formatUppercaseText() {
        return formatUnprocessedArrangedText().toUpperCase();
    }

    private String formatLowercaseText() {
        return formatUnprocessedArrangedText().toLowerCase();
    }

    private String formatFirstTitleCaseText() {
        String unprocessedText = formatUnprocessedArrangedText();
        return unprocessedText.substring(0, 1).toUpperCase() + unprocessedText.substring(1).toLowerCase();
    }

    private String formatLineTitleCaseText() {
        String unprocessedText = formatUnprocessedArrangedText();

        String[] splitText = unprocessedText.split("\n");
        StringBuilder finalText = new StringBuilder();
        for (String line : splitText) {
            finalText.append(line.substring(0, 1).toUpperCase()).append(line.substring(1).toLowerCase()).append('\n');
        }

        return finalText.toString().substring(0, finalText.length() - 1);
    }

    private String formatUnprocessedArrangedText() {
        String prefix = "";
        if (!roundTime) {
            prefix = languageManager.getPrefix(minuteIndex);
        }

        String suffix = "";
        if (!roundTime) {
            suffix = languageManager.getSuffix(minuteIndex);
        }

        String unprocessedText = prefix + getPrefixSeparator() + getExactTime() + getSuffixSeparator() + suffix;
        return arrangeWords(unprocessedText);
    }

    private String getExactTime() {
        return languageManager.getHour(hourIndex);
    }

    private String arrangeWords(String text) {
        double fontHeightToWidthRatio = 2.0; // TODO: make font dependent
        int maxNumberOfLines = 3; // TODO: refactor as a preference
        double lineWidthPrecision = 0.8;

        int numberOfLines = (int) Math.round(Math.sqrt((double) text.length() / fontHeightToWidthRatio));

        if (numberOfLines == 0) {
            numberOfLines = 1;
        }
        if (numberOfLines > maxNumberOfLines) {
            numberOfLines = maxNumberOfLines;
        }

        if (numberOfLines == 1) {
            return text;
        }

        String[] splitTextToWords = text.split(" ");
        StringBuilder arrangedText = new StringBuilder();
        StringBuilder currentLine = new StringBuilder();
        int totalLines = 0;
        for (String word : splitTextToWords) {
            if (currentLine.length() >= text.length() * lineWidthPrecision / numberOfLines && totalLines + 1 != numberOfLines) {
                arrangedText.append(currentLine).append('\n');
                currentLine = new StringBuilder();
                totalLines++;
            }
            if (currentLine.length() > 0) {
                currentLine.append(' ');
            }
            currentLine.append(word);
        }
        arrangedText.append(currentLine);

        return arrangedText.toString();
    }

    private int getHourIndex() {
        int hourIndex = preferenceManager.isMilitaryFormatText() ? calendar.get(Calendar.HOUR_OF_DAY) : calendar.get(Calendar.HOUR);
        hourIndex += languageManager.getTimeShift(minuteIndex);

        if (hourIndex >= 24) {
            hourIndex -= 24;
        }
        if (!preferenceManager.isMilitaryFormatText() && hourIndex > 12) {
            hourIndex -= 12;
        }

        if (hourIndex < 0) {
            if (preferenceManager.isMilitaryFormatText()) {
                hourIndex = 24 - hourIndex;
            } else {
                hourIndex = 12 - hourIndex;
            }
        }

        if (!preferenceManager.isMilitaryFormatText() && hourIndex == 0) {
            hourIndex = 12;
        }
        return hourIndex;
    }

    private String getPrefixSeparator() {
        if (roundTime || !languageManager.getSeparatePrefix(minuteIndex)) {
            return "";
        }
        return " ";
    }

    private String getSuffixSeparator() {
        if (roundTime || !languageManager.getSeparateSuffix(minuteIndex)) {
            return "";
        }
        return " ";
    }
}