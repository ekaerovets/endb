//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.eka.model;


import com.eka.database.FreqDB;
import com.eka.database.UserDB;
import com.eka.types.TextItem;
import com.eka.types.Locale;
import com.eka.types.WordState;
import com.eka.utils.Utils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.Observable;

public class Model extends Observable {
    private boolean isSaveEnable;
    private boolean buttonsChanged;
    private boolean scrollChanged;
    private boolean viewChanged;
    private int scrollHeight;
    private boolean textMode;
    private int textModeLineId;
    private int listModeLineId;
    private int displayHeightLines;
    private Runtime runtime = new Runtime();
    private String workingPath = "D:\\en_java\\";

    public boolean isButtonsChanged() {
        return this.buttonsChanged;
    }

    public boolean isScrollChanged() {
        return this.scrollChanged;
    }

    public boolean isViewChanged() {
        return this.viewChanged;
    }

    public boolean isSaveEnable() {
        return this.isSaveEnable;
    }

    public int getScrollHeight() {
        return this.scrollHeight;
    }

    public int getScrollPos() {
        return this.textMode ? this.textModeLineId : this.listModeLineId;
    }

    public void changesProcessed() {
        this.buttonsChanged = false;
        this.scrollChanged = false;
        this.viewChanged = false;
    }

    public Runtime getRuntime() {
        return this.runtime;
    }

    public void onSavePressed() {
        System.out.println("onSavePressed");
        this.runtime.userDB.save("");
        System.out.println("Db saved");
        this.setChanged();
        this.notifyObservers();
    }

    public void onViewClick(int x, int y) {
        if (this.runtime.display.onClick(x, y)) {
            int selectionId = this.runtime.display.getSelectionId(this.textMode);
            if (selectionId < 0) {
                return;
            }

            this.selectionChanged(selectionId);
            this.viewChanged = true;
            this.setChanged();
            this.notifyObservers();
        }

    }

    private void selectionChanged(int selectionId) {
        TextItem selectedWord = (TextItem) this.runtime.words.get(selectionId);
        this.runtime.occurrences = selectedWord.getParent().occurrencesCount;
        this.runtime.occurrenceId = ((TextItem) this.runtime.words.get(selectionId)).getCurIndex();
    }

    public void changeMode() {
        this.textMode = !this.textMode;
        if (!this.textMode) {
            this.runtime.filterUnknown();
            this.scrollHeight = this.runtime.unknownUnique.size() - this.displayHeightLines;
        } else {
            this.scrollHeight = this.runtime.lineStarts.size() - this.runtime.display.getHeightInLines();
        }

        this.runtime.display.setLine(this.textMode ? this.textModeLineId : this.listModeLineId, this.textMode);
        this.scrollChanged = true;
        this.viewChanged = true;
        this.setChanged();
        this.notifyObservers();
    }

    public void setWordState(int selectionId, WordState newState) {
        TextItem selectedWord = (TextItem) this.runtime.words.get(selectionId);
        String word = selectedWord.getWord().toLowerCase();
        int occurrences = selectedWord.getParent().occurrencesCount;
        WordState state = this.runtime.userDB.getWordStatus(word);
        switch (state) {
            case KNOWN:
            case LEARNING:
                this.runtime.stat.knownCount = this.runtime.stat.knownCount - occurrences;
                this.runtime.stat.unknownCount = this.runtime.stat.unknownCount + occurrences;
                break;
            case NAME:
                this.runtime.stat.nameCount = this.runtime.stat.nameCount - occurrences;
                this.runtime.stat.unknownCount = this.runtime.stat.unknownCount + occurrences;
            case UNKNOWN:
        }

        switch (newState) {
            case KNOWN:
            case LEARNING:
                this.runtime.userDB.setWordStatus(word, newState);
                this.runtime.stat.unknownCount = this.runtime.stat.unknownCount - occurrences;
                this.runtime.stat.knownCount = this.runtime.stat.knownCount + occurrences;
                break;
            case NAME:
                this.runtime.userDB.setWordStatus(word, WordState.NAME);
                this.runtime.stat.unknownCount = this.runtime.stat.unknownCount - occurrences;
                this.runtime.stat.nameCount = this.runtime.stat.nameCount + occurrences;
                break;
            case UNKNOWN:
                this.runtime.userDB.setWordStatus(word, WordState.UNKNOWN);
        }

        selectedWord.getParent().unknown = (newState == WordState.UNKNOWN || newState == WordState.LEARNING);
        if (!this.textMode) {
            this.runtime.filterUnknown();
        }

    }

