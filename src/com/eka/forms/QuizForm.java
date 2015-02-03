package com.eka.forms;

import com.eka.model.Model;
import com.eka.types.QuizItem;
import com.eka.types.WordState;
import com.eka.utils.Utils;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;


public class QuizForm extends JDialog {
    private JPanel pnlMain;
    private JLabel lblMain;

    private int lastIndex = -1;

    private boolean hasWords = true;

    public List<QuizItem> quizItems;
    public com.eka.model.Runtime runtime;
    public Model model;

    public QuizForm(List<QuizItem> quizItems) {
        this.setContentPane(pnlMain);
        this.setModal(true);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                runtime.isQuizActive = false;
                super.windowClosing(e);
            }
        });
        this.quizItems = quizItems;
        newWord();
    }

    public void keyPress(int key) {
        if (!hasWords)
            return;
        System.out.println("Quiz" + key);
        QuizItem item = quizItems.get(lastIndex);
        switch (key) {
            case 113:
                model.setWordState(item.index, WordState.UNKNOWN);
                break;
            case 114:
                model.setWordState(item.index, WordState.KNOWN);
                break;
            case 115:
                model.setWordState(item.index, WordState.NAME);
                break;
            case 76:
                model.setWordState(item.index, WordState.LEARNING);
                break;
            case 67:
                Utils.copyToClipboard(item.item.getWord());
            default:
                return;
        }
        newWord();
    }

    private void newWord() {
        lastIndex++;
        if (lastIndex >= quizItems.size()) {
            lblMain.setText("done");
            hasWords = false;
            return;
        }
        lblMain.setText(quizItems.get(lastIndex).text);
    }

}
