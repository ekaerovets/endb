//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.eka.forms;

import com.eka.types.Locale;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;


public class LanguageSelect extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton chineseRadioButton;
    private JRadioButton englishRadioButton;
    private JRadioButton spanishRadioButton;
    public Locale locale;

    public LanguageSelect() {
        this.setContentPane(this.contentPane);
        this.setModal(true);
        this.getRootPane().setDefaultButton(this.buttonOK);
        this.buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LanguageSelect.this.onOK();
            }
        });
        this.buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LanguageSelect.this.onCancel();
            }
        });
        this.setDefaultCloseOperation(0);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                LanguageSelect.this.onCancel();
            }
        });
        this.contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LanguageSelect.this.onCancel();
            }
        }, KeyStroke.getKeyStroke(27, 0), 1);
    }

    private void onOK() {
        if(this.chineseRadioButton.isSelected()) {
            this.locale = Locale.CH;
        } else if(this.englishRadioButton.isSelected()) {
            this.locale = Locale.EN;
        } else {
            Locale var10001 = this.locale;
            this.locale = Locale.ES;
        }

        this.dispose();
    }

    private void onCancel() {
        this.locale = null;
        this.dispose();
    }
}
