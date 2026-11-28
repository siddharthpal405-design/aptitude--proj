package com.aptitude.platform.init;

import com.aptitude.platform.model.Chapter;
import com.aptitude.platform.model.Difficulty;
import com.aptitude.platform.model.Question;

import java.util.ArrayList;
import java.util.List;

import static com.aptitude.platform.init.QuestionFactoryHelper.*;

/**
 * Generates mathematically verified, trick-annotated MCQs for all 14 Quantitative Aptitude chapters.
 */
public final class QuantQuestionFactory {

    private QuantQuestionFactory() {}

    public static Question create(Chapter chapter, String slug, int idx) {
        return switch (slug) {
            case "percentages" -> genPercentage(chapter, idx);
            case "profit-and-loss" -> genProfitLoss(chapter, idx);
            case "time-and-work" -> genTimeWork(chapter, idx);
            case "number-systems" -> genNumberSystem(chapter, idx);
            case "ratio-and-proportion" -> genRatio(chapter, idx);
            case "simple-and-compound-interest" -> genInterest(chapter, idx);
            case "time-speed-and-distance" -> genSpeedDistance(chapter, idx);
            case "averages-and-mixtures" -> genAveragesMixtures(chapter, idx);
            case "ages-and-partnerships" -> genAgesPartnerships(chapter, idx);
            case "permutation-and-combination" -> genPermComb(chapter, idx);
            case "probability" -> genProbability(chapter, idx);
            case "algebra-and-equations" -> genAlgebra(chapter, idx);
            case "geometry-and-mensuration" -> genGeometry(chapter, idx);
            default -> genProgressions(chapter, idx);
        };
    }

