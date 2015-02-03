//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.eka.model;

import java.awt.Color;

public class Settings {
    public Color bgColor;
    public Color selectionColor;
    public Color knownColor;
    public Color unknownColor;
    public Color nameColor;
    public Color learningColor;
    public Color highFrequencyColor;
    public Color mediumFrequencyColor;
    public Color lowFrequencyColor;
    public int freqLevel1 = 5000;
    public int freqLevel2 = 25000;
    public int[] charWidths = new int[65536];

    public Settings() {
        for(int i = 0; i < 65536; ++i) {
            this.charWidths[i] = -1;
        }

    }

    public void setDefaults() {
        this.bgColor = new Color(237, 237, 237);
        this.selectionColor = new Color(136, 136, 136);
        this.knownColor = new Color(0, 32, 0);
        this.unknownColor = new Color(160, 0, 0);
        learningColor = new Color(106, 212, 34);
        this.nameColor = new Color(32, 112, 112);
        this.highFrequencyColor = new Color(0, 255, 0);
        this.mediumFrequencyColor = new Color(0, 0, 160);
        this.lowFrequencyColor = new Color(160, 160, 160);
    }
}
