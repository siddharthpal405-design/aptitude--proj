package com.aptitude.platform.init;

import com.aptitude.platform.model.Chapter;
import com.aptitude.platform.model.Difficulty;
import com.aptitude.platform.model.Question;

import static com.aptitude.platform.init.QuestionFactoryHelper.*;

/**
 * Generates logically verified, trick-annotated MCQs for all 9 Logical Reasoning chapters.
 */
public final class ReasoningQuestionFactory {

    private ReasoningQuestionFactory() {}

    public static Question create(Chapter chapter, String slug, int idx) {
        return switch (slug) {
            case "blood-relations" -> genBloodRelations(chapter, idx);
            case "syllogisms" -> genSyllogisms(chapter, idx);
            case "coding-decoding" -> genCodingDecoding(chapter, idx);
            case "number-and-letter-series" -> genSeries(chapter, idx);
            case "direction-sense" -> genDirectionSense(chapter, idx);
            case "seating-arrangement" -> genSeating(chapter, idx);
            case "clocks-and-calendars" -> genClocksCalendars(chapter, idx);
            case "order-and-ranking" -> genOrderRanking(chapter, idx);
            default -> genDataSufficiency(chapter, idx);
        };
    }

    private static Question genBloodRelations(Chapter ch, int idx) {
        String[] men = {"Rohan", "Vikram", "Arjun", "Kabir", "Suresh", "Anil", "Deepak", "Nikhil"};
        String[] women = {"Priya", "Meera", "Ananya", "Kavya", "Sonal", "Neha", "Ritu", "Pooja"};
        String m = men[idx % men.length];
        String w = women[(idx / men.length) % women.length];
        int type = idx % 4;
        int cIdx = idx % 4;

        if (type == 0) {
            String[] opts = {"Brother", "Father", "Uncle", "Grandfather"};
            swap(opts, 1, cIdx);
            return buildQ(ch,
                    String.format("Pointing to a boy, %s said, 'He is the son of the only son of my mother.' How is %s related to the boy? (Case #%d)", m, m, idx + 1),
                    Difficulty.EASY,
                    String.format("Reverse Parse Trick: 'Only son of my mother' = %s. The boy is %s's son -> %s is Father.", m, m, m),
                    opts, cIdx);
        } else if (type == 1) {
            String[] opts = {"Sister", "Aunt", "Mother", "Niece"};
            swap(opts, 1, cIdx);
            return buildQ(ch,
                    String.format("%s is the sister of %s. %s is the father of Rahul. How is %s related to Rahul? (Set #%d)", w, m, m, w, idx + 1),
                    Difficulty.MEDIUM,
                    String.format("%s is Rahul's father's sister -> Paternal Aunt.", w),
                    opts, cIdx);
        } else if (type == 2) {
            String[] opts = {"Husband", "Brother", "Brother-in-law", "Father-in-law"};
            swap(opts, 2, cIdx);
            return buildQ(ch,
                    String.format("%s is married to %s. Karan is the only brother of %s. How is Karan related to %s? (Case #%d)", w, m, w, m, idx + 1),
                    Difficulty.MEDIUM,
                    String.format("Karan is the brother of %s's wife (%s) -> Brother-in-law.", m, w),
                    opts, cIdx);
        } else {
            String[] opts = {"Grandson", "Nephew", "Son", "Cousin"};
            swap(opts, 0, cIdx);
            return buildQ(ch,
                    String.format("X is Y's father, and Y is Z's father. If Z is male, how is Z related to X? (Case #%d)", idx + 1),
                    Difficulty.HARD,
                    "Two generations down from X -> Z is X's Grandson.",
                    opts, cIdx);
        }
    }

