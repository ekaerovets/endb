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


public class Display implements Iterable<IDisplayItem> {
    private List<Display.displayItem> displayItems;
    private Runtime runtime;
    private int lineHeight;
    private int displayHeight;
    private int heightInLines;
    private int selectionId;

    public Iterator<IDisplayItem> iterator() {
        return new displayItemsIterator<>();
    }

    public int getSelectionId(boolean textMode) {
        return !textMode && this.selectionId != -1?((UniqueWord)this.runtime.uniqueWords.get(this.selectionId)).firstOccurrence:this.selectionId;
    }

    public void setSelectionId(int selectionId) {
        this.selectionId = selectionId;
    }

    public int getHeightInLines() {
        return this.heightInLines;
    }

    private void calcHeightInLines() {
        this.heightInLines = 20;
        if(this.lineHeight > 0) {
            this.heightInLines = this.displayHeight / this.lineHeight;
            if(this.heightInLines > 100) {
                this.heightInLines = 100;
            }
        }

    }

    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
        this.calcHeightInLines();
    }

    public void setDisplayHeight(int displayHeight) {
        this.displayHeight = displayHeight;
        this.calcHeightInLines();
    }

    public Display(Runtime runtime) {
        this.runtime = runtime;
        this.displayItems = new ArrayList();
        this.selectionId = -1;
    }

    public Color getBgColor() {
        return this.runtime.settings.bgColor;
    }

    private void addDisplayItem(int x, int y, int w, boolean selectable, String data, int id) {
        Display.displayItem item = new displayItem();
        item.id = id;
        item.selectable = selectable;
        if(!selectable) {
            item.state = WordState.KNOWN;
        } else {
            item.state = this.runtime.userDB.getWordStatus(data);
            int freqRate = this.runtime.freqDB.getFreqRate(data);
            if(freqRate == -1) {
                item.frequency = null;
            } else if(freqRate > this.runtime.settings.freqLevel2) {
                item.frequency = Display.Frequency.LOW;
            } else if(freqRate < this.runtime.settings.freqLevel1) {
                item.frequency = Display.Frequency.HIGH;
            } else {
                item.frequency = Display.Frequency.MEDIUM;
            }
        }

        item.x = x;
        item.y = y;
        item.w = w;
        item.h = this.lineHeight;
        item.word = data;
        this.displayItems.add(item);
    }

    private int getWordWidth(String word) {
        int width = 0;

        for(int i = 0; i < word.length(); ++i) {
            char c = word.charAt(i);
          //  width += this.runtime.charWidths[c];
        }

        return 40;
    }

    private boolean setLineListMode(int lineNumber) {
        int maxLine = lineNumber + this.heightInLines - 1;
        this.displayItems.clear();
        if(lineNumber >= 0 && lineNumber < this.runtime.unknownUnique.size()) {
            for(int i = lineNumber; i < this.runtime.unknownUnique.size() && i <= maxLine; ++i) {
                UniqueWord word = (UniqueWord)this.runtime.uniqueWords.get(((Integer)this.runtime.unknownUnique.get(i)).intValue());
                String wordStr = ((TextItem) this.runtime.words.get(word.firstOccurrence)).getWord();
                int freqRate = word.freqRate;
                String index = Integer.toString(i + 1) + " ";
                String rate = " (" + Integer.toString(freqRate) + ")";
                int w1 = this.getWordWidth(index);
                int w2 = ((TextItem) this.runtime.words.get(word.firstOccurrence)).getWidth();
                int w3 = this.getWordWidth(rate);
                this.addDisplayItem(0, (i - lineNumber) * this.lineHeight, w1, false, index, -1);
                this.addDisplayItem(w1, (i - lineNumber) * this.lineHeight, w2, true, wordStr, ((Integer)this.runtime.unknownUnique.get(i)).intValue());
                if(freqRate != -1) {
                    this.addDisplayItem(w1 + w2, (i - lineNumber) * this.lineHeight, w3, false, rate, -1);
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public boolean setLine(int lineNumber, boolean textMode) {
        return textMode?this.setLineTextMode(lineNumber):this.setLineListMode(lineNumber);
    }

    private boolean setLineTextMode(int lineNumber) {
        int maxLine = lineNumber + this.heightInLines - 1;
        this.displayItems.clear();
        if(lineNumber >= 0 && lineNumber < this.runtime.lineStarts.size()) {
            int startWord = ((Integer)this.runtime.lineStarts.get(lineNumber)).intValue();

            for(int i = startWord; i < this.runtime.words.size(); ++i) {
                TextItem currentWord = (TextItem)this.runtime.words.get(i);
                if(currentWord.getLineId() > maxLine) {
                    break;
                }

                if(currentWord.getType() != WordType.NEWLINE && currentWord.getType() != WordType.SPACE) {
                    this.addDisplayItem(currentWord.getX(), (currentWord.getLineId() - lineNumber) * this.lineHeight, currentWord.getWidth(), currentWord.getType() == WordType.WORD, currentWord.getWord(), i);
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public boolean onClick(int x, int y) {
        int oldSelectionId = this.selectionId;
        this.selectionId = -1;
        Iterator i$ = this.displayItems.iterator();

        while(i$.hasNext()) {
            Display.displayItem item = (Display.displayItem)i$.next();
            if(item.pointInside(x, y)) {
                this.selectionId = item.id;
                break;
            }
        }

        return this.selectionId != oldSelectionId;
    }

    private class displayItemsIterator<IDisplayItem> implements Iterator<IDisplayItem> {
        int nextElement;

        private displayItemsIterator() {
            this.nextElement = 0;
        }

        public boolean hasNext() {
            return this.nextElement < Display.this.displayItems.size();
        }

        public IDisplayItem next() {
            return (IDisplayItem) displayItems.get(this.nextElement++);
        }

        public void remove() {
        }
    }

    private class displayItem implements IDisplayItem {
        private String word;
        private int id;
        private int x;
        private int y;
        private int w;
        private int h;
        private WordState state;
        private Display.Frequency frequency;
        private boolean selectable;

        private displayItem() {
        }

        public String getWord() {
            return this.word;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getW() {
            return this.w;
        }

        public int getH() {
            return this.h;
        }

        public Color getTextColor() {
            switch(this.state) {
                case UNKNOWN:
                    return Display.this.runtime.settings.unknownColor;
                case KNOWN:
                    return Display.this.runtime.settings.knownColor;
                case LEARNING:
                    return runtime.settings.learningColor;
                default:
                    return Display.this.runtime.settings.nameColor;
            }
        }

        public Color getBgColor() {
            return this.selectable && this.id == Display.this.selectionId?Display.this.runtime.settings.selectionColor:Display.this.runtime.settings.bgColor;
        }

        public Color getLineColor() {
            if(this.state == WordState.UNKNOWN && this.frequency != null) {
                switch(this.frequency) {
                    case HIGH:
                        return Display.this.runtime.settings.highFrequencyColor;
                    case MEDIUM:
                        return Display.this.runtime.settings.mediumFrequencyColor;
                    default:
                        return Display.this.runtime.settings.lowFrequencyColor;
                }
            } else {
                return null;
            }
        }

        private boolean pointInside(int x, int y) {
            return this.selectable && this.x <= x && this.x + this.w >= x && this.y <= y && this.y + this.h >= y;
        }
    }

    private static enum Frequency {
        HIGH,
        MEDIUM,
        LOW;

        private Frequency() {
        }
    }
}
