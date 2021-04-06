package com.androidbase.utils;

import java.text.DecimalFormat;

public class DecimalUtils {

    public static String formatDecimalWithZero(double num, int newScale) {
        StringBuilder pattern = new StringBuilder("0.");
        for (int i = 0; i < newScale; i++) {
            pattern.append("0");
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());
        return df.format(num);
    }

    public static String formatDecimalWithZero(String numstr, int newScale) {
        StringBuilder pattern = new StringBuilder("0.");
        for (int i = 0; i < newScale; i++) {
            pattern.append("0");
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());
        return df.format(Double.valueOf(numstr));
    }

    public static String formatDecimal(double num, int newScale) {
        StringBuilder pattern = new StringBuilder("#.");
        for (int i = 0; i < newScale; i++) {
            pattern.append("#");
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());
        return df.format(num);
    }

    public static String formatDecimal(String numstr, int newScale) {
        StringBuilder pattern = new StringBuilder("#.");
        for (int i = 0; i < newScale; i++) {
            pattern.append("#");
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());
        return df.format(Double.valueOf(numstr));
    }

    public static String formatThousandthDecimal(double num, int newScale) {
        StringBuilder pattern = new StringBuilder(",##0.");
        for (int i = 0; i < newScale; i++) {
            pattern.append("#");
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());
        return df.format(num);
    }

    public static String formatThousandthDecimal(String numstr, int newScale) {
        StringBuilder pattern = new StringBuilder(",##0.");
        for (int i = 0; i < newScale; i++) {
            pattern.append("#");
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());
        return df.format(Double.valueOf(numstr));
    }

    public static String formatThousandthDecimalWithZero(double num, int newScale) {
        StringBuilder pattern = new StringBuilder(",##0.");
        for (int i = 0; i < newScale; i++) {
            pattern.append("0");
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());
        return df.format(num);
    }

    public static String formatThousandthDecimalWithZero(String numstr, int newScale) {
        StringBuilder pattern = new StringBuilder(",##0.");
        for (int i = 0; i < newScale; i++) {
            pattern.append("0");
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());
        return df.format(Double.valueOf(numstr));
    }

}
