//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.eka.types;


public class TextItem {

    // The text value of the item
    private String word;

    // type - is it a word, space, other characters or newline symbol
    private WordType type;

    private int firstId;

    // index of the next occurrence of the word
    private int nextId;

    // index of the previous occurrence of the word
    private int prevId;

    // this occurrence ordinal number
    private int curIndex;

    // width in pixels
    private int width;

    // mutable part
    private int x;

    private int lineId;

    public TextItem(WordType type, String word, int width) {
        this.type = type;
        this.word = word;
        this.setWidth(width);
    }


    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getNextId() {
        return nextId;
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

    public int getPrevId() {
        return prevId;
    }

    public void setPrevId(int prevId) {
        this.prevId = prevId;
    }

    public int getCurIndex() {
        return curIndex;
    }

    public void setCurIndex(int curIndex) {
        this.curIndex = curIndex;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public WordType getType() {
        return type;
    }

    public void setType(WordType type) {
        this.type = type;
    }

    public int getFirstId() {
        return firstId;
    }

    public void setFirstId(int firstId) {
        this.firstId = firstId;
    }
}
