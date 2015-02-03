package com.eka.model;

import com.eka.types.TextItem;
import com.eka.types.WordType;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by laby on 30.01.2015.
 */
public class Flower {

    // tabulation width in pixels. An offset for the first line and for any line after WordType.NEWLINE
    private static final int TAB_SIZE = 30;


    public static void updateWidth(List<TextItem> words, FontMetrics fm) {
        int[] charWidths = new int[65536];
        Arrays.fill(charWidths, -1);
        for (TextItem word: words) {
            int w = 0;
            for (int i = 0; i < word.getWord().length(); i++) {
                char c = word.getWord().charAt(i);
                if (charWidths[c] == -1) {
                    charWidths[c] = fm.charWidth(c);
                }
                w += charWidths[c];
            }
            word.setWidth(w);
        }
    }

    /**
     * Function calculates lineId and offset for each word, also it calculates the first word
     * for each line
     * @param words        the parsed list of words, representing the original text
     * @param displayWidth width of the text panel, each line must be no longer than @displayWidth
     * @return a list, each entry value is the index of the word in the list @words
     * As a side effect, for each word its position (lineId and xOffset) are calculated
     */
    public static ArrayList<Integer> reFlow(List<TextItem> words, int displayWidth) {
        ArrayList<Integer> lineStarts = new ArrayList<>();
        int curX = TAB_SIZE;
        int curLine = 0;
        int lastLine = -1;

        for (int i = 0; i < words.size(); i++) {
            TextItem curWord = words.get(i);
            if (curWord.getType() == WordType.NEWLINE) {
                curWord.setX(0);
                curLine++;
                curWord.setLineId(curLine);
                curX = TAB_SIZE;
            } else if (curX + curWord.getWidth() >= displayWidth && curX != 0) {
                curX = curWord.getType() == WordType.SPACE ? 0 : curWord.getWidth();
                curWord.setX(0);
                curLine++;
                curWord.setLineId(curLine);
            } else {
                curWord.setX(curX);
                curWord.setLineId(curLine);
                curX += curWord.getWidth();
            }

            if (curLine > lastLine) {
                lastLine = curLine;
                lineStarts.add(i);
            }
        }
        return lineStarts;
    }

}
