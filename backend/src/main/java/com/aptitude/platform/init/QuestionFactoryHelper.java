package com.aptitude.platform.init;

import com.aptitude.platform.model.Chapter;
import com.aptitude.platform.model.Difficulty;
import com.aptitude.platform.model.Question;
import com.aptitude.platform.model.QuestionOption;

import java.util.*;

/**
 * Shared builders and formatting helpers for parameterized aptitude question generation.
 */
public final class QuestionFactoryHelper {

    private QuestionFactoryHelper() {}

    public static Question buildQ(Chapter ch, String text, Difficulty diff, String exp, String[] opts, int correctIdx) {
        Question q = new Question(ch, text, diff, exp);
        String[] keys = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            q.addOption(new QuestionOption(keys[i], opts[i], i == correctIdx));
        }
        return q;
    }

    public static String[] buildNumericOptions(int correct, int[] offsets, int cIdx, String affix) {
        Set<Integer> used = new HashSet<>();
        used.add(correct);
        List<Integer> wrongs = new ArrayList<>();
        for (int off : offsets) {
            int cand = Math.max(1, correct + off);
            if (!used.contains(cand)) {
                used.add(cand);
                wrongs.add(cand);
            }
            if (wrongs.size() == 3) break;
        }
        int fallback = 1;
        while (wrongs.size() < 3) {
            int cand = Math.max(1, correct + fallback++);
            if (!used.contains(cand)) {
                used.add(cand);
                wrongs.add(cand);
            }
        }
        String[] res = new String[4];
        int w = 0;
        for (int i = 0; i < 4; i++) {
            int val = (i == cIdx) ? correct : wrongs.get(w++);
            if (affix.startsWith("Rs.")) {
                res[i] = affix + val;
            } else {
                res[i] = val + affix;
            }
        }
        return res;
    }

    public static String[] buildDecimalOptions(double correct, double[] offsets, int cIdx, String suffix) {
        Set<String> used = new HashSet<>();
        String correctStr = formatCleanNumber(correct) + suffix;
        used.add(correctStr);
        List<String> wrongs = new ArrayList<>();
        for (double off : offsets) {
            double cand = Math.max(0.5, Math.round((correct + off) * 100.0) / 100.0);
            String s = formatCleanNumber(cand) + suffix;
            if (!used.contains(s)) {
                used.add(s);
                wrongs.add(s);
            }
            if (wrongs.size() == 3) break;
        }
        int fallback = 1;
        while (wrongs.size() < 3) {
            String s = formatCleanNumber(correct + fallback++) + suffix;
            if (!used.contains(s)) {
                used.add(s);
                wrongs.add(s);
            }
        }
        String[] res = new String[4];
        int w = 0;
        for (int i = 0; i < 4; i++) {
            res[i] = (i == cIdx) ? correctStr : wrongs.get(w++);
        }
        return res;
    }

    public static String formatCleanPercent(double v) {
        if (Math.abs(v - Math.round(v)) < 0.01) {
            return Math.round(v) + "%";
        }
        return String.format(Locale.US, "%.2f%%", v);
    }

    public static String formatCleanNumber(double v) {
        if (Math.abs(v - Math.round(v)) < 0.01) {
            return String.valueOf(Math.round(v));
        }
        return String.format(Locale.US, "%.2f", v);
    }

    public static String shiftWord(String word, int shift) {
        StringBuilder sb = new StringBuilder();
        for (char c : word.toCharArray()) {
            char shifted = (char) ('A' + ((c - 'A' + shift) % 26));
            sb.append(shifted);
        }
        return sb.toString();
    }

    public static String reverseStr(String s) {
        return new StringBuilder(s).reverse().toString();
    }

    public static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    public static void swap(String[] arr, int a, int b) {
        String tmp = arr[a];
        arr[a] = arr[b];
        arr[b] = tmp;
    }
}
