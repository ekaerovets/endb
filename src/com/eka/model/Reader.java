package com.eka.model;


import com.eka.types.Locale;
import com.eka.types.TextItem;
import com.eka.types.WordType;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class, used for reading and parsing a book.
 */
public class Reader {

    public static List<TextItem> parseFile(String filename, Locale locale) throws IOException {
        return readFileContent(filename, locale == Locale.CH);
    }

    private static void addWordItem(ArrayList<TextItem> words, WordType type, String content) {
        words.add(new TextItem(type, content, 0));
    }

    private static void parseLine(boolean isChinese, ArrayList<TextItem> words, String line) {
        WordType lastChar = WordType.NEWLINE;
        int newIndex = 0;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == 32) {
                if (lastChar != WordType.WORD && lastChar != WordType.CHARS) {
                    lastChar = WordType.SPACE;
                } else {
                    addWordItem(words, lastChar, line.substring(newIndex, i));
                    lastChar = WordType.SPACE;
                    newIndex = i;
                }
            } else if (!isChinese && (c >= 65 && c <= 90 || c >= 97 && c <= 122 || c >= 192 && c <= 255)) {
                if (lastChar != WordType.CHARS && lastChar != WordType.SPACE) {
                    lastChar = WordType.WORD;
                } else {
                    addWordItem(words, lastChar, line.substring(newIndex, i));
                    lastChar = WordType.WORD;
                    newIndex = i;
                }
            } else if (isChinese && c >= 19968 && c <= '\u9fff') {
                if (lastChar != WordType.CHARS && lastChar != WordType.SPACE) {
                    if (lastChar == WordType.NEWLINE) {
                        addWordItem(words, WordType.WORD, line.substring(i, i + 1));
                        newIndex = i + 1;
                    }
                } else {
                    addWordItem(words, lastChar, line.substring(newIndex, i));
                    addWordItem(words, WordType.WORD, line.substring(i, i + 1));
                    lastChar = WordType.NEWLINE;
                    newIndex = i + 1;
                }
            } else if (lastChar != WordType.WORD && lastChar != WordType.SPACE) {
                lastChar = WordType.CHARS;
            } else {
                addWordItem(words, lastChar, line.substring(newIndex, i));
                lastChar = WordType.CHARS;
                newIndex = i;
            }
        }
        if (lastChar != WordType.NEWLINE) {
            addWordItem(words, lastChar, line.substring(newIndex));
        }
    }

    private static ArrayList<TextItem> readFileContent(String filename, boolean isChinese) throws IOException {
        ArrayList<TextItem> words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (words.size() > 0) {
                    words.add(new TextItem(WordType.NEWLINE, "", 0));
                }
                parseLine(isChinese, words, line);
            }
        }
        return words;
    }

}