    public void gotoWord(int id) {
        int lineId = ((TextItem) this.runtime.words.get(id)).getLineId();
        this.textMode = true;
        this.textModeLineId = lineId;
        this.runtime.display.setSelectionId(id);
        this.selectionChanged(id);
        this.scrollChanged = true;
    }

    public void onViewKeyPress(int key) {
        int selectionId = this.runtime.display.getSelectionId(this.textMode);
        if (selectionId >= 0) {
            TextItem selectedWord = runtime.words.get(selectionId);
            System.out.println(key);
            switch (key) {
                case 67:
                    // key C
                    Utils.copyToClipboard(selectedWord.getWord());
                    break;
                case 76:
                    // key L
                    setWordState(selectionId, WordState.LEARNING);
                    break;
                case 113:
                    this.setWordState(selectionId, WordState.UNKNOWN);
                    break;
                case 114:
                    this.setWordState(selectionId, WordState.KNOWN);
                    break;
                case 115:
                    this.setWordState(selectionId, WordState.NAME);
                    break;
                case 120:
                    System.out.println("F9 pressed");
                    if (this.textMode) {
                        if (selectedWord.getPrevId() != -1) {
                            this.gotoWord(selectedWord.getPrevId());
                        }
                    } else {
                        this.scrollHeight = this.runtime.lineStarts.size() - this.runtime.display.getHeightInLines();
                        this.gotoWord(selectedWord.getParent().firstOccurrence);
                    }
                    break;
                case 121:
                    if (this.textMode && selectedWord.getNextId() != -1) {
                        this.gotoWord(selectedWord.getNextId());
                    }
            }

            if (key == 123) {
                this.gotoLine(0);
            }

            this.runtime.stat.dbWordCount = this.runtime.userDB.getSize();
            this.runtime.display.setLine(this.textMode ? this.textModeLineId : this.listModeLineId, this.textMode);
            this.viewChanged = true;
            this.setChanged();
            this.notifyObservers();
        }
    }

    public void onScroll(int pos) {
        if (this.textMode) {
            this.textModeLineId = pos;
        } else {
            this.listModeLineId = pos;
        }

        this.runtime.display.setLine(pos, this.textMode);
        this.viewChanged = true;
        this.scrollChanged = true;
        this.setChanged();
        this.notifyObservers();
    }

    public Model() {
        this.runtime.settings = new Settings();
        this.runtime.settings.setDefaults();
        this.runtime.display = new Display(this.runtime);
        this.runtime.reader = new Reader(this.runtime);
        this.workingPath = "";
        this.runtime.userDB = new UserDB();
        this.runtime.freqDB = new FreqDB();
        this.runtime.userDB.open(this.workingPath);
    }

    public void init(int displayWidth) throws IOException {
        this.openBook(this.workingPath + "data/test.txt", Locale.EN, displayWidth);
    }

    public void openBook(String filename, Locale locale, int displayWidth) throws IOException {
        this.textMode = true;
        this.runtime.userDB.setLanguage(locale);
        this.runtime.freqDB.open(locale, this.workingPath);
        this.runtime.reader.parseFile(filename, locale);
        Flower.updateWidth(runtime.words, runtime.fm);
        runtime.lineStarts = Flower.reFlow(runtime.words, displayWidth);
        this.runtime.display.setLine(0, this.textMode);
        this.scrollHeight = this.runtime.lineStarts.size() - this.runtime.display.getHeightInLines();
        this.runtime.stat.dbWordCount = this.runtime.userDB.getSize();
        this.scrollChanged = true;
        this.viewChanged = true;
        this.setChanged();
        this.notifyObservers();
    }

    public void gotoLine(int lineNumber) {
        if (this.textMode) {
            this.textModeLineId = lineNumber;
        } else {
            this.listModeLineId = lineNumber;
        }

        this.scrollChanged = true;
        this.setChanged();
        this.notifyObservers();
    }

    public void onContextChanged(int width, int height, FontMetrics fm) {
        if (fm != null) {
            this.runtime.fm = fm;
            this.runtime.display.setLineHeight(fm.getHeight());
        }

        this.runtime.display.setDisplayHeight(height);
        this.displayHeightLines = this.runtime.display.getHeightInLines();
        //this.runtime.reader.setDisplayWidth(width);
        if (runtime.words != null) {
            Flower.updateWidth(runtime.words, runtime.fm);
            runtime.lineStarts = Flower.reFlow(runtime.words, width);
        }
        this.viewChanged = true;
        this.setChanged();
        this.notifyObservers();
    }
}
