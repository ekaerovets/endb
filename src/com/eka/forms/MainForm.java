//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.eka.forms;

import com.eka.model.Display;
import com.eka.model.IDisplayItem;
import com.eka.model.Model;
import com.eka.model.QuizGen;
import com.eka.types.QuizItem;
import com.eka.utils.DisplayUtils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.util.*;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.filechooser.FileNameExtensionFilter;


public class MainForm implements Observer {
    
    private Model model;
    static JFrame frame;
    private JButton modeButton;
    private JButton saveButton;
    private JScrollBar scrollBar1;
    private JPanel mainPanel;
    private JButton openButton;
    private JButton settingsButton;
    private JPanel pnlDisplay;
    private JLabel lblStat;
    private JLabel lblOccurrences;
    private JLabel dbCount;
    private JButton btnQuiz;
    private QuizForm quizForm;

    public static void main(String[] args) {
        frame = new JFrame("MainForm");
        frame.setMinimumSize(new Dimension(600, 500));
        MainForm main = new MainForm();
        frame.setContentPane(main.mainPanel);
        frame.setDefaultCloseOperation(3);
        frame.pack();
        frame.setLocationRelativeTo((Component)null);
        frame.setVisible(true);
        main.model.onContextChanged(main.pnlDisplay.getWidth(), main.pnlDisplay.getHeight(), main.pnlDisplay.getFontMetrics(main.pnlDisplay.getFont()));
        try {
            main.model.init(main.pnlDisplay.getWidth());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MainForm() {
        this.model = new Model();
        this.model.addObserver(this);
        this.saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainForm.this.model.onSavePressed();
            }
        });
        this.modeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainForm.this.model.changeMode();
            }
        });
        btnQuiz.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<QuizItem> quizItems = QuizGen.prepareQuiz(model.getRuntime());
                quizForm = new QuizForm(quizItems);
                quizForm.model = model;
