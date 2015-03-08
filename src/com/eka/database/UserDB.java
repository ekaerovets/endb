//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.eka.database;

import com.eka.types.CoreException;
import com.eka.types.Locale;
import com.eka.types.WordState;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class UserDB {

    Map<String, Integer> data_cur;
    Map<String, Map<String, Integer>> data;

    public UserDB() {
    }

    @SuppressWarnings("unchecked")
    public void open(String workingDir) {
        data = new HashMap<>();

        String filePath = workingDir + "data/data.dat";
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            data = (Map<String, Map<String, Integer>>) in.readObject();
        } catch (Exception e) {
            throw new CoreException("Error while opening DB", e);
        }
    }

    public void create(String workingDir) {
        String filePath = workingDir + "data/data.dat";
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(this.data);
        } catch (IOException e) {
            throw new CoreException("Error while creating user DB", e);
        }
    }

    public void setLanguage(Locale locale) {
        if (data == null) {
            System.out.println("in setLanguage: no data loaded");
        } else {
            switch (locale) {
                case EN:
                    data_cur = data.get("en");
                    break;
                case ES:
                    data_cur = data.get("es");
                    break;
                case CH:
                    data_cur = data.get("ch");
            }

        }
    }

    public void save(String workingDir) {
        if (data != null) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(workingDir + "data/data.dat"))) {
                out.writeObject(data);
            } catch (IOException e) {
                throw new CoreException("Save data error", e);
            }
        }
    }

    public WordState getWordStatus(String word) {
        Integer status = data_cur.get(word.toLowerCase());
        if (status == null)
            return WordState.UNKNOWN;
        else if (status == 1)
            return WordState.KNOWN;
        else if (status == 2)
            return WordState.NAME;
        else if (status == 3)
            return WordState.LEARNING;
        else
            throw new RuntimeException("Incorrect data");
    }

    public void setWordStatus(String word, WordState newStatus) {
        if (newStatus == WordState.KNOWN) {
            this.data_cur.put(word.toLowerCase(), 1);
        } else if (newStatus == WordState.NAME) {
            this.data_cur.put(word.toLowerCase(), 2);
        } else if (newStatus == WordState.LEARNING) {
            this.data_cur.put(word.toLowerCase(), 3);
        } else if (newStatus == WordState.UNKNOWN) {
            this.data_cur.remove(word.toLowerCase());
        }

    }

    public int getSize() {
        if (data_cur == null)
            return -1;
        return this.data_cur.size();
    }



}