    private static Question genSyllogisms(Chapter ch, int idx) {
        String[][] sets = {
                {"cats", "dogs", "birds"}, {"pens", "books", "papers"}, {"cars", "buses", "trucks"},
                {"apples", "mangoes", "fruits"}, {"squares", "rectangles", "polygons"},
                {"roses", "flowers", "plants"}, {"laptops", "computers", "machines"}, {"rivers", "streams", "oceans"}
        };
        String[] s = sets[idx % sets.length];
        int type = idx % 3;
        int cIdx = idx % 4;
        String[] opts = {"Only conclusion I follows", "Only conclusion II follows", "Both I and II follow", "Neither I nor II follows"};

        if (type == 0) {
            swap(opts, 0, cIdx);
            return buildQ(ch,
                    String.format("Statements: All %s are %s. All %s are %s.\nConclusions: I. All %s are %s. II. All %s are %s. (#%d)",
                            s[0], s[1], s[1], s[2], s[0], s[2], s[2], s[0], idx + 1),
                    Difficulty.EASY,
                    String.format("Concentric Venn circles: %s inside %s inside %s => Only I follows.", s[0], s[1], s[2]),
                    opts, cIdx);
        } else if (type == 1) {
            swap(opts, 2, cIdx);
            return buildQ(ch,
                    String.format("Statements: All %s are %s. No %s is a %s.\nConclusions: I. No %s is a %s. II. Some %s are %s. (#%d)",
                            s[0], s[1], s[1], s[2], s[0], s[2], s[1], s[0], idx + 1),
                    Difficulty.MEDIUM,
                    String.format("%s is inside %s and disjoint from %s => Both I and II follow.", s[0], s[1], s[2]),
                    opts, cIdx);
        } else {
            swap(opts, 3, cIdx);
            return buildQ(ch,
                    String.format("Statements: Some %s are %s. Some %s are %s.\nConclusions: I. All %s are %s. II. No %s is a %s. (#%d)",
                            s[0], s[1], s[1], s[2], s[0], s[2], s[0], s[2], idx + 1),
                    Difficulty.HARD,
                    "Two 'Some' premises yield no definite universal link between outer terms => Neither follows.",
                    opts, cIdx);
        }
    }

    private static Question genCodingDecoding(Chapter ch, int idx) {
        String[] words = {"APPLE", "BRAIN", "CLOUD", "DELTA", "EAGLE", "FLAME", "GRAPE", "HOUSE", "INDEX", "JOKER",
                "KNIFE", "LIGHT", "MAGIC", "NOBLE", "OCEAN", "PEARL", "QUEST", "RIVER", "SMART", "TRAIN",
                "UNITY", "VALUE", "WATER", "YOUTH", "ZEBRA"};
        String word = words[idx % words.length];
        int shift = 1 + (idx / words.length) % 3;
        String encoded = shiftWord(word, shift);
        int cIdx = idx % 4;
        String[] opts = {
                shiftWord(word, shift + 1),
                shiftWord(word, shift + 2),
                shiftWord(word, shift == 1 ? 4 : shift - 1),
                reverseStr(encoded)
        };
        opts[cIdx] = encoded;

        return buildQ(ch,
                String.format("If every letter is shifted forward by +%d in the alphabet (wrapping after Z), how is '%s' coded?", shift, word),
                shift == 1 ? Difficulty.EASY : Difficulty.MEDIUM,
                String.format("Shift each letter of %s by +%d => %s.", word, shift, encoded),
                opts, cIdx);
    }

