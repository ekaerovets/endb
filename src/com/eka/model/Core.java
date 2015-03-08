package com.eka.model;

import com.eka.database.FreqDB;
import com.eka.database.UserDB;
import com.eka.types.Locale;
import com.eka.types.*;

import java.io.IOException;
import java.util.*;

public class Core {

    private UserDB userDB;
    private FreqDB freqDB;
    private List<UniqueWord> uniqueWords;
    private Map<String, UniqueWord> uniqueIndex;
    private List<TextItem> words;
    private int wordCount;
    private int knownCount;
    private int nameCount;
    private int unknownCount;
    private int learningCount;
    private int unknownUniqueCount;

    private Random rnd = new Random();

    public Core() {
        userDB = new UserDB();
        freqDB = new FreqDB();
    }

    /**
     * Function parses specified text file in specified locale
     *
     * @param filename full name of the file to parse
     * @param locale   locale, used for parsing the file
     * @throws IOException
     */
    public void readFile(String filename, Locale locale) throws IOException {
        words = Reader.parseFile(filename, locale);
        index(userDB, freqDB);
    }

    public void openUserDB(String workingDir) {
        userDB.open(workingDir);
    }

    public void openFreqDB(Locale locale, String workingDir) {
        userDB.setLanguage(locale);
        freqDB.open(locale, workingDir);
    }

    public WordState getWordStatus(String word) {
        return userDB.getWordStatus(word);
    }

    public void setWordStatus(String word, WordState newStatus) {

        System.out.println(word + " -> " + newStatus);
        int occurences = getOccurencesCount(word);
        switch (userDB.getWordStatus(word)) {
            case KNOWN:
                knownCount -= occurences;
                break;
            case LEARNING:
                learningCount -= occurences;
                break;
            case NAME:
                nameCount -= occurences;
                break;
            case UNKNOWN:
                unknownCount -= occurences;
                break;
        }
        switch (newStatus) {
            case KNOWN:
                knownCount += occurences;
                break;
            case LEARNING:
                learningCount += occurences;
                break;
            case NAME:
                nameCount += occurences;
                break;
            case UNKNOWN:
                unknownCount += occurences;
                break;
        }

        boolean oldUnknown = uniqueIndex.get(word).unknown;
        boolean newUnknown = newStatus == WordState.LEARNING || newStatus == WordState.UNKNOWN;

        if (oldUnknown && !newUnknown) {
            unknownUniqueCount--;
        } else if (!oldUnknown && newUnknown) {
            unknownUniqueCount++;
        }

        uniqueIndex.get(word).unknown = newUnknown;

        userDB.setWordStatus(word, newStatus);
    }

    public void saveUserDB(String workingDir) {
        userDB.save(workingDir);
    }

    public int getFreqRate(String word) {
        return freqDB.getFreqRate(word);
    }

    public int getOccurencesCount(String word) {
        return uniqueIndex.get(word.toLowerCase()).occurrencesCount;
    }

    public int getUnknownUniqueCount() {
        return unknownUniqueCount;
    }

    public List<TextItem> getWords() {
        return words;
    }

    public int getDBSize() {
        return userDB.getSize();
    }

    public int getWordCount() {
        return wordCount;
    }

    public int getKnownCount() {
        return knownCount;
    }

    public int getNameCount() {
        return nameCount;
    }

    public int getUnknownCount() {
        return unknownCount;
    }

    public int getLearningCount() {
        return learningCount;
    }

    public List<UniqueWord> getInRange(int from, int to) {
        List<UniqueWord> words = new LinkedList<>();
        if (to <= from) {
            return words;
        }
        int index = 0;
        for (UniqueWord word : uniqueWords) {
            if (!word.unknown) {
                continue;
            }
            if (index >= from) {
                words.add(word);
            }
            index++;
            if (index == to) {
                break;
            }
        }
        return words;
    }

    private void index(UserDB userDB, FreqDB freqDB) {
        uniqueWords = new ArrayList<>();
        uniqueIndex = new HashMap<>();

        String item;
        for (int i = 0; i < words.size(); ++i) {
            TextItem word = words.get(i);
            if (word.getType() == WordType.WORD) {
                item = word.getWord().toLowerCase();
                UniqueWord uniqueWord;
                if (uniqueIndex.containsKey(item)) {
                    uniqueWord = uniqueIndex.get(item);
                    word.setPrevId(uniqueWord.lastOccurrence);
                    word.setNextId(-1);
                    words.get(uniqueWord.lastOccurrence).setNextId(i);
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
                    uniqueIndex.put(item, uniqueWord);
                    uniqueWords.add(uniqueWord);
                }

                word.setFirstId(uniqueWord.firstOccurrence);
            }
        }

        knownCount = 0;
        unknownCount = 0;
        nameCount = 0;
        wordCount = 0;

        UniqueWord uniqueWord;
        for (Iterator it = uniqueIndex.keySet().iterator(); it.hasNext(); uniqueWord.freqRate = freqDB.getFreqRate(item)) {
            item = (String) it.next();
            uniqueWord = uniqueIndex.get(item);
            int occurrences = uniqueWord.occurrencesCount;
            WordState state = userDB.getWordStatus(item);
            switch (state) {
                case KNOWN:
                    knownCount = knownCount + occurrences;
                    uniqueWord.unknown = false;
                    break;
                case LEARNING:
                    knownCount = knownCount + occurrences;
                    uniqueWord.unknown = true;
                    break;
                case NAME:
                    nameCount = nameCount + occurrences;
                    uniqueWord.unknown = false;
                    break;
                case UNKNOWN:
                    unknownCount = unknownCount + occurrences;
                    uniqueWord.unknown = true;
            }
            if (uniqueWord.unknown) {
                unknownUniqueCount++;
            }
            wordCount = wordCount + occurrences;
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

    public int getQuizWordId() {
        List<UniqueWord> inRange = getInRange(0, 500);
        if (inRange.size() == 0) {
            return -1;
        }
        int index = rnd.nextInt(inRange.size());
        UniqueWord uniqueWord = inRange.get(index);
        int occurrencesCount = uniqueWord.occurrencesCount;
        index = rnd.nextInt(occurrencesCount);
        int id = uniqueWord.firstOccurrence;
        for (int i = 0; i < index; i++) {
            id = words.get(id).getNextId();
        }
        return id;
    }

}
