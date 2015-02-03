//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.eka.model;


import com.eka.database.FreqDB;
import com.eka.database.UserDB;
import com.eka.types.TextItem;
import com.eka.types.UniqueWord;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Runtime {

    public final Stat stat = new Stat();

    public int occurrences;
    public int occurrenceId;
    public List<TextItem> words;
    public List<Integer> lineStarts;
    public List<UniqueWord> uniqueWords;
  //  public int[] charWidths = new int[65536];
    public List<Integer> unknownUnique;
    public FontMetrics fm;
    public Display display;
    public Settings settings;
    public Reader reader;
    public UserDB userDB;
    public FreqDB freqDB;

    public boolean isQuizActive = false;

    public void filterUnknown() {
        this.unknownUnique.clear();

        for(int i = 0; i < uniqueWords.size(); ++i) {
            if(uniqueWords.get(i).unknown) {
                unknownUnique.add(i);
            }
        }

    }

    public Runtime() {


        this.unknownUnique = new ArrayList();
    }


}
