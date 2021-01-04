package com.layoutxml.twelveish;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;

import com.layoutxml.twelveish.objects.WordClockTaskWrapper;

import java.lang.ref.WeakReference;

import androidx.core.content.res.ResourcesCompat;

public class WordClockTask extends AsyncTask<Void,Void, WordClockTaskWrapper> {

    private final WeakReference<Context> contextWeakReference;
    private final WeakReference<WordClockListener> wordClockListenerWeakReference;
    private String font;
    private int capitalisation;
    private int hourText;
    private int minutes;
    private int index;
    private String[] Prefixes;
    private String[] Suffixes;
    private boolean[] PrefixNewLine = new boolean[]{false, false, true, true, true, true, true, true, true, true, true, true};
    private boolean[] SuffixNewLine = new boolean[]{false, true, false, true, false, false, false, true, false, true, false, false};
    private String language;
    private boolean showSuffixes;
    private boolean legacyWords;
    private boolean complicationLeftSet;
    private boolean complicationRightSet;
    private int boundsWidth;
    private int boundsHeight;
    private float mChinSize;
    private float firstSeparator;
    private Paint mTextPaint2;
    private int mainTextOffset;

    public WordClockTask(WeakReference<Context> context, String font, int capitalisation, int hourText, int minutes, int index, String[] prefixes,
                         String[] suffixes, boolean[] prefixNewLine, boolean[] suffixNewLine,
                         String language, boolean showSuffixes, boolean legacyWords, boolean complicationLeftSet, boolean complicationRightSet,
                         int boundsWidth, int boundsHeight, float firstSeparator, float mChinSize, int mainTextOffset, WeakReference<WordClockListener> wordClockListenerWeakReference) {
        contextWeakReference = context;
        this.wordClockListenerWeakReference = wordClockListenerWeakReference;
        this.font = font;
        this.capitalisation = capitalisation;
        this.hourText = hourText;
        this.minutes = minutes;
        this.index = index;
        this.Prefixes = prefixes;
        this.Suffixes = suffixes;
        this.PrefixNewLine = prefixNewLine;
        this.SuffixNewLine = suffixNewLine;
        this.language = language;
        this.showSuffixes = showSuffixes;
        this.legacyWords = legacyWords;
        this.complicationLeftSet = complicationLeftSet;
        this.complicationRightSet = complicationRightSet;
        this.boundsWidth = boundsWidth;
        this.boundsHeight = boundsHeight;
        this.firstSeparator = firstSeparator;
        this.mChinSize = mChinSize;
        this.mainTextOffset = mainTextOffset;
    }

    @Override
    protected WordClockTaskWrapper doInBackground(Void... voids) {
        
        String text;
        switch (capitalisation) {
            case 0:
                text = capitalise0(hourText, minutes, index); //every word title case
                break;
            case 1:
                text = capitalise1(hourText, minutes, index); //all caps
                break;
            case 2:
                text = capitalise2(hourText, minutes, index); //all lowercase
                break;
            case 3:
                text = capitalise3(hourText, minutes, index); //first word title case
                break;
            case 4:
                text = capitalise4(hourText, minutes, index); //first word in every line title case
                break;
            default:
                text = capitalise0(hourText, minutes, index);
                break;
        }

        float textSize;
        float x;
        if (!complicationLeftSet && !complicationRightSet) {
            textSize = getTextSizeForWidth(boundsWidth - 32, (firstSeparator > boundsHeight / 4.0f + mChinSize) ? boundsHeight - firstSeparator - boundsHeight/4.0f - 32 : boundsHeight / 2.0f - mChinSize - 32, text, true);
            x = boundsWidth / 2.0f;
        } else if (complicationLeftSet && !complicationRightSet) {
            textSize = getTextSizeForWidth(boundsWidth * 3.0f / 4.0f - 24, (firstSeparator > boundsHeight / 4.0f + mChinSize) ? boundsHeight - firstSeparator - boundsHeight/4.0f - 32 : boundsHeight / 2.0f - mChinSize - 32, text, false);
            x = boundsWidth * 5.0f / 8.0f - 16;
        } else if (!complicationLeftSet && complicationRightSet) {
            textSize = getTextSizeForWidth(boundsWidth * 3.0f / 4.0f - 24, (firstSeparator > boundsHeight / 4.0f + mChinSize) ? boundsHeight - firstSeparator - boundsHeight/4.0f - 32 : boundsHeight / 2.0f - mChinSize - 32, text, false);
            x = boundsWidth * 3.0f / 8.0f + 16;
        } else {
            textSize = getTextSizeForWidth(boundsWidth / 2.0f, (firstSeparator > boundsHeight / 4.0f + mChinSize) ? boundsHeight - firstSeparator - boundsHeight/4.0f - 32 : boundsHeight / 2.0f - mChinSize - 32, text, false);
            x = boundsWidth / 2.0f;
        }

        float y=0;
        mTextPaint2.setTextSize(textSize+mainTextOffset);
        for (String ignored : text.split("\n")) {
            y += mTextPaint2.descent() - mTextPaint2.ascent();
        }
        y = -mTextPaint2.ascent() - y / 2 + boundsHeight / 2.0f;
        float basey = y;
        for (String line : text.split("\n")) {
            Canvas canvas = new Canvas();
            canvas.drawText(line, x, y, mTextPaint2);
            y += mTextPaint2.descent() - mTextPaint2.ascent();
        }

        return new WordClockTaskWrapper(text,basey, textSize, x);
    }