//                form.quizItems = quizItems;
                quizForm.runtime = model.getRuntime();
                model.getRuntime().isQuizActive = true;
                quizForm.pack();
                quizForm.setLocationRelativeTo(MainForm.frame);
                quizForm.setVisible(true);
            }
        });
        this.openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", new String[]{"txt"});
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(MainForm.frame);
                if(returnVal == 0) {
                    LanguageSelect dialog = new LanguageSelect();
                    dialog.pack();
                    dialog.setLocationRelativeTo(MainForm.frame);
                    dialog.setVisible(true);
                    if(dialog.locale != null) {
                        switch(dialog.locale) {
                            case EN:
                                MainForm.this.pnlDisplay.setFont(new Font("Comic Sans", 0, 14));
                                MainForm.this.model.getRuntime().settings.freqLevel1 = 5000;
                                MainForm.this.model.getRuntime().settings.freqLevel2 = 25000;
                                break;
                            case ES:
                                MainForm.this.pnlDisplay.setFont(new Font("Comic Sans", 0, 14));
                                MainForm.this.model.getRuntime().settings.freqLevel1 = 4000;
                                MainForm.this.model.getRuntime().settings.freqLevel2 = 25000;
                                break;
                            case CH:
                                MainForm.this.pnlDisplay.setFont(new Font("NSimSun Regular", 0, 22));
                                MainForm.this.model.getRuntime().settings.freqLevel1 = 300;
                                MainForm.this.model.getRuntime().settings.freqLevel2 = 900;
                        }

                        MainForm.this.model.onContextChanged(MainForm.this.pnlDisplay.getWidth(), MainForm.this.pnlDisplay.getHeight(), MainForm.this.pnlDisplay.getFontMetrics(MainForm.this.pnlDisplay.getFont()));
                        try {
                            MainForm.this.model.openBook(chooser.getSelectedFile().getAbsolutePath(), dialog.locale, pnlDisplay.getWidth());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }

                }
            }
        });
        this.pnlDisplay.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
                MainForm.this.model.onViewClick(e.getX(), e.getY());
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });
        this.pnlDisplay.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                int newPos = MainForm.this.scrollBar1.getValue() + e.getWheelRotation();
                if(newPos >= 0) {
                    MainForm.this.model.onScroll(newPos);
                }

            }
        });

        KeyEventDispatcher keyEventDispatcher = new KeyEventDispatcher() {
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (MainForm.this.model.getRuntime().isQuizActive) {
                    if (e.getID() == 401)
                        quizForm.keyPress(e.getKeyCode());
                } else if(e.getID() == 401) {
                    System.out.println(e.getKeyCode());
                    MainForm.this.model.onViewKeyPress(e.getKeyCode());
                }

                return true;
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);
        this.pnlDisplay.addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) {
                MainForm.this.model.onContextChanged(MainForm.this.pnlDisplay.getWidth(), MainForm.this.pnlDisplay.getHeight(), (FontMetrics) null);
            }

            public void componentMoved(ComponentEvent e) {
            }

            public void componentShown(ComponentEvent e) {
            }

            public void componentHidden(ComponentEvent e) {
            }
        });
        this.scrollBar1.addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if(e.getValueIsAdjusting()) {
                    MainForm.this.model.onScroll(e.getValue());
                }

            }
        });
    }

    public void update(Observable o, Object arg) {
        if(this.model.isButtonsChanged()) {
            this.saveButton.setEnabled(this.model.isSaveEnable());
        }

        if(this.model.isViewChanged()) {
            this.pnlDisplay.updateUI();
            this.lblStat.setText(DisplayUtils.FormatBookStat(this.model.getRuntime().stat.unknownCount, this.model.getRuntime().stat.wordCount));
            this.lblOccurrences.setText(Integer.toString(this.model.getRuntime().occurrenceId) + " / " + Integer.toString(this.model.getRuntime().occurrences));
            this.dbCount.setText("In DB: " + Integer.toString(this.model.getRuntime().stat.dbWordCount));
        }

        if(this.model.isScrollChanged()) {
            this.scrollBar1.setMaximum(this.model.getScrollHeight());
            this.scrollBar1.setValue(this.model.getScrollPos());
        }

        this.model.changesProcessed();
    }

    private void createUIComponents() {
        this.pnlDisplay = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g;
                RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
                g2.setRenderingHints(rh);
                int lineHeight = g.getFontMetrics().getHeight();
                Display fDisplay = MainForm.this.model.getRuntime().display;
                Color bgColor = fDisplay.getBgColor();
                Color curColor = null;
                g.setColor(bgColor);
                g.fillRect(0, 0, this.getWidth(), this.getHeight());

                IDisplayItem item;
                for(Iterator i$ = MainForm.this.model.getRuntime().display.iterator(); i$.hasNext(); g.drawString(item.getWord(), item.getX(), item.getY() + lineHeight)) {
                    item = (IDisplayItem)i$.next();
                    Color wordBgColor = item.getBgColor();
                    if(!wordBgColor.equals(bgColor)) {
                        g.setColor(wordBgColor);
                        curColor = wordBgColor;
                        g.fillRect(item.getX() - 2, item.getY() + 4, item.getW() + 4, item.getH());
                    }

                    Color lineColor = item.getLineColor();
                    if(lineColor != null) {
                        if(!lineColor.equals(curColor)) {
                            curColor = lineColor;
                            g.setColor(lineColor);
                        }

                        g.drawLine(item.getX(), item.getY() + lineHeight + lineHeight / 5, item.getX() + item.getW(), item.getY() + lineHeight + lineHeight / 5);
                    }

                    Color textColor = item.getTextColor();
                    if(!textColor.equals(curColor)) {
                        curColor = textColor;
                        g.setColor(textColor);
                    }
                }

            }
        };
        this.pnlDisplay.setFont(new Font("Comic Sans", 0, 14));
    }
}
