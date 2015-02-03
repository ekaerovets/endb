//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.eka.utils;

public class DisplayUtils {
    public DisplayUtils() {
    }

    public static String FormatBookStat(int unknownWords, int totalWords) {
        return Integer.toString(unknownWords) + "/" + Integer.toString(totalWords) +
                " (" + String.format("%.2f%%", new Object[]{Double.valueOf((double)unknownWords * 100.0D / (double)totalWords)}) + ")";
    }



}
