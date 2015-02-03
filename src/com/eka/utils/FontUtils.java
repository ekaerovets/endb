package com.eka.utils;

import java.awt.*;

/**
 * Created by laby on 29.01.2015.
 */
public class FontUtils {

    private int[] charWidths;
    private FontMetrics fm;

    public FontUtils(FontMetrics fm) {
        this.fm = fm;
        charWidths = new int[65536];

    }

}