    private static Question genSeries(Chapter ch, int idx) {
        int type = idx % 4;
        int variant = idx / 4;
        int cIdx = idx % 4;

        if (type == 0) {
            int a = 2 + (variant % 8);
            int t1 = a, t2 = t1 * 2 + 1, t3 = t2 * 2 + 1, t4 = t3 * 2 + 1, t5 = t4 * 2 + 1;
            return buildQ(ch,
                    String.format("Find the missing term in the series: %d, %d, %d, %d, ?", t1, t2, t3, t4),
                    Difficulty.EASY,
                    String.format("Multiply & Offset Trick: Each term = previous*2 + 1. %d*2 + 1 = %d.", t4, t5),
                    buildNumericOptions(t5, new int[]{-4, -2, 2, 4, 6, -3}, cIdx, ""), cIdx);
        } else if (type == 1) {
            int start = 5 + (variant % 10);
            int d1 = 3 + (variant % 4);
            int s1 = start, s2 = s1 + d1, s3 = s2 + d1 + 2, s4 = s3 + d1 + 4, s5 = s4 + d1 + 6;
            return buildQ(ch,
                    String.format("Find the next number in the sequence: %d, %d, %d, %d, ?", s1, s2, s3, s4),
                    Difficulty.MEDIUM,
                    String.format("2nd-Order Difference Trick: Differences increase by +2 each step (+%d, +%d, +%d, +%d) => %d.", d1, d1 + 2, d1 + 4, d1 + 6, s5),
                    buildNumericOptions(s5, new int[]{-3, -2, 2, 4, 5, -4}, cIdx, ""), cIdx);
        } else if (type == 2) {
            int n = 2 + (variant % 6);
            int s1 = n * n + 1, s2 = (n + 1) * (n + 1) + 1, s3 = (n + 2) * (n + 2) + 1, s4 = (n + 3) * (n + 3) + 1;
            int s5 = (n + 4) * (n + 4) + 1;
            return buildQ(ch,
                    String.format("Complete the number series: %d, %d, %d, %d, ?", s1, s2, s3, s4),
                    Difficulty.MEDIUM,
                    String.format("Square Anchor Trick: Pattern is k^2 + 1 for k=%d,%d,%d,%d,%d => %d^2 + 1 = %d.", n, n + 1, n + 2, n + 3, n + 4, n + 4, s5),
                    buildNumericOptions(s5, new int[]{-4, -2, 2, 4, 6, -5}, cIdx, ""), cIdx);
        } else {
            String[] words = {"ACE", "BDF", "CEG", "DFH", "EGI", "FHJ", "GIK", "HJL"};
            String w = words[variant % words.length];
            String next = shiftWord(w, 2);
            String[] opts = {shiftWord(w, 1), shiftWord(w, 3), shiftWord(w, 4), reverseStr(next)};
            opts[cIdx] = next;
            return buildQ(ch,
                    String.format("Find the next letter cluster in the series: %s, %s, ?", w, shiftWord(w, 1)),
                    Difficulty.EASY,
                    String.format("Each letter shifts +1 per step => after %s comes %s.", shiftWord(w, 1), next),
                    opts, cIdx);
        }
    }

    private static Question genDirectionSense(Chapter ch, int idx) {
        int[][] triplets = {{3, 4, 5}, {6, 8, 10}, {5, 12, 13}, {8, 15, 17}, {9, 12, 15}};
        int type = idx % 3;
        int variant = idx / 3;
        int cIdx = idx % 4;

        if (type == 0) {
            int[] t = triplets[variant % triplets.length];
            return buildQ(ch,
                    String.format("A person walks %d km North, then turns right and walks %d km East. What is the shortest distance from the starting point?", t[0], t[1]),
                    Difficulty.EASY,
                    String.format("NESW + Pythagoras: sqrt(%d^2 + %d^2) = %d km.", t[0], t[1], t[2]),
                    buildNumericOptions(t[2], new int[]{-2, -1, 2, 3, 4, -3}, cIdx, " km"), cIdx);
        } else if (type == 1) {
            int n = 10 + (variant % 8) * 2;
            int e = 6 + (variant % 5) * 2;
            int s = 4 + (variant % 4);
            int w = e; // East and West cancel!
            int net = n - s;
            return buildQ(ch,
                    String.format("Rohan walks %d m North, %d m East, %d m South, and %d m West. How far is he from the start?", n, e, s, w),
                    Difficulty.MEDIUM,
                    String.format("NESW Cancellation Trick: East (%d) cancels West (%d). Net North = %d - %d = %d m.", e, w, n, s, net),
                    buildNumericOptions(net, new int[]{-4, -2, 2, 4, 6, -3}, cIdx, " m"), cIdx);
        } else {
            String[] opts = {"North", "South", "East", "West"};
            boolean morning = (variant % 2 == 0);
            swap(opts, morning ? 3 : 2, cIdx);
            return buildQ(ch,
                    String.format("During %s, a flagpole stands vertically on an open ground. Towards which direction does its shadow fall? (Case #%d)",
                            morning ? "early morning (sunrise)" : "late evening (sunset)", idx + 1),
                    Difficulty.EASY,
                    morning ? "Morning Sun is in the East, so all shadows fall West." : "Evening Sun is in the West, so all shadows fall East.",
                    opts, cIdx);
        }
    }

