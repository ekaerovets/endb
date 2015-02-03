package com.eka.model;


import com.eka.database.FreqDB;
import com.eka.database.UserDB;
import com.eka.types.*;
import com.eka.types.Locale;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;

/**
 * A utility class, used for reading and parsing a book.
 */
public class Reader {

    public Runtime runtime;

    private void addWordItem(ArrayList<TextItem> words, WordType type, String content) {
        words.add(new TextItem(type, content, 0));
    }

    private void parseLine(boolean isChinese, ArrayList<TextItem> words, String line) {
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

    public Reader(Runtime runtime) {
        this.runtime = runtime;
    }

    private void index() {
        ArrayList uniqueWords = new ArrayList();
        HashMap temp = new HashMap();
        this.runtime.uniqueWords = uniqueWords;
        List words = this.runtime.words;

        String item;
        for (int i = 0; i < words.size(); ++i) {
            TextItem word = (TextItem) words.get(i);
            if (word.getType() == WordType.WORD) {
                item = word.getWord().toLowerCase();
                UniqueWord uniqueWord;
                if (temp.containsKey(item)) {
                    uniqueWord = (UniqueWord) temp.get(item);
                    word.setPrevId(uniqueWord.lastOccurrence);
                    word.setNextId(-1);
                    ((TextItem) words.get(uniqueWord.lastOccurrence)).setNextId(i);
                    uniqueWord.lastOccurrence = i;
                    ++uniqueWord.occurrencesCount;
                    word.setCurIndex(uniqueWord.occurrencesCount);
                } else {
                    uniqueWord = new UniqueWord();
                    uniqueWord.firstOccurrence = i;
                    uniqueWord.lastOccurrence = i;
                    uniqueWord.occurrencesCount = 1;
                    word.setPrevId(-1);
                    word.setNextId(-1);
                    word.setCurIndex(1);
                    temp.put(item, uniqueWord);
                    uniqueWords.add(uniqueWord);
                }

                word.setParent(uniqueWord);
            }
        }

        Stat stat = this.runtime.stat;
        stat.knownCount = 0;
        stat.unknownCount = 0;
        stat.nameCount = 0;
        stat.wordCount = 0;
        UserDB var11 = this.runtime.userDB;
        FreqDB var12 = this.runtime.freqDB;

        UniqueWord uniqueWord;
        for (Iterator it = temp.keySet().iterator(); it.hasNext(); uniqueWord.freqRate = var12.getFreqRate(item)) {
            item = (String) it.next();
            uniqueWord = (UniqueWord) temp.get(item);
            int occurrences = uniqueWord.occurrencesCount;
            WordState state = var11.getWordStatus(item);
            switch (state) {
                case KNOWN:
                    stat.knownCount = stat.knownCount + occurrences;
                    uniqueWord.unknown = false;
                    break;
                case LEARNING:
                    stat.knownCount = stat.knownCount + occurrences;
                    uniqueWord.unknown = true;
                    break;
                case NAME:
                    stat.nameCount = stat.nameCount + occurrences;
                    uniqueWord.unknown = false;
                    break;
                case UNKNOWN:
                    stat.unknownCount = stat.unknownCount + occurrences;
                    uniqueWord.unknown = true;
            }

            stat.wordCount = stat.wordCount + occurrences;
        }

        Collections.sort(uniqueWords, new Comparator<UniqueWord>() {
            public int compare(UniqueWord o1, UniqueWord o2) {
                int r1 = o1.freqRate;
                r1 = r1 == -1 ? 2147483647 : r1;
                int r2 = o2.freqRate;
                r2 = r2 == -1 ? 2147483647 : r2;
                return r1 - r2;
            }
        });
    }

    public void parseFile(String filename, Locale locale) throws IOException {
        ArrayList<TextItem> words = readFileContent(filename, locale == Locale.CH);
        runtime.words = words;
        index();
    }

    private ArrayList<TextItem> readFileContent(String filename, boolean isChinese) throws IOException {

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
