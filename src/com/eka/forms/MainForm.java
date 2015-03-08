//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.eka.forms;

import com.eka.model.Display;
import com.eka.model.DisplayItem;
import com.eka.model.Model;
import com.eka.model.Settings;
import com.eka.types.WordState;
import com.eka.utils.DisplayUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;


public class MainForm implements Observer {

    private Model model;
    static JFrame frame;
    private JButton modeButton;
    private JButton saveButton;
    private JScrollBar scrollBar1;
    private JPanel mainPanel;
    private JButton openButton;
    private JPanel pnlDisplay;
    private JLabel lblStat;
    private JLabel lblOccurrences;
    private JLabel dbCount;
    private JCheckBox chbQuizMode;

    public static void main(String[] args) {
        frame = new JFrame("MainForm");
        frame.setMinimumSize(new Dimension(600, 500));
        MainForm main = new MainForm();
        frame.setContentPane(main.mainPanel);
        frame.setDefaultCloseOperation(3);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        main.model.setFontMetrics(main.pnlDisplay.getFontMetrics(main.pnlDisplay.getFont()));
        main.model.setDimensions(main.pnlDisplay.getWidth(), main.pnlDisplay.getHeight());
        try {
            main.model.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MainForm() {
        model = new Model();
        model.addObserver(this);
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
        this.openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(MainForm.frame);
                if (returnVal == 0) {
                    LanguageSelect dialog = new LanguageSelect();
                    dialog.pack();
                    dialog.setLocationRelativeTo(MainForm.frame);
                    dialog.setVisible(true);
                    if (dialog.locale != null) {
                        Settings settings = model.getSettings();
                        switch (dialog.locale) {
                            case EN:
                                pnlDisplay.setFont(new Font("Comic Sans", 0, 14));
                                settings.freqLevel1 = 5000;
                                settings.freqLevel2 = 25000;
                                break;
                            case ES:
                                pnlDisplay.setFont(new Font("Comic Sans", 0, 14));
                                settings.freqLevel1 = 4000;
                                settings.freqLevel2 = 25000;
                                break;
                            case CH:
                                pnlDisplay.setFont(new Font("NSimSun Regular", 0, 22));
                                settings.freqLevel1 = 300;
                                settings.freqLevel2 = 900;
                        }

                        model.setFontMetrics(pnlDisplay.getFontMetrics(pnlDisplay.getFont()));
                        try {
                            MainForm.this.model.openBook(chooser.getSelectedFile().getAbsolutePath(), dialog.locale);
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
                model.onViewClick(e.getX(), e.getY());
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
                if (newPos >= 0) {
                    MainForm.this.model.onScroll(newPos);
                }

            }
        });

        KeyEventDispatcher keyEventDispatcher = new KeyEventDispatcher() {
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == 402) {
                    MainForm.this.model.onViewKeyPress(e.getKeyCode());
                    return true;
                } else {
                    return false;
                }
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);
        this.pnlDisplay.addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) {
                model.setDimensions(pnlDisplay.getWidth(), pnlDisplay.getHeight());
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
                if (e.getValueIsAdjusting()) {
                    MainForm.this.model.onScroll(e.getValue());
                }

            }
        });
    }

    public void update(Observable o, Object arg) {
/*        if(this.model.isButtonsChanged()) {
            this.saveButton.setEnabled(this.model.isSaveEnable());
        }*/

        if (this.model.isViewChanged()) {
            this.pnlDisplay.updateUI();
            this.lblStat.setText(DisplayUtils.FormatBookStat(model.getStateCount(WordState.UNKNOWN), model.getWordsCount()));
            this.lblOccurrences.setText(Integer.toString(model.getOccurrenceId()) + " / " + Integer.toString(model.getOccurrences()));
            this.dbCount.setText("In DB: " + Integer.toString(model.getDBSize()));
        }

        if (this.model.isScrollChanged()) {
            this.scrollBar1.setMaximum(this.model.getScrollHeight());
            this.scrollBar1.setValue(this.model.getScrollPos());
        }

        this.model.changesProcessed();
    }

    private void createUIComponents() {
        pnlDisplay = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
                g2.setRenderingHints(rh);
                int lineHeight = g.getFontMetrics().getHeight();
                Display fDisplay = model.getDisplay();
                Color bgColor = fDisplay.getBgColor();
                Color curColor = null;
                g.setColor(bgColor);
                g.fillRect(0, 0, this.getWidth(), this.getHeight());

                for (DisplayItem item : model.getDisplay().getDisplayItems()) {
                    Color wordBgColor = item.getBgColor();
                    if (!wordBgColor.equals(bgColor)) {
                        g.setColor(wordBgColor);
                        curColor = wordBgColor;
                        g.fillRect(item.getX() - 2, item.getY() + 4, item.getW() + 4, item.getH());
                    }

                    Color lineColor = item.getLineColor();
                    if (lineColor != null) {
                        if (!lineColor.equals(curColor)) {
                            curColor = lineColor;
                            g.setColor(lineColor);
                        }

                        g.drawLine(item.getX(), item.getY() + lineHeight + lineHeight / 5, item.getX() + item.getW(), item.getY() + lineHeight + lineHeight / 5);
                    }

                    Color textColor = item.getTextColor();
                    if (!textColor.equals(curColor)) {
                        curColor = textColor;
                        g.setColor(textColor);
                    }
                    g.drawString(item.getWord(), item.getX(), item.getY() + lineHeight);
                }

            }
        };
        pnlDisplay.setFont(new Font("Comic Sans", 0, 14));
    }
}
