package com.eka.database;

import com.eka.types.Locale;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class FreqDB {
    private Map<String, Integer> freqMap;

    public FreqDB() {
    }

    public void open(Locale locale, String workingDir) {
        System.out.println(System.currentTimeMillis());
        String s = "";

        try {
            FileInputStream i = new FileInputStream(workingDir + "data/" + locale.toString() + "_str.dat");
            Throwable arr$ = null;

            try {
                ObjectInputStream len$ = new ObjectInputStream(i);
                Throwable i$ = null;

                try {
                    s = (String)len$.readObject();
                } catch (Throwable var32) {
                    i$ = var32;
                    throw var32;
                } finally {
                    if(len$ != null) {
                        if(i$ != null) {
                            try {
                                len$.close();
                            } catch (Throwable var31) {
                                i$.addSuppressed(var31);
                            }
                        } else {
                            len$.close();
                        }
                    }

                }
            } catch (Throwable var34) {
                arr$ = var34;
                throw var34;
            } finally {
                if(i != null) {
                    if(arr$ != null) {
                        try {
                            i.close();
                        } catch (Throwable var30) {
                            arr$.addSuppressed(var30);
                        }
                    } else {
                        i.close();
                    }
                }

            }
        } catch (ClassNotFoundException | IOException var36) {
            var36.printStackTrace();
        }

        System.out.println(System.currentTimeMillis());
        this.freqMap = new HashMap();
        int index = 1;
        String[] words = s.split("\\.");
        int len = words.length;

        for(int i = 0; i < len; ++i) {
            String item = words[i];
            this.freqMap.put(item, Integer.valueOf(index++));
        }

        System.out.println(index);
        System.out.println(System.currentTimeMillis());
    }

    /**
     * Function return the index of a word in frequency dictionary.
     * The first word (the) has index 1, the next one 2 etc.
     * @param word word to check
     * @return     index in the frequency dictionary
     */
    public int getFreqRate(String word) {
        if(this.freqMap == null) {
            return -1;
        } else {
            word = word.toLowerCase();
            Integer rate = (Integer)this.freqMap.get(word);
            return rate == null?-1:rate.intValue();
        }
    }
}