    @Override
    protected void onPostExecute(WordClockTaskWrapper wordClockTaskWrapper) {
        super.onPostExecute(wordClockTaskWrapper);
        final WordClockListener listener = wordClockListenerWeakReference.get();
        if (listener!=null) {
            listener.wordClockListener(wordClockTaskWrapper);
        }
    }
    

    /**
     * Calculates the best maximum font size value for text
     *
     * @param desiredWidth  canvas width with margins subtracted
     * @param desiredHeight canvas height with margins subtracted
     * @param text          text
     * @param addMargin     whether to add additional margin or not
     * @return text size
     */
    private float getTextSizeForWidth(float desiredWidth, float desiredHeight, String text, boolean addMargin) {

        Context context = contextWeakReference.get();
        if (context == null) {
            return -1;
        }

        Typeface NORMAL_TYPEFACE = ResourcesCompat.getFont(context, R.font.alegreya);
        switch (font) {
            case "robotolight":
                NORMAL_TYPEFACE = Typeface.create("sans-serif-light", Typeface.NORMAL);
                break;
            case "alegreya":
                NORMAL_TYPEFACE = ResourcesCompat.getFont(context, R.font.alegreya);
                break;
            case "cabin":
                NORMAL_TYPEFACE = ResourcesCompat.getFont(context, R.font.cabin);
                break;
            case "ibmplexsans":
                NORMAL_TYPEFACE = ResourcesCompat.getFont(context, R.font.ibmplexsans);
                break;
            case "inconsolata":
                NORMAL_TYPEFACE = ResourcesCompat.getFont(context, R.font.inconsolata);
                break;
            case "merriweather":
                NORMAL_TYPEFACE = ResourcesCompat.getFont(context, R.font.merriweather);
                break;
            case "nunito":
                NORMAL_TYPEFACE = ResourcesCompat.getFont(context, R.font.nunito);
                break;
            case "pacifico":
                NORMAL_TYPEFACE = ResourcesCompat.getFont(context, R.font.pacifico);
                break;
            case "quattrocento":
                NORMAL_TYPEFACE = ResourcesCompat.getFont(context, R.font.quattrocento);
                break;
            case "quicksand":
                NORMAL_TYPEFACE = ResourcesCompat.getFont(context, R.font.quicksand);
                break;
            case "rubik":
                NORMAL_TYPEFACE = ResourcesCompat.getFont(context, R.font.rubik);
                break;
            default:
                NORMAL_TYPEFACE = Typeface.create("sans-serif-light", Typeface.NORMAL);
                break;
        }

        mTextPaint2 = new Paint();
        mTextPaint2.setTypeface(NORMAL_TYPEFACE);
        mTextPaint2.setAntiAlias(true);
        mTextPaint2.setTextAlign(Paint.Align.CENTER);

        text = text.toUpperCase();
        float min = Integer.MAX_VALUE, linecount = 0;
        for (String line : text.split("\n")) {
            if (!line.equals(""))
                linecount++;
            if (addMargin)
                line = "O" + line + "O";
            float testTextSize = 100.00f;
            mTextPaint2.setTextSize(testTextSize);
            Rect bounds = new Rect();
            mTextPaint2.getTextBounds(line, 0, line.length(), bounds);
            float desiredTextSize = testTextSize * desiredWidth / bounds.width();
            float desiredTextSize2 = testTextSize * (desiredHeight) / (bounds.height() + mTextPaint2.descent()) / linecount;
            if (desiredTextSize < min)
                min = desiredTextSize;
            if (desiredTextSize2 < min)
                min = desiredTextSize2;
        }

        return min;
    }