    private static Question genSeating(Chapter ch, int idx) {
        int type = idx % 3;
        int variant = idx / 3;
        int cIdx = idx % 4;

        if (type == 0) {
            String[] names = {"Arun", "Bala", "Chetan", "Deepak", "Eshan", "Farhan"};
            String p1 = names[variant % 3];
            String p2 = names[(variant + 1) % 3 + 3];
            String[] opts = {"Clockwise", "Anti-clockwise", "Opposite", "Cannot be determined"};
            swap(opts, 0, cIdx);
            return buildQ(ch,
                    String.format("Six friends sit around a circular table facing the center. If %s sits to the immediate left of %s, in which direction from %s is %s seated? (#%d)", p1, p2, p2, p1, idx + 1),
                    Difficulty.EASY,
                    "Facing Center Rule: Immediate Left is always Clockwise; Immediate Right is Anti-clockwise.",
                    opts, cIdx);
        } else if (type == 1) {
            int n = 6 + (variant % 3) * 2; // 6, 8, 10
            int oppositeOffset = n / 2;
            String[] opts = {"Position " + (1 + oppositeOffset), "Position " + oppositeOffset, "Position " + (2 + oppositeOffset), "Position 2"};
            String ans = "Position " + (1 + oppositeOffset);
            opts[cIdx] = ans;
            return buildQ(ch,
                    String.format("%d students sit equally spaced around a circle numbered 1 to %d clockwise. Who sits directly opposite to Position 1?", n, n),
                    Difficulty.MEDIUM,
                    String.format("Circle Opposite Shortcut: Add N/2 (%d/2 = %d) => 1 + %d = Position %d.", n, oppositeOffset, oppositeOffset, 1 + oppositeOffset),
                    opts, cIdx);
        } else {
            String[] opts = {"First person", "Second person", "Third person", "Last person"};
            swap(opts, 1, cIdx);
            return buildQ(ch,
                    String.format("In a seating puzzle statement: 'A sits second to the right of B, who is facing North.' Whom does the word 'who' refer to? (Set #%d)", idx + 1),
                    Difficulty.EASY,
                    "Grammar Anchor Trick: 'Who/Which' always refers to the immediately preceding person (B), whereas 'and' refers to the first subject (A).",
                    opts, cIdx);
        }
    }

    private static Question genClocksCalendars(Chapter ch, int idx) {
        int type = idx % 4;
        int variant = idx / 4;
        int cIdx = idx % 4;

        if (type == 0) {
            int[] hours = {2, 3, 4, 5, 6, 7, 8, 9, 10};
            int[] mins = {20, 30, 40, 10, 50};
            int h = hours[variant % hours.length];
            int m = mins[variant % mins.length];
            double angle = Math.abs(30.0 * h - 5.5 * m);
            if (angle > 180) angle = 360 - angle;
            return buildQ(ch,
                    String.format("Find the smaller angle between the hour hand and minute hand of a clock at %d:%02d.", h, m),
                    Difficulty.MEDIUM,
                    String.format("Clock Formula: |30(%d) - 5.5(%d)| = %s degrees.", h, m, formatCleanNumber(angle)),
                    buildDecimalOptions(angle, new double[]{-10, 10, 15, -15, 20}, cIdx, "°"), cIdx);
        } else if (type == 1) {
            int[] leapYears = {2016, 2020, 2024, 2012, 2008};
            int yr = leapYears[variant % leapYears.length];
            int nextYr = yr + 28;
            return buildQ(ch,
                    String.format("Which year after %d will have the exact same calendar as %d?", yr, yr),
                    Difficulty.MEDIUM,
                    String.format("Calendar Repeat Trick: A leap year repeats its entire calendar after +28 years => %d + 28 = %d.", yr, nextYr),
                    buildNumericOptions(nextYr, new int[]{-22, -17, 6, 11, 12, -6}, cIdx, ""), cIdx);
        } else if (type == 2) {
            String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
            int startIdx = variant % 7;
            int addDays = 15 + variant * 4;
            String ansDay = days[(startIdx + addDays) % 7];
            String[] opts = {days[(startIdx + addDays + 1) % 7], days[(startIdx + addDays + 2) % 7], days[(startIdx + addDays + 3) % 7], days[(startIdx + addDays + 4) % 7]};
            opts[cIdx] = ansDay;
            return buildQ(ch,
                    String.format("If today is %s, what day of the week will it be after %d days?", days[startIdx], addDays),
                    Difficulty.EASY,
                    String.format("Odd Days Trick: %d mod 7 = %d odd days. %s + %d = %s.", addDays, addDays % 7, days[startIdx], addDays % 7, ansDay),
                    opts, cIdx);
        } else {
            int[] centuries = {100, 200, 300, 400};
            int[] odds = {5, 3, 1, 0};
            int pick = variant % 4;
            String ans = odds[pick] + " odd days";
            String[] opts = {"5 odd days", "3 odd days", "1 odd days", "0 odd days"};
            swap(opts, pick, cIdx);
            return buildQ(ch,
                    String.format("How many odd days are contained in a period of %d years? (Case #%d)", centuries[pick], idx + 1),
                    Difficulty.HARD,
                    String.format("Standard Odd Days Cycle: 100y=5, 200y=3, 300y=1, 400y=0 => %s.", ans),
                    opts, cIdx);
        }
    }

