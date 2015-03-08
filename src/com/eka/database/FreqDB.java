package com.eka.database;

import com.eka.types.CoreException;
import com.eka.types.Locale;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;


public class FreqDB {
    private Map<String, Integer> freqMap;

    public FreqDB() {
    }

    public void open(Locale locale, String workingDir) {
        String s;
        String filePath = workingDir + "data/" + locale.toString() + "_str.dat";
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            s = (String) in.readObject();
        } catch (Exception e) {
            throw new CoreException("Freq db reading error", e);
        }

        freqMap = new HashMap<>();
        int index = 1;
        for (String item : s.split("\\.")) {
            this.freqMap.put(item, index++);
        }
    }

    /**
     * Function return the index of a word in frequency dictionary.
     * The first word (the) has index 1, the next one 2 etc.
     *
     * @param word word to check
     * @return index in the frequency dictionary
     */
    public int getFreqRate(String word) {
        if (this.freqMap == null) {
            return -1;
        } else {
            Integer rate = this.freqMap.get(word.toLowerCase());
            return rate == null ? -1 : rate;
        }
    }
}
