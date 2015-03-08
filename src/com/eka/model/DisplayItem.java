package com.eka.model;

import java.awt.*;

public class DisplayItem {
    private String word;
    private int id;
    private int x;
    private int y;
    private int w;
    private int h;
    private Color textColor;
    private Color lineColor;
    private Color bgColor;
    private boolean selectable;

    public DisplayItem(String word, int id, int x, int y, int w, int h, Color textColor, Color lineColor, Color bgColor, boolean selectable) {
        this.word = word;
        this.id = id;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.textColor = textColor;
        this.lineColor = lineColor;
        this.bgColor = bgColor;
        this.selectable = selectable;
    }

    public String getWord() {
        return word;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public Color getTextColor() {
        return textColor;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public Color getBgColor() {
        return bgColor;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public boolean pointInside(int x, int y) {
        return this.selectable && this.x <= x && this.x + this.w >= x && this.y <= y && this.y + this.h >= y;
    }
}
