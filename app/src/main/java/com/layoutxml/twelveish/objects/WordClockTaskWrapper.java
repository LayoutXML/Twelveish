package com.layoutxml.twelveish.objects;

import com.layoutxml.twelveish.WordClockListener;

public class WordClockTaskWrapper {

    private String text;
    private float basey;
    private float textSize;
    private float x;

    public WordClockTaskWrapper(String text, float basey, float textSize, float x) {
        this.text = text;
        this.basey = basey;
        this.textSize = textSize;
        this.x = x;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getBasey() {
        return basey;
    }

    public void setBasey(float basey) {
        this.basey = basey;
    }
}