    /**
     * Gets hours text from appropriate language array
     * @param hours hours
     * @return hours text
     */
    private String getExactTime(int hours) {
        
        Context context = contextWeakReference.get();
        if (context == null) {
            return "";
        }
        
        String exactTime;
        switch (language) {
            case "nl":
                exactTime = context.getResources().getStringArray(R.array.ExactTimesNL)[hours];
                break;
            case "en":
                exactTime = context.getResources().getStringArray(R.array.ExactTimes)[hours];
                break;
            case "de":
                exactTime = context.getResources().getStringArray(R.array.ExactTimesDE)[hours];
                break;
            case "el":
                exactTime = context.getResources().getStringArray(R.array.ExactTimesEL)[hours];
                break;
            case "lt":
                exactTime = context.getResources().getStringArray(R.array.ExactTimesLT)[hours];
                break;
            case "fi":
                exactTime = context.getResources().getStringArray(R.array.ExactTimesFI)[hours];
                break;
            case "no":
                exactTime = context.getResources().getStringArray(R.array.ExactTimesNO)[hours];
                break;
            case "ru":
                exactTime = context.getResources().getStringArray(R.array.ExactTimesRU)[hours];
                break;
            case "hu":
                exactTime = context.getResources().getStringArray(R.array.ExactTimesHU)[hours];
                break;
            case "it":
                exactTime = context.getResources().getStringArray(R.array.ExactTimesIT)[hours];
                break;
            case "es":
                exactTime = context.getResources().getStringArray(R.array.ExactTimesES)[hours];
                break;
            case "fr":
                exactTime = context.getResources().getStringArray(R.array.ExactTimesFR)[hours];
                break;
            case "pt":
                exactTime = context.getResources().getStringArray(R.array.ExactTimesPT)[hours];
                break;
            case "sv":
                exactTime = context.getResources().getStringArray(R.array.ExactTimesSV)[hours];
                break;
            default:
                exactTime = context.getResources().getStringArray(R.array.ExactTimes)[hours];
        }
        return exactTime;
    }

    /**
     * Adds line breaks to fit text better
     * @param text text
     * @return new text
     */
    private String arrangeWords(String text) {
        String[] textArray = text.split(" ");
        int numberOfWords = textArray.length;
        if (numberOfWords < 2)
            return text;
        else if ((numberOfWords == 2 && text.length() >= 10) || (numberOfWords == 3 && text.length() > 12)) {
            StringBuilder newText = new StringBuilder();
            for (String word : textArray) {
                newText.append(word).append('\n');
            }
            return newText.substring(0, newText.length() - 1);
        } else {
            if (numberOfWords % 4 == 0 && numberOfWords != 4) {
                int div = numberOfWords / 4;
                StringBuilder newText = new StringBuilder();
                for (int i = 0; i < numberOfWords; i++) {
                    newText.append(textArray[i]);
                    if ((i + 1) % div == 0) {
                        if (i + 1 != numberOfWords)
                            newText.append('\n');
                    } else {
                        if (i + 1 != numberOfWords)
                            newText.append(" ");
                    }
                }
                return newText.toString();
            } else if (numberOfWords % 3 == 0) {
                int div = numberOfWords / 3;
                StringBuilder newText = new StringBuilder();
                for (int i = 0; i < numberOfWords; i++) {
                    newText.append(textArray[i]);
                    if ((i + 1) % div == 0) {
                        if (i + 1 != numberOfWords)
                            newText.append("\n");
                    } else {
                        if (i + 1 != numberOfWords)
                            newText.append(" ");
                    }
                }
                return newText.toString();
            } else if (numberOfWords % 2 == 0 || (numberOfWords == 4 && text.length() > 16)) {
                int div = numberOfWords / 2;
                StringBuilder newText = new StringBuilder();
                for (int i = 0; i < numberOfWords; i++) {
                    newText.append(textArray[i]);
                    if ((i + 1) % div == 0) {
                        if (i + 1 != numberOfWords)
                            newText.append('\n');
                    } else {
                        if (i + 1 != numberOfWords)
                            newText.append(" ");
                    }
                }
                return newText.toString();
            } else {
                int div = numberOfWords / 3;
                if (div == 1)
                    div = numberOfWords / 2;
                StringBuilder newText = new StringBuilder();
                for (int i = 0; i < numberOfWords; i++) {
                    newText.append(textArray[i]);
                    if ((i + 1) % div == 0) {
                        if (i + 1 != numberOfWords)
                            newText.append("\n");
                    } else {
                        if (i + 1 != numberOfWords)
                            newText.append(" ");
                    }
                }
                return newText.toString();
            }
        }
    }

