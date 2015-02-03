package com.eka.model;

import com.eka.forms.QuizForm;
import com.eka.types.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizGen {

    private static final int MAX_QUIZ = 1000;
    private static final int RANGE = 40;

    private static final Random rnd = new Random();

    public static List<QuizItem> prepareQuiz(Runtime runtime) {
        runtime.filterUnknown();
        int len = runtime.unknownUnique.size() > MAX_QUIZ ? MAX_QUIZ : runtime.unknownUnique.size();

        List<QuizItem> quizItems = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            UniqueWord uniqueWord = runtime.uniqueWords.get(runtime.unknownUnique.get(i));
            int occCount = rnd.nextInt(uniqueWord.occurrencesCount);
            int index = uniqueWord.firstOccurrence;
            for (int j = 0; j < occCount; j++) {
                index = runtime.words.get(index).getNextId();
            }
            int from = index - RANGE > 0 ? index - RANGE : 0;
            int to = index + RANGE >= runtime.words.size() ? runtime.words.size() - 1 : index + RANGE;

            String s = "<html>" + getStr(runtime.words, from, index);
            if (runtime.userDB.getWordStatus(runtime.words.get(index).getWord()) == WordState.LEARNING)
                s += "<font color=green>" + getStr(runtime.words, index, index + 1);
            else
                s += "<font color=red>" + getStr(runtime.words, index, index + 1);
            s += "</font>" + getStr(runtime.words, index + 1, to) + "</html>";

            QuizItem item = new QuizItem(s, runtime.words.get(index));
            item.index = index;
            quizItems.add(item);

        }
        Collections.shuffle(quizItems);
        return quizItems;

    }

    private static String getStr(List<TextItem> words, int from, int to) {
        String s = "";
        int break_pos = from + (to - from) / 2;
        for (int i = from; i < to; i++) {
            TextItem item = words.get(i);
            if (item.getType() == WordType.NEWLINE) {
                s += "<br/>";
            } else {
                s += item.getWord();
            }
            if (i == break_pos) {
                s += "<br>";
            }
        }
        return s;
    }

}