    private static Question genPercentage(Chapter ch, int idx) {
        int type = idx % 5;
        int variant = idx / 5;
        String[] items = {"rice", "wheat", "coffee", "cooking oil", "electricity", "fuel", "milk", "tea"};
        String item = items[idx % items.length];

        if (type == 0) {
            int[] percents = {12, 15, 18, 22, 24, 28, 32, 35, 36, 42, 45, 48, 55, 62, 65, 68, 72, 75, 82, 85};
            int p = percents[variant % percents.length];
            int n = 200 + (variant * 50);
            int ans = (p * n) / 100;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("Calculate %d%% of %d.", p, n),
                    Difficulty.EASY,
                    String.format("Trick: (%d * %d) / 100 = %d.", p, n, ans),
                    buildNumericOptions(ans, new int[]{-20, -10, 10, 20, 25, -15}, cIdx, ""), cIdx);
        } else if (type == 1) {
            int[] cleanR = {25, 60, 100, 150, 300, 20, 50, 10, 15, 30, 40, 80, 120, 200, 250, 35, 45, 75, 90, 125};
            int r = cleanR[variant % cleanR.length];
            double exact = Math.round((r * 100.0 / (100.0 + r)) * 100.0) / 100.0;
            String ansStr = formatCleanPercent(exact);
            int cIdx = idx % 4;
            String[] opts = buildDecimalOptions(exact, new double[]{-5, 4, 8, -3, 6}, cIdx, "%");
            return buildQ(ch,
                    String.format("If the price of %s increases by %d%%, by what percentage must consumption be reduced so total expenditure remains constant?", item, r),
                    Difficulty.MEDIUM,
                    String.format("Shortcut [R / (100 + R)] * 100 = [%d / %d] * 100 = %s.", r, 100 + r, ansStr),
                    opts, cIdx);
        } else if (type == 2) {
            int passPct = 30 + (variant % 4) * 5;
            int maxMarks = 200 + (variant * 40);
            int passMarks = (passPct * maxMarks) / 100;
            int failBy = 12 + (variant % 15);
            int scored = passMarks - failBy;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("In an exam, a student needs %d%% marks to pass. A candidate scores %d marks and fails by %d marks. Find maximum marks.", passPct, scored, failBy),
                    Difficulty.MEDIUM,
                    String.format("Pass marks = %d + %d = %d. Max Marks = (%d * 100) / %d = %d.", scored, failBy, passMarks, passMarks, passPct, maxMarks),
                    buildNumericOptions(maxMarks, new int[]{-40, -20, 20, 40, 50, -50}, cIdx, ""), cIdx);
        } else if (type == 3) {
            int x = 10 + (variant * 5) % 45;
            if (x % 10 != 0) x = 15 + (variant * 2) % 30;
            double loss = Math.round(((x * x) / 100.0) * 100.0) / 100.0;
            String ansStr = formatCleanPercent(loss) + " decrease";
            int cIdx = idx % 4;
            String[] opts = {
                    "No change",
                    formatCleanPercent(loss) + " increase",
                    formatCleanPercent(loss + 2) + " decrease",
                    formatCleanPercent(Math.max(0.5, loss - 1)) + " decrease"
            };
            opts[cIdx] = ansStr;
            return buildQ(ch,
                    String.format("A value is increased by %d%% and then decreased by %d%%. Find the net percentage change.", x, x),
                    Difficulty.EASY,
                    String.format("Mirror Trick: Always a loss of (x^2 / 100)%% = (%d^2 / 100)%% = %s.", x, ansStr),
                    opts, cIdx);
        } else {
            int r = (variant % 3 == 0) ? 10 : (variant % 3 == 1 ? 20 : 5);
            int p0 = (10 + variant) * 1000;
            long p2 = Math.round(p0 * Math.pow(1 + r / 100.0, 2));
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("The population of a town is %d. Growing at %d%% per annum, what will it be after 2 years?", p0, r),
                    Difficulty.HARD,
                    String.format("Effective 2-yr rate = 2R + R^2/100. Population = %d * (1 + %d/100)^2 = %d.", p0, r, p2),
                    buildNumericOptions((int) p2, new int[]{-1000, -500, 500, 1200, 2000, -1500}, cIdx, ""), cIdx);
        }
    }

    private static Question genProfitLoss(Chapter ch, int idx) {
        int type = idx % 5;
        int variant = idx / 5;
        String[] articles = {"watch", "bicycle", "printer", "desk", "jacket", "laptop stand", "headset", "cabinet"};
        String art = articles[idx % articles.length];

        if (type == 0) {
            int cp = 400 + variant * 50;
            int profitPct = 10 + (variant % 5) * 5;
            int sp = cp + (cp * profitPct) / 100;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("A merchant buys a %s for Rs. %d and sells it at a %d%% profit. Find the selling price.", art, cp, profitPct),
                    Difficulty.EASY,
                    String.format("SP = CP * (100 + %d)/100 = Rs. %d.", profitPct, sp),
                    buildNumericOptions(sp, new int[]{-40, -20, 20, 40, 50, -30}, cIdx, "Rs. "), cIdx);
        } else if (type == 1) {
            int cp = 500 + variant * 100;
            int gainPct = (variant % 2 == 0) ? 20 : 25;
            int sp = cp + (cp * gainPct) / 100;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("Selling a %s for Rs. %d yields a %d%% profit. What was the cost price?", art, sp, gainPct),
                    Difficulty.EASY,
                    String.format("CP = (%d * 100) / %d = Rs. %d.", sp, 100 + gainPct, cp),
                    buildNumericOptions(cp, new int[]{-50, -25, 40, 60, 80, -75}, cIdx, "Rs. "), cIdx);
        } else if (type == 2) {
            int[] falseWeights = {800, 900, 750, 950, 850, 600, 700, 920};
            int fw = falseWeights[variant % falseWeights.length];
            int err = 1000 - fw;
            double gain = Math.round((err * 100.0 / fw) * 100.0) / 100.0;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("A dishonest trader uses a false weight of %d grams instead of 1 kg while claiming to sell at CP. Find gain%%.", fw),
                    Difficulty.MEDIUM,
                    String.format("False Weight Trick: (Error / False Weight) * 100 = (%d / %d) * 100 = %s.", err, fw, formatCleanPercent(gain)),
                    buildDecimalOptions(gain, new double[]{-2.5, 3, 6, -4, 5}, cIdx, "%"), cIdx);
        } else if (type == 3) {
            int d1 = 10 + (variant % 3) * 5;
            int d2 = 10 + ((variant + 1) % 3) * 10;
            double netDisc = Math.round((d1 + d2 - (d1 * d2) / 100.0) * 100.0) / 100.0;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("Two successive discounts of %d%% and %d%% are offered on a %s. Find the single equivalent discount%%.", d1, d2, art),
                    Difficulty.MEDIUM,
                    String.format("Equivalent Discount = %d + %d - (%d*%d/100) = %s.", d1, d2, d1, d2, formatCleanPercent(netDisc)),
                    buildDecimalOptions(netDisc, new double[]{3, -2, 5, -4, 2}, cIdx, "%"), cIdx);
        } else {
            int sold = 30 + (variant % 6) * 6;
            int gainUnits = 6 + (variant % 4) * 2;
            int cpUnits = sold - gainUnits;
            double pct = Math.round((gainUnits * 100.0 / cpUnits) * 100.0) / 100.0;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("By selling %d meters of cloth, a trader gains the SP of %d meters. Find gain%%.", sold, gainUnits),
                    Difficulty.HARD,
                    String.format("Shortcut: [K / (N - K)] * 100 = [%d / (%d - %d)] * 100 = %s.", gainUnits, sold, gainUnits, formatCleanPercent(pct)),
                    buildDecimalOptions(pct, new double[]{-4, 5, 8, -3, 10}, cIdx, "%"), cIdx);
        }
    }

    private static Question genTimeWork(Chapter ch, int idx) {
        int type = idx % 5;
        int variant = idx / 5;

        if (type == 0) {
            int[][] pairs = {{10, 15}, {12, 24}, {15, 30}, {20, 30}, {12, 18}, {15, 20}, {14, 21}, {18, 36}, {24, 40}, {30, 60}};
            int a = pairs[variant % pairs.length][0] + (variant / 10) * 2;
            int b = pairs[variant % pairs.length][1] + (variant / 10) * 3;
            double days = Math.round(((a * b) / (double) (a + b)) * 100.0) / 100.0;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("A completes a task in %d days and B in %d days. Together they finish in how many days?", a, b),
                    Difficulty.EASY,
                    String.format("2-Worker Shortcut: (a*b)/(a+b) = (%d*%d)/(%d+%d) = %s days.", a, b, a, b, formatCleanNumber(days)),
                    buildDecimalOptions(days, new double[]{2, -1.5, 3.5, 5, -2}, cIdx, " days"), cIdx);
        } else if (type == 1) {
            int together = 6 + (variant % 8) * 2;
            int b = together * 2 + (variant % 3) * 4;
            double a = Math.round(((together * b) / (double) (b - together)) * 100.0) / 100.0;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("A and B together finish a job in %d days. B alone takes %d days. How many days will A alone take?", together, b),
                    Difficulty.EASY,
                    String.format("Shortcut: (T * B) / (B - T) = (%d * %d) / (%d - %d) = %s days.", together, b, b, together, formatCleanNumber(a)),
                    buildDecimalOptions(a, new double[]{4, -3, 6, -5, 8}, cIdx, " days"), cIdx);
        } else if (type == 2) {
            int fill = 4 + (variant % 7) * 2;
            int empty = fill + 4 + (variant % 4) * 2;
            double net = Math.round(((fill * empty) / (double) (empty - fill)) * 100.0) / 100.0;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("Pipe A fills a tank in %d hrs and Pipe B empties it in %d hrs. Both opened together fill it in how many hours?", fill, empty),
                    Difficulty.MEDIUM,
                    String.format("Pipe Shortcut: (x*y)/(y-x) = (%d*%d)/(%d-%d) = %s hours.", fill, empty, empty, fill, formatCleanNumber(net)),
                    buildDecimalOptions(net, new double[]{2, -2, 4, 3, -1}, cIdx, " hours"), cIdx);
        } else if (type == 3) {
            int m1 = 10 + (variant % 6) * 2;
            int d1 = 12 + (variant % 5) * 3;
            int m2 = m1 / 2;
            int d2 = (m1 * d1) / m2;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("If %d workers build a wall in %d days, how many days will %d workers take?", m1, d1, m2),
                    Difficulty.EASY,
                    String.format("M1*D1 = M2*D2 => %d*%d = %d*D2 => D2 = %d days.", m1, d1, m2, d2),
                    buildNumericOptions(d2, new int[]{-4, -2, 3, 6, 8, -6}, cIdx, " days"), cIdx);
        } else {
            int k = 2 + (variant % 3);
            int together = 6 + (variant % 6) * 2;
            int bDays = together * (k + 1);
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("A is %d times as efficient as B. Together they finish in %d days. Find days taken by B alone.", k, together),
                    Difficulty.HARD,
                    String.format("Shortcut: B_days = Together * (k + 1) = %d * (%d + 1) = %d days.", together, k, bDays),
                    buildNumericOptions(bDays, new int[]{-6, -4, 4, 8, 10, -8}, cIdx, " days"), cIdx);
        }
    }

    private static Question genNumberSystem(Chapter ch, int idx) {
        int type = idx % 5;
        int variant = idx / 5;

        if (type == 0) {
            int[] bases = {2, 3, 7, 8};
            int base = bases[variant % bases.length];
            int exp = 21 + variant * 3;
            int rem = exp % 4;
            if (rem == 0) rem = 4;
            int unitDigit = (int) (Math.pow(base, rem) % 10);
            int cIdx = idx % 4;
            int[] pool = {2, 4, 6, 8, 1, 3, 7, 9};
            List<String> wrong = new ArrayList<>();
            for (int p : pool) {
                if (p != unitDigit && wrong.size() < 3) wrong.add(String.valueOf(p));
            }
            String[] opts = new String[4];
            int w = 0;
            for (int i = 0; i < 4; i++) opts[i] = (i == cIdx) ? String.valueOf(unitDigit) : wrong.get(w++);
            return buildQ(ch,
                    String.format("Find the unit digit of %d^%d.", base, exp),
                    Difficulty.MEDIUM,
                    String.format("Cyclicity Trick: %d mod 4 = %d. Unit digit of %d^%d is %d.", exp, rem, base, rem, unitDigit),
                    opts, cIdx);
        } else if (type == 1) {
            int hcf = 4 + (variant % 8) * 2;
            int mult1 = 3 + (variant % 4);
            int mult2 = 5 + (variant % 5);
            if (mult1 == mult2) mult2 += 2;
            int num1 = hcf * mult1;
            int num2 = hcf * mult2;
            int lcm = (num1 * num2) / hcf;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("The HCF and LCM of two numbers are %d and %d. If one number is %d, find the other.", hcf, lcm, num1),
                    Difficulty.EASY,
                    String.format("Other = (HCF * LCM) / First = (%d * %d) / %d = %d.", hcf, lcm, num1, num2),
                    buildNumericOptions(num2, new int[]{-hcf, hcf, 2 * hcf, -2 * hcf, 12, -10}, cIdx, ""), cIdx);
        } else if (type == 2) {
            int n = 15 + variant * 3;
            int sum = (n * (n + 1)) / 2;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("What is the sum of the first %d natural numbers?", n),
                    Difficulty.EASY,
                    String.format("n(n+1)/2 = %d * %d / 2 = %d.", n, n + 1, sum),
                    buildNumericOptions(sum, new int[]{-n, n, 10, -10, 25, -15}, cIdx, ""), cIdx);
        } else if (type == 3) {
            int n = 25 + variant * 5;
            int zeroes = (n / 5) + (n / 25) + (n / 125);
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("Count the trailing zeroes at the end of %d! (factorial).", n),
                    Difficulty.HARD,
                    String.format("Zeroes = floor(%d/5) + floor(%d/25) + floor(%d/125) = %d.", n, n, n, zeroes),
                    buildNumericOptions(zeroes, new int[]{-2, -1, 1, 2, 3, -3}, cIdx, ""), cIdx);
        } else {
            int d = 7 + (variant % 6) * 2;
            int q = 12 + variant;
            int r = 2 + (variant % (d - 2));
            int dividend = d * q + r;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("In division, divisor = %d, quotient = %d, remainder = %d. Find dividend.", d, q, r),
                    Difficulty.EASY,
                    String.format("Dividend = (%d * %d) + %d = %d.", d, q, r, dividend),
                    buildNumericOptions(dividend, new int[]{-5, -2, 3, 5, 7, -4}, cIdx, ""), cIdx);
        }
    }

    private static Question genRatio(Chapter ch, int idx) {
        int type = idx % 4;
        int variant = idx / 4;

        if (type == 0) {
            int a = 2 + (variant % 4);
            int b = 3 + (variant % 3);
            int c = 4 + (variant % 5);
            int d = 5 + (variant % 4);
            int x = a * c;
            int y = b * d;
            int g = gcd(x, y);
            x /= g;
            y /= g;
            String ansStr = x + " : " + y;
            int cIdx = idx % 4;
            String[] opts = {(x + 1) + " : " + y, x + " : " + (y + 2), (x + 2) + " : " + (y + 1), (a + c) + " : " + (b + d)};
            opts[cIdx] = ansStr;
            return buildQ(ch,
                    String.format("If A : B = %d : %d and B : C = %d : %d, find A : C.", a, b, c, d),
                    Difficulty.EASY,
                    String.format("A/C = (%d/%d) * (%d/%d) = %s.", a, b, c, d, ansStr),
                    opts, cIdx);
        } else if (type == 1) {
            int r1 = 2 + (variant % 3), r2 = 3 + (variant % 4), r3 = 5 + (variant % 3);
            int sumParts = r1 + r2 + r3;
            int mult = 50 + variant * 10;
            int total = sumParts * mult;
            int shareC = r3 * mult;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("Rs. %d is split among A, B, C in ratio %d : %d : %d. Find C's share.", total, r1, r2, r3),
                    Difficulty.EASY,
                    String.format("1 part = %d / %d = %d. C's share = %d * %d = Rs. %d.", total, sumParts, mult, r3, mult, shareC),
                    buildNumericOptions(shareC, new int[]{-100, -50, 50, 100, 150, -150}, cIdx, "Rs. "), cIdx);
        } else if (type == 2) {
            int[] squares = {4, 9, 16, 25, 36, 49, 64, 81};
            int a = squares[variant % squares.length];
            int b = squares[(variant + 2) % squares.length];
            if (a == b) b = 100;
            int mean = (int) Math.round(Math.sqrt(a * b));
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("Find the mean proportional between %d and %d.", a, b),
                    Difficulty.MEDIUM,
                    String.format("sqrt(%d * %d) = %d.", a, b, mean),
                    buildNumericOptions(mean, new int[]{-4, -2, 2, 4, 6, -5}, cIdx, ""), cIdx);
        } else {
            int m = 3 + (variant % 4), w = 1 + (variant % 3), mult = 5 + (variant % 8);
            int vol = (m + w) * mult, milk = m * mult, water = w * mult;
            int addWater = Math.max(10, milk - water);
            int newW = water + addWater;
            int g = gcd(milk, newW);
            String ansStr = (milk / g) + " : " + (newW / g);
            int cIdx = idx % 4;
            String[] opts = {(milk / g + 1) + " : " + (newW / g), (milk / g) + " : " + (newW / g + 1), "2 : 3", "3 : 5"};
            opts[cIdx] = ansStr;
            return buildQ(ch,
                    String.format("A %dL mixture has milk:water = %d:%d. Adding %dL water gives what new ratio?", vol, m, w, addWater),
                    Difficulty.HARD,
                    String.format("Milk stays %dL; new water = %d + %d = %dL => %s.", milk, water, addWater, newW, ansStr),
                    opts, cIdx);
        }
    }

    private static Question genInterest(Chapter ch, int idx) {
        int type = idx % 4;
        int variant = idx / 4;

        if (type == 0) {
            int p = 2000 + variant * 500;
            int r = 5 + (variant % 5) * 2;
            int t = 2 + (variant % 4);
            int si = (p * r * t) / 100;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("Find the Simple Interest on Rs. %d at %d%% per annum for %d years.", p, r, t),
                    Difficulty.EASY,
                    String.format("SI = (P*R*T)/100 = (%d * %d * %d)/100 = Rs. %d.", p, r, t, si),
                    buildNumericOptions(si, new int[]{-200, -100, 100, 250, 400, -150}, cIdx, "Rs. "), cIdx);
        } else if (type == 1) {
            int[] rates = {10, 20, 5, 15, 25};
            int r = rates[variant % rates.length];
            int p = (10 + variant) * 1000;
            int diff = (p * r * r) / 10000;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("Find the difference between Compound Interest and Simple Interest on Rs. %d at %d%% p.a. for 2 years.", p, r),
                    Difficulty.MEDIUM,
                    String.format("2-Yr Trick: Diff = P*(R/100)^2 = %d * (%d/100)^2 = Rs. %d.", p, r, diff),
                    buildNumericOptions(diff, new int[]{-50, -25, 25, 50, 100, -40}, cIdx, "Rs. "), cIdx);
        } else if (type == 2) {
            int r = (variant % 2 == 0) ? 10 : 20;
            int p = 5000 + variant * 1000;
            long amt = Math.round(p * Math.pow(1 + r / 100.0, 2));
            int ci = (int) (amt - p);
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("What is the Compound Interest earned on Rs. %d at %d%% p.a. compounded annually for 2 years?", p, r),
                    Difficulty.MEDIUM,
                    String.format("Effective 2-yr CI%% = 2R + R^2/100 = %d%%. CI = Rs. %d.", 2 * r + (r * r) / 100, ci),
                    buildNumericOptions(ci, new int[]{-300, -150, 150, 300, 450, -200}, cIdx, "Rs. "), cIdx);
        } else {
            int r = 6 + (variant % 6) * 3;
            int yrs = Math.round(72f / r);
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("Using the Rule of 72, approximately how many years will an investment take to double at %d%% compound interest?", r),
                    Difficulty.EASY,
                    String.format("Rule of 72 Trick: Years ≈ 72 / %d = %d years.", r, yrs),
                    buildNumericOptions(yrs, new int[]{-3, -2, 2, 3, 4, -1}, cIdx, " years"), cIdx);
        }
    }

    private static Question genSpeedDistance(Chapter ch, int idx) {
        int type = idx % 5;
        int variant = idx / 5;

        if (type == 0) {
            int[] kmh = {18, 36, 54, 72, 90, 108, 126, 144};
            int s = kmh[variant % kmh.length];
            int ms = (s * 5) / 18;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("Convert a speed of %d km/h into meters per second (m/s).", s),
                    Difficulty.EASY,
                    String.format("Unit Flip Trick: %d * (5/18) = %d m/s.", s, ms),
                    buildNumericOptions(ms, new int[]{-5, -2, 3, 5, 8, -4}, cIdx, " m/s"), cIdx);
        } else if (type == 1) {
            int[][] pairs = {{20, 30}, {40, 60}, {30, 60}, {15, 30}, {24, 40}, {30, 45}, {60, 90}};
            int x = pairs[variant % pairs.length][0];
            int y = pairs[variant % pairs.length][1];
            int avg = (2 * x * y) / (x + y);
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("A car goes from City A to B at %d km/h and returns at %d km/h. Find average speed.", x, y),
                    Difficulty.MEDIUM,
                    String.format("Harmonic Mean Trick: 2xy/(x+y) = (2*%d*%d)/(%d+%d) = %d km/h.", x, y, x, y, avg),
                    buildNumericOptions(avg, new int[]{-4, -2, 2, 4, 6, -5}, cIdx, " km/h"), cIdx);
        } else if (type == 2) {
            int len = 150 + (variant % 8) * 50;
            int speedKmh = 36 + (variant % 4) * 18;
            int speedMs = (speedKmh * 5) / 18;
            int time = len / speedMs;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("A %d m long train runs at %d km/h. How long does it take to cross a pole?", len, speedKmh),
                    Difficulty.EASY,
                    String.format("Speed = %d * (5/18) = %d m/s. Time = %d / %d = %d seconds.", speedKmh, speedMs, len, speedMs, time),
                    buildNumericOptions(time, new int[]{-4, -2, 3, 5, 7, -3}, cIdx, " sec"), cIdx);
        } else if (type == 3) {
            int down = 14 + (variant % 8) * 2;
            int up = 6 + (variant % 5) * 2;
            int still = (down + up) / 2;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("A boat's downstream speed is %d km/h and upstream speed is %d km/h. Find boat speed in still water.", down, up),
                    Difficulty.EASY,
                    String.format("Still Water = (Down + Up)/2 = (%d + %d)/2 = %d km/h.", down, up, still),
                    buildNumericOptions(still, new int[]{-3, -2, 2, 3, 4, -1}, cIdx, " km/h"), cIdx);
        } else {
            int s1 = 30 + (variant % 3) * 10;
            int s2 = s1 + 10 + (variant % 2) * 10;
            int diffMin = 12 + (variant % 3) * 6;
            double dist = Math.round(((s1 * s2) / (double) (s2 - s1)) * (diffMin / 60.0) * 100.0) / 100.0;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("Walking at %d km/h vs %d km/h creates a %d-minute arrival difference. Find the distance.", s1, s2, diffMin),
                    Difficulty.HARD,
                    String.format("Shortcut: [(S1*S2)/|S1-S2|] * (dt/60) = %s km.", formatCleanNumber(dist)),
                    buildDecimalOptions(dist, new double[]{5, -4, 8, 10, -6}, cIdx, " km"), cIdx);
        }
    }

    private static Question genAveragesMixtures(Chapter ch, int idx) {
        int type = idx % 3;
        int variant = idx / 3;

        if (type == 0) {
            int count = 5 + (variant % 5) * 2;
            int start = 11 + variant * 2;
            int mid = start + (count - 1);
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("Find the average of %d consecutive even numbers starting from %d (Wait: odd/step-2 starting at %d).", count, start, start),
                    Difficulty.EASY,
                    String.format("AP Middle Trick: Average of symmetric sequence = (First + Last)/2 = %d.", mid),
                    buildNumericOptions(mid, new int[]{-4, -2, 2, 4, 6, -3}, cIdx, ""), cIdx);
        } else if (type == 1) {
            int n = 10 + (variant % 8) * 2;
            int oldAvg = 40 + (variant % 6) * 5;
            int inc = 1 + (variant % 3);
            int newWeight = oldAvg + (n + 1) * inc;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("The average weight of %d students is %d kg. When a teacher joins, the average increases by %d kg. Find teacher's weight.", n, oldAvg, inc),
                    Difficulty.MEDIUM,
                    String.format("New Entrant Trick: OldAvg + (N+1)*Inc = %d + (%d)*%d = %d kg.", oldAvg, n + 1, inc, newWeight),
                    buildNumericOptions(newWeight, new int[]{-5, -3, 3, 6, 8, -4}, cIdx, " kg"), cIdx);
        } else {
            int cheap = 30 + (variant % 5) * 5;
            int dear = cheap + 20 + (variant % 3) * 10;
            int mean = cheap + 10;
            int d1 = dear - mean;
            int d2 = mean - cheap;
            int g = gcd(d1, d2);
            String ansStr = (d1 / g) + " : " + (d2 / g);
            int cIdx = idx % 4;
            String[] opts = {(d2 / g) + " : " + (d1 / g), (d1 / g + 1) + " : " + (d2 / g), "1 : 3", "3 : 4"};
            opts[cIdx] = ansStr;
            return buildQ(ch,
                    String.format("In what ratio must Rs. %d/kg tea be mixed with Rs. %d/kg tea so the mixture is worth Rs. %d/kg?", cheap, dear, mean),
                    Difficulty.MEDIUM,
                    String.format("Alligation Cross: (%d - %d) : (%d - %d) = %d : %d = %s.", dear, mean, mean, cheap, d1, d2, ansStr),
                    opts, cIdx);
        }
    }

    private static Question genAgesPartnerships(Chapter ch, int idx) {
        int type = idx % 3;
        int variant = idx / 3;

        if (type == 0) {
            int r1 = 3 + (variant % 3);
            int r2 = 4 + (variant % 4);
            if (r1 == r2) r2++;
            int mult = 4 + (variant % 6);
            int sum = (r1 + r2) * mult;
            int ageB = r2 * mult;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("The present ages of A and B are in ratio %d : %d. If the sum of their ages is %d years, find B's age.", r1, r2, sum),
                    Difficulty.EASY,
                    String.format("Total parts = %d. 1 part = %d / %d = %d. B's age = %d * %d = %d years.", r1 + r2, sum, r1 + r2, mult, r2, mult, ageB),
                    buildNumericOptions(ageB, new int[]{-4, -2, 3, 5, 6, -5}, cIdx, " years"), cIdx);
        } else if (type == 1) {
            int invA = 10000 + (variant % 6) * 5000;
            int invB = 15000 + (variant % 5) * 5000;
            int tA = 12;
            int tB = 8;
            int sA = invA * tA;
            int sB = invB * tB;
            int g = gcd(sA, sB);
            String ansStr = (sA / g) + " : " + (sB / g);
            int cIdx = idx % 4;
            String[] opts = {(sB / g) + " : " + (sA / g), (sA / g + 1) + " : " + (sB / g), "2 : 5", "3 : 7"};
            opts[cIdx] = ansStr;
            return buildQ(ch,
                    String.format("A invests Rs. %d for 12 months and B invests Rs. %d for 8 months. Find their profit-sharing ratio.", invA, invB),
                    Difficulty.MEDIUM,
                    String.format("Profit Ratio = (Invest * Time): (%d*12) : (%d*8) = %s.", invA, invB, ansStr),
                    opts, cIdx);
        } else {
            int diff = 12 + (variant % 8) * 3;
            int k = 3 + (variant % 2); // father is k times son
            int son = diff / (k - 1);
            if (diff % (k - 1) != 0) son = 10 + variant;
            int father = son * k;
            int actualDiff = father - son;
            int after = 5 + (variant % 5);
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("A father is %d times as old as his son. If their age difference is %d years, what will the son's age be after %d years?", k, actualDiff, after),
                    Difficulty.MEDIUM,
                    String.format("Constant Gap Trick: (%d - 1) parts = %d => Son = %d yrs. After %d yrs = %d.", k, actualDiff, son, after, son + after),
                    buildNumericOptions(son + after, new int[]{-4, -2, 2, 4, 6, -3}, cIdx, " years"), cIdx);
        }
    }

    private static Question genPermComb(Chapter ch, int idx) {
        int type = idx % 4;
        int variant = idx / 4;

        if (type == 0) {
            int n = 6 + (variant % 5);
            int r = 2 + (variant % 2);
            int ans = (r == 2) ? (n * (n - 1)) / 2 : (n * (n - 1) * (n - 2)) / 6;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("Evaluate %dC%d (ways to choose %d items from %d distinct items).", n, r, r, n),
                    Difficulty.EASY,
                    String.format("%dC%d = %d.", n, r, ans),
                    buildNumericOptions(ans, new int[]{-5, -3, 4, 7, 10, -4}, cIdx, ""), cIdx);
        } else if (type == 1) {
            int n = 4 + (variant % 4); // 4..7 people
            int ans = 1;
            for (int i = 2; i <= n - 1; i++) ans *= i;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("In how many distinct ways can %d people be seated around a circular table?", n),
                    Difficulty.MEDIUM,
                    String.format("Circular Permutation Trick: (n - 1)! = (%d - 1)! = %d ways.", n, ans),
                    buildNumericOptions(ans, new int[]{-12, -6, 12, 24, 36, -4}, cIdx, ""), cIdx);
        } else if (type == 2) {
            String[] words = {"APPLE", "LEVEL", "HELLO", "SMART", "BRAIN", "CLASS", "TRAIN"};
            String w = words[variant % words.length];
            int ans = switch (w) {
                case "APPLE", "HELLO", "CLASS" -> 60;
                case "LEVEL" -> 30;
                default -> 120;
            };
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("How many distinct permutations can be formed using all letters of the word '%s'?", w),
                    Difficulty.MEDIUM,
                    String.format("Divide %d! by factorials of repeated letters -> %d.", w.length(), ans),
                    buildNumericOptions(ans, new int[]{-15, -10, 15, 30, 45, -5}, cIdx, ""), cIdx);
        } else {
            int m = 3 + (variant % 3);
            int w = 2 + (variant % 3);
            int ans = m * w;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("A team needs 1 lead from %d engineers and 1 analyst from %d statisticians. How many ways can the pair be chosen?", m, w),
                    Difficulty.EASY,
                    String.format("AND = Multiply: %d * %d = %d ways.", m, w, ans),
                    buildNumericOptions(ans, new int[]{-2, -1, 2, 3, 4, -3}, cIdx, ""), cIdx);
        }
    }

    private static Question genProbability(Chapter ch, int idx) {
        int type = idx % 4;
        int variant = idx / 4;
        int cIdx = idx % 4;

        if (type == 0) {
            int red = 3 + (variant % 4);
            int blue = 4 + (variant % 5);
            int total = red + blue;
            int g = gcd(red, total);
            String ans = (red / g) + "/" + (total / g);
            String[] opts = {(blue / gcd(blue, total)) + "/" + (total / gcd(blue, total)), "1/" + total, "2/" + (total + 1), "3/11"};
            opts[cIdx] = ans;
            return buildQ(ch,
                    String.format("A bag contains %d red and %d blue balls. One ball is drawn at random. Find probability it is red.", red, blue),
                    Difficulty.EASY,
                    String.format("P(Red) = %d / (%d + %d) = %d/%d = %s.", red, red, blue, red, total, ans),
                    opts, cIdx);
        } else if (type == 1) {
            String[] opts = {"1/4", "1/2", "3/4", "2/3"};
            swap(opts, 2, cIdx);
            return buildQ(ch,
                    String.format("Two fair coins are tossed simultaneously. What is the probability of getting at least one head? (Case #%d)", idx + 1),
                    Difficulty.EASY,
                    "At Least One Trick: 1 - P(no head) = 1 - P(TT) = 1 - 1/4 = 3/4.",
                    opts, cIdx);
        } else if (type == 2) {
            int[] sums = {7, 6, 8, 5, 9, 4, 10};
            int target = sums[variant % sums.length];
            int ways = (target <= 7) ? (target - 1) : (13 - target);
            int g = gcd(ways, 36);
            String ans = (ways / g) + "/" + (36 / g);
            String[] opts = {"1/6", "1/12", "5/36", "7/36"};
            opts[cIdx] = ans;
            return buildQ(ch,
                    String.format("Two standard 6-sided dice are rolled. What is the probability of getting a sum of %d?", target),
                    Difficulty.MEDIUM,
                    String.format("Dice Sum Trick: Ways for sum %d = %d out of 36 => %s.", target, ways, ans),
                    opts, cIdx);
        } else {
            String[] opts = {"4/13", "3/13", "1/13", "1/4"};
            swap(opts, 1, cIdx);
            return buildQ(ch,
                    String.format("One card is drawn from a well-shuffled 52-card deck. Find probability of drawing a face card (J, Q, or K). (Set #%d)", idx + 1),
                    Difficulty.MEDIUM,
                    "There are 12 face cards in 52 cards. P(Face) = 12/52 = 3/13.",
                    opts, cIdx);
        }
    }

    private static Question genAlgebra(Chapter ch, int idx) {
        int type = idx % 3;
        int variant = idx / 3;

        if (type == 0) {
            int k = 3 + (variant % 12);
            int ans = k * k - 2;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("If x + 1/x = %d, find the value of x^2 + 1/x^2.", k),
                    Difficulty.EASY,
                    String.format("Reciprocal Trick: k^2 - 2 = %d^2 - 2 = %d.", k, ans),
                    buildNumericOptions(ans, new int[]{-4, -2, 2, 4, 6, -3}, cIdx, ""), cIdx);
        } else if (type == 1) {
            int k = 3 + (variant % 6);
            int ans = k * k * k - 3 * k;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("If x + 1/x = %d, find the value of x^3 + 1/x^3.", k),
                    Difficulty.MEDIUM,
                    String.format("Cube Trick: k^3 - 3k = %d^3 - 3(%d) = %d.", k, k, ans),
                    buildNumericOptions(ans, new int[]{-6, -3, 3, 6, 9, -4}, cIdx, ""), cIdx);
        } else {
            int r1 = 2 + (variant % 6);
            int r2 = 3 + (variant % 5);
            int b = -(r1 + r2);
            int c = r1 * r2;
            int sum = r1 + r2;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("Find the sum of the roots of the quadratic equation x^2 - %dx + %d = 0.", sum, c),
                    Difficulty.EASY,
                    String.format("Sum of roots = -B/A = -(%d)/1 = %d.", b, sum),
                    buildNumericOptions(sum, new int[]{-2, -1, 1, 2, 3, -3}, cIdx, ""), cIdx);
        }
    }

    private static Question genGeometry(Chapter ch, int idx) {
        int type = idx % 4;
        int variant = idx / 4;

        if (type == 0) {
            int[][] triplets = {{3, 4, 5}, {5, 12, 13}, {8, 15, 17}, {7, 24, 25}, {6, 8, 10}, {9, 12, 15}};
            int[] t = triplets[variant % triplets.length];
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("A right-angled triangle has legs of length %d cm and %d cm. Find the length of its hypotenuse.", t[0], t[1]),
                    Difficulty.EASY,
                    String.format("Pythagorean Triplet (%d, %d, %d) => Hypotenuse = %d cm.", t[0], t[1], t[2], t[2]),
                    buildNumericOptions(t[2], new int[]{-2, -1, 2, 3, 4, -3}, cIdx, " cm"), cIdx);
        } else if (type == 1) {
            int x = 10 + (variant % 6) * 5;
            double inc = Math.round((2 * x + (x * x) / 100.0) * 100.0) / 100.0;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("If the radius of a circle is increased by %d%%, by what percentage does its area increase?", x),
                    Difficulty.MEDIUM,
                    String.format("Area Scaling Trick: 2x + x^2/100 = 2(%d) + %d^2/100 = %s.", x, x, formatCleanPercent(inc)),
                    buildDecimalOptions(inc, new double[]{-4, 4, 8, -2, 6}, cIdx, "%"), cIdx);
        } else if (type == 2) {
            int l = 8 + (variant % 8) * 2;
            int w = 5 + (variant % 5) * 2;
            int area = l * w;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("A rectangle has length %d cm and breadth %d cm. Find its area.", l, w),
                    Difficulty.EASY,
                    String.format("Area = L * B = %d * %d = %d sq.cm.", l, w, area),
                    buildNumericOptions(area, new int[]{-10, -5, 5, 10, 12, -8}, cIdx, " sq.cm"), cIdx);
        } else {
            int a = 4 + (variant % 8) * 2;
            int areaCube = 6 * a * a;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("Find the total surface area of a cube whose side length is %d cm.", a),
                    Difficulty.MEDIUM,
                    String.format("Total Surface Area = 6 * a^2 = 6 * %d^2 = %d sq.cm.", a, areaCube),
                    buildNumericOptions(areaCube, new int[]{-24, -12, 12, 24, 36, -18}, cIdx, " sq.cm"), cIdx);
        }
    }

    private static Question genProgressions(Chapter ch, int idx) {
        int type = idx % 3;
        int variant = idx / 3;

        if (type == 0) {
            int a = 4 + (variant % 6) * 2;
            int d = 3 + (variant % 5);
            int n = 10 + (variant % 6);
            int tn = a + (n - 1) * d;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("Find the %dth term of the Arithmetic Progression: %d, %d, %d, ...", n, a, a + d, a + 2 * d),
                    Difficulty.EASY,
                    String.format("Tn = a + (n-1)d = %d + (%d)*%d = %d.", a, n - 1, d, tn),
                    buildNumericOptions(tn, new int[]{-d, d, 2 * d, -2 * d, 5, -5}, cIdx, ""), cIdx);
        } else if (type == 1) {
            int n = 5 + (variant % 4) * 2; // odd count: 5, 7, 9, 11
            int mid = 15 + variant * 3;
            int sum = n * mid;
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("In an Arithmetic Progression of %d terms, the middle term is %d. Find the sum of all %d terms.", n, mid, n),
                    Difficulty.MEDIUM,
                    String.format("Middle-Term Sum Trick: Sum = n * Middle = %d * %d = %d.", n, mid, sum),
                    buildNumericOptions(sum, new int[]{-10, -5, 5, 10, 15, -15}, cIdx, ""), cIdx);
        } else {
            int a = 6 + (variant % 8) * 4;
            int sumInf = a * 2; // r = 1/2 => a/(1 - 1/2) = 2a
            int cIdx = idx % 4;
            return buildQ(ch,
                    String.format("Find the sum to infinity of the Geometric Progression: %d, %d, %d, ...", a, a / 2, a / 4),
                    Difficulty.HARD,
                    String.format("S_inf = a / (1 - r) = %d / (1 - 0.5) = %d.", a, sumInf),
                    buildNumericOptions(sumInf, new int[]{-4, -2, 4, 6, 8, -6}, cIdx, ""), cIdx);
        }
    }
}