    /**
     * Capitalizes text: every word title case
     * @param hours hours
     * @param minutes minutes
     * @param index array element for prefixes & suffixes
     * @return capitalized text
     */
    private String capitalise0(int hours, int minutes, int index) {
        //Prefix
        String mainPrefix = "";
        StringBuilder prefix;
        if ((minutes > 0) && (!Prefixes[index].equals("")) && (Prefixes[index] != null)) {

            //We first need to replace unicode spaces with regular spaces
            StringBuilder preString = new StringBuilder();
            String[] preArray = Prefixes[index].split("\\u00A0");

            for (int i = 0; i < preArray.length; i++){
                preString.append(preArray[i]);
                preString.append(" ");
            }

            // Then we'll separate out the first letter of each word and capitalize it

            String[] prefixArray = preString.toString().split(" ");
            prefix = new StringBuilder();
            for (String word : prefixArray) {
                if (prefix.length() != 0)
                    prefix.append(" ");
                String capitalised = word.substring(0, 1).toUpperCase() + word.substring(1);
                prefix.append(capitalised);
            }
            mainPrefix = prefix.toString();
        }

        //Time
        StringBuilder hoursInWords = new StringBuilder();
        String mainText;
        String[] mainArray = getExactTime(hours).split(" ");

        for (String word : mainArray) {
            if (hoursInWords.length() != 0) {
                hoursInWords.append(" ");
                hoursInWords.append(word.substring(0, 1).toUpperCase()).append(word.substring(1));
            } else {
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
        if (legacyWords)
            return mainPrefix + ((minutes > 0) ? (PrefixNewLine[index] ? "\n" : "") : "") + mainText + ((minutes > 0) ? (SuffixNewLine[index] ? "\n" : "") : "") + mainSuffix;
        else
            return arrangeWords(mainPrefix + ((minutes > 0) ? (PrefixNewLine[index] ? " " : "") : "") + mainText + ((minutes > 0) ? (SuffixNewLine[index] ? " " : "") : "") + mainSuffix);
    }

    /**
     * Capitalizes text: all caps
     * @param hours hours
     * @param minutes minutes
     * @param index array element for prefixes & suffixes
     * @return capitalized text
     */
    private String capitalise1(int hours, int minutes, int index) {
        String middle = getExactTime(hours);

        if (legacyWords) {
            String text =
                    ((minutes > 0) ? Prefixes[index] : "")
                            + ((minutes > 0) ? (PrefixNewLine[index] ? "\n" : "") : "")
                            + middle
                            + ((minutes > 0) ? (SuffixNewLine[index] ? "\n" : "") : "")
                            + ((showSuffixes) ? ((minutes > 0) ? Suffixes[index] : "") : "");
            return text.toUpperCase();
        } else {
            String text =
                    ((minutes > 0) ? Prefixes[index] : "")
                            + ((minutes > 0) ? (PrefixNewLine[index] ? " " : "") : "")
                            + middle
                            + ((minutes > 0) ? (SuffixNewLine[index] ? " " : "") : "")
                            + ((showSuffixes) ? ((minutes > 0) ? Suffixes[index] : "") : "");
            return arrangeWords(text).toUpperCase();
        }
    }

    /**
     * Capitalizes text: all lowercase
     * @param hours hours
     * @param minutes minutes
     * @param index array element for prefixes & suffixes
     * @return capitalized text
     */
    private String capitalise2(int hours, int minutes, int index) {
        String middle = getExactTime(hours);

        if (legacyWords) {
            String text =
                    ((minutes > 0) ? Prefixes[index] : "")
                            + ((minutes > 0) ? (PrefixNewLine[index] ? "\n" : "") : "")
                            + middle
                            + ((minutes > 0) ? (SuffixNewLine[index] ? "\n" : "") : "")
                            + ((showSuffixes) ? ((minutes > 0) ? Suffixes[index] : "") : "");
            return text.toLowerCase();
        } else {
            String text =
                    ((minutes > 0) ? Prefixes[index] : "")
                            + ((minutes > 0) ? (PrefixNewLine[index] ? " " : "") : "")
                            + middle
                            + ((minutes > 0) ? (SuffixNewLine[index] ? " " : "") : "")
                            + ((showSuffixes) ? ((minutes > 0) ? Suffixes[index] : "") : "");
            return arrangeWords(text).toLowerCase();
        }
    }

    /**
     * Capitalizes text: first word title case
     * @param hours hours
     * @param minutes minutes
     * @param index array element for prefixes & suffixes
     * @return capitalized text
     */
    private String capitalise3(int hours, int minutes, int index) {
        String middle = getExactTime(hours);
        if (legacyWords) {
            String text20 =
                    ((minutes > 0) ? Prefixes[index] : "")
                            + ((minutes > 0) ? (PrefixNewLine[index] ? "\n" : "") : "")
                            + middle
                            + ((minutes > 0) ? (SuffixNewLine[index] ? "\n" : "") : "")
                            + ((showSuffixes) ? ((minutes > 0) ? Suffixes[index] : "") : "");
            return text20.substring(0, 1).toUpperCase() + text20.substring(1).toLowerCase();
        } else {
            String text20 =
                    ((minutes > 0) ? Prefixes[index] : "")
                            + ((minutes > 0) ? (PrefixNewLine[index] ? " " : "") : "")
                            + middle
                            + ((minutes > 0) ? (SuffixNewLine[index] ? " " : "") : "")
                            + ((showSuffixes) ? ((minutes > 0) ? Suffixes[index] : "") : "");
            return arrangeWords(text20.substring(0, 1).toUpperCase() + text20.substring(1).toLowerCase());
        }
    }

    /**
     * Capitalizes text: first word in every line title case
     * @param hours hours
     * @param minutes minutes
     * @param index array element for prefixes & suffixes
     * @return capitalized text
     */
    private String capitalise4(int hours, int minutes, int index) {
        //Prefix
        String mainPrefix = "";
        if ((minutes > 0) && (!Prefixes[index].equals("")) && (Prefixes[index] != null)) {
            mainPrefix = Prefixes[index];
        }

        //Suffix
        String mainSuffix = "";
        if (showSuffixes) {
            if ((minutes > 0) && (!Suffixes[index].equals("")) && (Suffixes[index] != null)) {
                mainSuffix = Suffixes[index];
            }
        }

        if (legacyWords) {
            String text = mainPrefix + ((minutes > 0) ? (PrefixNewLine[index] ? "\n" : "") : "") + getExactTime(hours) + ((minutes > 0) ? (SuffixNewLine[index] ? "\n" : "") : "") + mainSuffix;
            String[] textArray = text.split("\n");
            StringBuilder newText = new StringBuilder();
            for (String word : textArray) {
                newText.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase()).append('\n');
            }
            return newText.toString().substring(0, newText.length() - 1);
        } else {
            String text = mainPrefix + ((minutes > 0) ? (PrefixNewLine[index] ? " " : "") : "") + getExactTime(hours) + ((minutes > 0) ? (SuffixNewLine[index] ? " " : "") : "") + mainSuffix;
            text = arrangeWords(text);
            String[] textArray = text.split("\n");
            StringBuilder newText = new StringBuilder();
            for (String word : textArray) {
                newText.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase()).append('\n');
            }
            return newText.toString().substring(0, newText.length() - 1);
        }

    }
}