    private static Question genOrderRanking(Chapter ch, int idx) {
        int type = idx % 3;
        int variant = idx / 3;
        int cIdx = idx % 4;

        if (type == 0) {
            int left = 8 + (variant % 15);
            int right = 10 + (variant % 18);
            int total = left + right - 1;
            return buildQ(ch,
                    String.format("In a row of students, Aman is %dth from the left and %dth from the right. How many students are in the row?", left, right),
                    Difficulty.EASY,
                    String.format("Total = Left + Right - 1 = %d + %d - 1 = %d.", left, right, total),
                    buildNumericOptions(total, new int[]{-2, -1, 1, 2, 3, -3}, cIdx, ""), cIdx);
        } else if (type == 1) {
            int total = 35 + (variant % 20);
            int fromTop = 7 + (variant % 15);
            int fromBottom = total - fromTop + 1;
            return buildQ(ch,
                    String.format("In a class of %d students, Priya ranks %dth from the top. What is her rank from the bottom?", total, fromTop),
                    Difficulty.EASY,
                    String.format("Other End = Total - Given + 1 = %d - %d + 1 = %d.", total, fromTop, fromBottom),
                    buildNumericOptions(fromBottom, new int[]{-2, -1, 1, 2, 3, -3}, cIdx, ""), cIdx);
        } else {
            int total = 40 + (variant % 10);
            int posA = 12 + (variant % 6);
            int newPosA = 20 + (variant % 6);
            int posB = total - newPosA + 1 + 3; // arbitrary consistent
            int between = Math.abs(newPosA - posA) - 1;
            return buildQ(ch,
                    String.format("In a queue, A is %dth from the front. After shifting right by %d places to become %dth from the front, how many people sit strictly between A's old and new positions?", posA, newPosA - posA, newPosA),
                    Difficulty.MEDIUM,
                    String.format("Between Count = |%d - %d| - 1 = %d.", newPosA, posA, between),
                    buildNumericOptions(between, new int[]{-2, -1, 1, 2, 3, -3}, cIdx, ""), cIdx);
        }
    }

    private static Question genDataSufficiency(Chapter ch, int idx) {
        int type = idx % 3;
        int variant = idx / 3;
        int cIdx = idx % 4;
        String[] opts = {
                "Statement I alone is sufficient",
                "Statement II alone is sufficient",
                "Both statements I and II together are needed",
                "Neither statement I nor II is sufficient"
        };

        if (type == 0) {
            int val = (4 + (variant % 6));
            int sq = val * val;
            swap(opts, 2, cIdx);
            return buildQ(ch,
                    String.format("Question: What is the exact value of x?\nStatement I: x^2 = %d.\nStatement II: x > 0. (#%d)", sq, idx + 1),
                    Difficulty.MEDIUM,
                    String.format("Statement I gives +%d or -%d (not unique). Combining with Statement II (x > 0) gives x = +%d uniquely => Both needed.", val, val, val),
                    opts, cIdx);
        } else if (type == 1) {
            int ageSum = 30 + (variant % 10) * 2;
            swap(opts, 0, cIdx);
            return buildQ(ch,
                    String.format("Question: Is A older than B?\nStatement I: A is 4 years older than B.\nStatement II: The sum of A and B's ages is %d. (#%d)", ageSum, idx + 1),
                    Difficulty.EASY,
                    "Statement I directly states A > B, answering Yes uniquely. Statement II gives only sum => Statement I alone is sufficient.",
                    opts, cIdx);
        } else {
            swap(opts, 3, cIdx);
            return buildQ(ch,
                    String.format("Question: What is the area of rectangle ABCD?\nStatement I: Length is greater than breadth.\nStatement II: Opposite sides are parallel and equal. (#%d)", idx + 1),
                    Difficulty.EASY,
                    "Neither statement provides any numerical length or breadth => Neither is sufficient.",
                    opts, cIdx);
        }
    }
}
