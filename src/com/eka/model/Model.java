//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.eka.model;

import com.eka.types.Locale;
import com.eka.types.TextItem;
import com.eka.types.WordState;
import com.eka.utils.Utils;

import java.awt.*;
import java.io.IOException;
import java.util.Observable;

public class Model extends Observable {
    private boolean buttonsChanged;
    private boolean scrollChanged;
    private boolean viewChanged;
    private int scrollHeight;
    private boolean textMode;
    private int textModeLineId;
    private int listModeLineId;
    private int displayHeightLines;
    private boolean quizMode;
    private String workingPath = "D:\\en_java\\";
    private int displayWidth;
    private int displayHeight;
    private FontMetrics fm;

    public void setFontMetrics(FontMetrics fm) {
        this.fm = fm;
        onContextChanged();
    }

    private Settings settings;

    private Display display;

    public Display getDisplay() {
        return display;
    }

    public int occurrences;
    public int occurrenceId;


    private Core core;

    public Settings getSettings() {
        return settings;
    };

    public void setDimensions(int width, int height) {
        this.displayWidth = width;
        this.displayHeight = height;
        onContextChanged();
    }

    public void setQuizMode(boolean quizMode) {
        this.quizMode = quizMode;
        if (textMode && quizMode) {
            int quizWordId = core.getQuizWordId();
            if (quizWordId != -1) {
                gotoWord(quizWordId);
                display.setLine(textModeLineId, textMode);
                viewChanged = true;
                setChanged();
                notifyObservers();
            }
        }

    }

    public boolean isButtonsChanged() {
        return this.buttonsChanged;
    }

    public boolean isScrollChanged() {
        return this.scrollChanged;
    }

    public boolean isViewChanged() {
        return this.viewChanged;
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

    public void onSavePressed() {
        System.out.println("onSavePressed");
        core.saveUserDB("");
        System.out.println("Db saved");
        this.setChanged();
        this.notifyObservers();
    }

    public void onViewClick(int x, int y) {
        if (display.onClick(x, y)) {
            int selectionId = display.getSelectionId();
            /*if (selectionId < 0) {
                return;
            }*/
            display.setLine(textMode ? textModeLineId : listModeLineId, textMode);

            selectionChanged(selectionId);
            viewChanged = true;
            setChanged();
            notifyObservers();
        }

    }

    private void selectionChanged(int selectionId) {
        if (selectionId >= 0) {
            occurrences = core.getOccurencesCount(core.getWords().get(selectionId).getWord());
            occurrenceId = core.getWords().get(selectionId).getCurIndex();
        } else {
            occurrences = 0;
            occurrenceId = 0;
        }
    }

    public void changeMode() {
        textMode = !textMode;
        if (!textMode) {
            scrollHeight = core.getUnknownUniqueCount() - this.displayHeightLines;
        } else {
            scrollHeight = display.lineStarts.size() - display.getHeightInLines();
        }

        display.setLine(textMode ? textModeLineId : listModeLineId, textMode);
        scrollChanged = true;
        viewChanged = true;
        setChanged();
        notifyObservers();
    }

    public void setWordState(int selectionId, WordState newState) {
        TextItem selectedWord = core.getWords().get(selectionId);
        String word = selectedWord.getWord().toLowerCase();
        core.setWordStatus(word, newState);
    }

    private void gotoWord(int id) {
        int lineId = (core.getWords().get(id)).getLineId() - 3;
        if (lineId < 0)
            lineId = 0;
        textMode = true;
        textModeLineId = lineId;
        display.setSelectionId(id);
        selectionChanged(id);
        scrollChanged = true;
    }

    public void onViewKeyPress(int key) {
        int selectionId = display.getSelectionId();
        if (selectionId >= 0) {
            boolean stateChanged = false;
            TextItem selectedWord = core.getWords().get(selectionId);
            System.out.println(key);
            switch (key) {
                case 67:
                    // key C
                    Utils.copyToClipboard(selectedWord.getWord());
                    break;
                case 76:
                    // key L
                    setWordState(selectionId, WordState.LEARNING);
                    stateChanged = true;
                    break;
                case 113:
                case 85: // U
                    this.setWordState(selectionId, WordState.UNKNOWN);
                    stateChanged = true;
                    break;
                case 114:
                case 75: // K
                    this.setWordState(selectionId, WordState.KNOWN);
                    stateChanged = true;
                    break;
                case 115:
                case 78:
                    this.setWordState(selectionId, WordState.NAME);
                    stateChanged = true;
                    break;
                case 83: // S (skip)
                    stateChanged = true;
                    break;
                case 120:
                    if (textMode) {
                        if (selectedWord.getPrevId() != -1) {
                            this.gotoWord(selectedWord.getPrevId());
                        }
                    } else {
                        this.scrollHeight = display.lineStarts.size() - display.getHeightInLines();
                        this.gotoWord(selectedWord.getFirstId());
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

            if (textMode && quizMode && stateChanged) {
                int quizWordId = core.getQuizWordId();
                if (quizWordId != -1) {
                    gotoWord(quizWordId);
                }
            }
            display.setLine(textMode ? textModeLineId : listModeLineId, textMode);
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

        display.setLine(pos, this.textMode);
        this.viewChanged = true;
        this.scrollChanged = true;
        this.setChanged();
        this.notifyObservers();
    }

    public Model() {
        settings = new Settings();
        settings.setDefaults();
        core = new Core();
        display = new Display(core, settings);
        workingPath = "";
        core.openUserDB(workingPath);
    }

    public void init() throws IOException {
        this.openBook(this.workingPath + "data/test.txt", Locale.EN);
    }

    public void openBook(String filename, Locale locale) throws IOException {
        this.textMode = true;
        core.openFreqDB(locale, workingPath);
        core.readFile(filename, locale);
        Flower.updateWidth(core.getWords(), fm);
        display.lineStarts = Flower.reFlow(core.getWords(), displayWidth);
        display.setLine(0, textMode);
        scrollHeight = display.lineStarts.size() - display.getHeightInLines();
        scrollChanged = true;
        viewChanged = true;
        setChanged();
        notifyObservers();
    }

    public void gotoLine(int lineNumber) {
        if (textMode) {
            textModeLineId = lineNumber;
        } else {
            listModeLineId = lineNumber;
        }

        scrollChanged = true;
        setChanged();
        notifyObservers();
    }

    private void onContextChanged() {
        if (fm == null)
            return;
        display.setLineHeight(fm.getHeight());
        display.setDisplayHeight(displayHeight);
        this.displayHeightLines = display.getHeightInLines();
        if (core.getWords() != null) {
            Flower.updateWidth(core.getWords(), fm);
            display.lineStarts = Flower.reFlow(core.getWords(), displayWidth);
        }
        this.viewChanged = true;
        this.setChanged();
        this.notifyObservers();
    }

    public int getOccurrences() {
        return occurrences;
    }

    public int getOccurrenceId() {
        return occurrenceId;
    }

    public int getWordsCount() {
        return core.getWordCount();
    }

    public int getDBSize() {
        return core.getDBSize();
    }

    public int getStateCount(WordState state) {
        switch (state) {
            case KNOWN:
                return core.getKnownCount();
            case LEARNING:
                return core.getLearningCount();
            case NAME:
                return core.getNameCount();
            case UNKNOWN:
                return core.getUnknownCount();
        }
        return 0;
    }

}
