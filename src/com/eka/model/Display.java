//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.eka.model;

import com.eka.types.TextItem;
import com.eka.types.UniqueWord;
import com.eka.types.WordState;
import com.eka.types.WordType;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Display {

    public List<DisplayItem> getDisplayItems() {
        return displayItems;
    }

    private List<DisplayItem> displayItems;

    private int lineHeight;
    private int displayHeight;
    private int heightInLines;
    private int selectionId;

    private Core core;
    private Settings settings;

    public List<Integer> lineStarts;

    public int getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(int selectionId) {
        this.selectionId = selectionId;
    }

    public int getHeightInLines() {
        return this.heightInLines;
    }

    private void calcHeightInLines() {
        heightInLines = 20;
        if(lineHeight > 0) {
            heightInLines = this.displayHeight / lineHeight;
            if(heightInLines > 100) {
                heightInLines = 100;
            }
        }
    }

    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
        calcHeightInLines();
    }

    public void setDisplayHeight(int displayHeight) {
        this.displayHeight = displayHeight;
        calcHeightInLines();
    }

    public Display(Core core, Settings settings) {
        this.core = core;
        this.settings = settings;
        displayItems = new ArrayList<>();
        selectionId = -1;
    }

    public Color getBgColor() {
        return settings.bgColor;
    }

    private void addDisplayItem(int x, int y, int w, boolean selectable, String data, int id) {

        Color bgColor = selectionId == id ? settings.selectionColor : settings.bgColor;
        Color textColor = null;
        Color lineColor;
        if (!selectable) {
            textColor = settings.knownColor;
            lineColor = settings.bgColor;
        } else {
            switch (core.getWordStatus(data)) {
                case KNOWN:
                    textColor = settings.knownColor;
                    break;
                case LEARNING:
                    textColor = settings.learningColor;
                    break;
                case NAME:
                    textColor = settings.nameColor;
                    break;
                case UNKNOWN:
                    textColor = settings.unknownColor;
                    break;
            }
            int freqRate = core.getFreqRate(data);
            if(freqRate == -1 || core.getWordStatus(data) == WordState.KNOWN || core.getWordStatus(data) == WordState.NAME) {
                lineColor = null;
            } else if(freqRate > settings.freqLevel2) {
                lineColor = settings.lowFrequencyColor;
            } else if(freqRate < settings.freqLevel1) {
                lineColor = settings.highFrequencyColor;
            } else {
                lineColor = settings.mediumFrequencyColor;
            }
        }

        this.displayItems.add(new DisplayItem(data, id, x, y, w, lineHeight, textColor, lineColor, bgColor, selectable));
    }

    private int getWordWidth(String word) {
        int width = 0;

        for(int i = 0; i < word.length(); ++i) {
            char c = word.charAt(i);
          //  width += this.runtime.charWidths[c];
        }

        return 40;
    }

    private void setListMode(int lineNumber) {
        List<UniqueWord> inRange = core.getInRange(lineNumber, lineNumber + heightInLines);
        displayItems.clear();
        for(int i = 0; i < inRange.size(); i++) {
            UniqueWord word = inRange.get(i);
            String wordStr = core.getWords().get(word.firstOccurrence).getWord();
            int freqRate = word.freqRate;
            String index = Integer.toString(lineNumber + i + 1) + " ";
            String rate = " (" + Integer.toString(freqRate) + ")";
            int w1 = getWordWidth(index);
            int w2 = core.getWords().get(word.firstOccurrence).getWidth();
            int w3 = getWordWidth(rate);
            this.addDisplayItem(0, i * lineHeight, w1, false, index, -1);
            this.addDisplayItem(w1, i * lineHeight, w2, true, wordStr, word.firstOccurrence);
            if(freqRate != -1) {
                addDisplayItem(w1 + w2, i * lineHeight, w3, false, rate, -1);
            }
        }
    }

    public void setLine(int lineNumber, boolean textMode) {
        if (textMode) {
            setTextMode(lineNumber);
        } else {
            setListMode(lineNumber);
        }
    }

    private void setTextMode(int lineNumber) {
        int maxLine = lineNumber + this.heightInLines - 1;
        this.displayItems.clear();
        if(lineNumber >= 0 && lineNumber < lineStarts.size()) {
            int startWord = lineStarts.get(lineNumber);

            for(int i = startWord; i < core.getWords().size(); ++i) {
                TextItem currentWord = core.getWords().get(i);
                if(currentWord.getLineId() > maxLine) {
                    break;
                }

                if(currentWord.getType() != WordType.NEWLINE && currentWord.getType() != WordType.SPACE) {
                    addDisplayItem(currentWord.getX(), (currentWord.getLineId() - lineNumber) * lineHeight,
                            currentWord.getWidth(), currentWord.getType() == WordType.WORD, currentWord.getWord(), i);
                }
            }
        }
    }

    public boolean onClick(int x, int y) {
        int oldSelectionId = selectionId;
        selectionId = -1;
        for (DisplayItem item : displayItems) {
            if (item.pointInside(x, y)) {
                this.selectionId = item.getId();
                break;
            }
        }
        return this.selectionId != oldSelectionId;
    }

}
