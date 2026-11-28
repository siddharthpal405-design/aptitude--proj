package com.aptitude.platform.init;

import com.aptitude.platform.model.Chapter;
import com.aptitude.platform.model.Difficulty;
import com.aptitude.platform.model.Question;

import static com.aptitude.platform.init.QuestionFactoryHelper.*;

/**
 * Generates verified, trick-annotated MCQs for all 7 Verbal Ability and 4 Data Interpretation chapters.
 */
public final class VerbalDiQuestionFactory {

    private VerbalDiQuestionFactory() {}

    public static Question create(Chapter chapter, String slug, int idx) {
        return switch (slug) {
            case "spotting-errors" -> genSpottingErrors(chapter, idx);
            case "sentence-improvement" -> genSentenceImprovement(chapter, idx);
            case "synonyms-and-antonyms" -> genSynonymsAntonyms(chapter, idx);
            case "reading-comprehension" -> genReadingComp(chapter, idx);
            case "para-jumbles" -> genParaJumbles(chapter, idx);
            case "idioms-and-phrases" -> genIdioms(chapter, idx);
            case "fill-in-the-blanks" -> genFillBlanks(chapter, idx);
            case "tables-and-percentage-share" -> genTableDI(chapter, idx);
            case "bar-and-pie-charts" -> genChartDI(chapter, idx);
            case "line-graphs" -> genLineGraphDI(chapter, idx);
            default -> genCaseletDI(chapter, idx);
        };
    }

    private static Question genSpottingErrors(Chapter ch, int idx) {
        String[] subjects = {"candidates", "students", "applicants", "engineers", "managers", "teachers", "analysts", "designers", "players", "doctors"};
        String sub = subjects[idx % subjects.length];
        int type = idx % 4;
        int cIdx = idx % 4;
        String[] opts = {"Part (A)", "Part (B)", "Part (C)", "No error"};

        if (type == 0) {
            swap(opts, 1, cIdx);
            return buildQ(ch,
                    String.format("Identify the erroneous segment:\n(A) Neither of the two %s / (B) have submitted / (C) the project report / (D) before Friday. (#%d)", sub, idx + 1),
                    Difficulty.EASY,
                    "Singular Indefinite Rule: 'Neither of' always takes a singular verb ('has submitted').",
                    opts, cIdx);
        } else if (type == 1) {
            swap(opts, 1, cIdx);
            return buildQ(ch,
                    String.format("Identify the erroneous segment:\n(A) One of the senior %s / (B) were selected / (C) for the summit / (D) in Mumbai. (#%d)", sub, idx + 1),
                    Difficulty.MEDIUM,
                    "'One of the + Plural Noun' takes a singular verb -> Part (B) should be 'was selected'.",
                    opts, cIdx);
        } else if (type == 2) {
            swap(opts, 2, cIdx);
            return buildQ(ch,
                    String.format("Identify the erroneous segment:\n(A) The team of %s / (B) is working diligently / (C) since yesterday morning / (D) on the release. (#%d)", sub, idx + 1),
                    Difficulty.MEDIUM,
                    "'Since + point of time' requires Present Perfect Continuous ('has been working').",
                    opts, cIdx);
        } else {
            swap(opts, 1, cIdx);
            return buildQ(ch,
                    String.format("Identify the erroneous segment:\n(A) The lead architect, along with his %s, / (B) are attending / (C) the conference / (D) today. (#%d)", sub, idx + 1),
                    Difficulty.HARD,
                    "'Along with' Rule: Verb agrees with the FIRST subject ('The lead architect') -> 'is attending'.",
                    opts, cIdx);
        }
    }

    private static Question genSentenceImprovement(Chapter ch, int idx) {
        String[] events = {"the meeting began", "the bell rang", "the rain started", "the train departed", "the lights went out", "the alarm sounded"};
        String ev = events[idx % events.length];
        int type = idx % 3;
        int cIdx = idx % 4;

        if (type == 0) {
            String[] opts = {"when " + ev, "than " + ev, "then " + ev, "after " + ev};
            swap(opts, 1, cIdx);
            return buildQ(ch,
                    String.format("Improve the bracketed phrase:\nNo sooner had she entered the hall [when %s]. (#%d)", ev, idx + 1),
                    Difficulty.MEDIUM,
                    "Correlative Pair Trick: 'No sooner' always pairs with 'than', never 'when'.",
                    opts, cIdx);
        } else if (type == 1) {
            String[] opts = {"returned back", "returned", "came back again", "returning back"};
            swap(opts, 1, cIdx);
            return buildQ(ch,
                    String.format("Improve the bracketed phrase:\nAfter %s, the committee [returned back] to headquarters. (#%d)", ev, idx + 1),
                    Difficulty.EASY,
                    "Redundancy Cut: 'Return' already means 'go back'; drop 'back'.",
                    opts, cIdx);
        } else {
            String[] opts = {"to meet you", "to meeting you", "for meet you", "in meeting you"};
            swap(opts, 1, cIdx);
            return buildQ(ch,
                    String.format("Improve the bracketed phrase:\nBefore %s, I was looking forward [to meet you] at the seminar. (#%d)", ev, idx + 1),
                    Difficulty.HARD,
                    "Gerund Trigger: 'Look forward to' takes V-ing ('to meeting you').",
                    opts, cIdx);
        }
    }

    private static Question genSynonymsAntonyms(Chapter ch, int idx) {
        String[][] entries = {
                {"BENEVOLENT", "Synonym", "Charitable", "Hostile", "Miserly", "Cruel", "'Bene-' means good/kind -> Charitable."},
                {"BENEVOLENT", "Antonym", "Malevolent", "Generous", "Kind", "Altruistic", "'Bene-' (good) contrasts with 'Mal-' (bad) -> Malevolent."},
                {"EPHEMERAL", "Synonym", "Short-lived", "Eternal", "Permanent", "Enduring", "Ephemeral means lasting a very short time."},
                {"AMBIGUOUS", "Antonym", "Unambiguous", "Vague", "Obscure", "Unclear", "Ambi- (both ways/unclear) -> opposite is Unambiguous."},
                {"MITIGATE", "Synonym", "Alleviate", "Aggravate", "Worsen", "Intensify", "Mitigate means to make less severe -> Alleviate."},
                {"CANDID", "Antonym", "Deceptive", "Frank", "Honest", "Outspoken", "Candid means truthful/frank -> opposite is Deceptive."},
                {"PRAGMATIC", "Synonym", "Practical", "Idealistic", "Fanciful", "Unrealistic", "Pragmatic means dealing with things practically."},
                {"LUCID", "Antonym", "Obscure", "Clear", "Transparent", "Coherent", "Lucid means clear/intelligible -> opposite is Obscure."},
                {"DILIGENT", "Synonym", "Industrious", "Lazy", "Negligent", "Apathetic", "Diligent means showing steady effort -> Industrious."},
                {"VERBOSE", "Antonym", "Concise", "Wordy", "Rambling", "Long-winded", "Verbose means overly wordy -> opposite is Concise."}
        };
        String[] e = entries[idx % entries.length];
        int cIdx = idx % 4;
        String[] opts = {e[2], e[3], e[4], e[5]};
        swap(opts, 0, cIdx);
        return buildQ(ch,
                String.format("Choose the most accurate %s for the word '%s': (Set #%d)", e[1], e[0], idx + 1),
                idx % 3 == 0 ? Difficulty.EASY : Difficulty.MEDIUM,
                "Polarity & Root Trick: " + e[6],
                opts, cIdx);
    }

    private static Question genReadingComp(Chapter ch, int idx) {
        int type = idx % 3;
        int cIdx = idx % 4;
        String[] domains = {"solar microgrids", "AI diagnostic tools", "urban vertical farming", "battery recycling", "remote collaboration"};
        String dom = domains[idx % domains.length];

        if (type == 0) {
            String[] opts = {"Balanced and analytical", "Sarcastic and dismissive", "Overly emotional", "Hostile"};
            swap(opts, 0, cIdx);
            return buildQ(ch,
                    String.format("Passage Excerpt: 'While %s significantly lowers operational costs and boosts efficiency, scaling infrastructure requires careful regulatory oversight.'\nWhat is the author's tone? (#%d)", dom, idx + 1),
                    Difficulty.EASY,
                    "Tone Trick: The passage weighs both strengths ('lowers costs') and hurdles ('oversight') -> Balanced and analytical.",
                    opts, cIdx);
        } else if (type == 1) {
            String[] opts = {
                    String.format("%s offers strong benefits alongside implementation challenges.", dom),
                    String.format("%s should be banned immediately due to cost.", dom),
                    String.format("%s requires zero regulatory oversight.", dom),
                    String.format("%s has failed in every global market.", dom)
            };
            swap(opts, 0, cIdx);
            return buildQ(ch,
                    String.format("Passage Excerpt: 'Early adopters of %s reported a 30%% productivity gain, though supply-chain standardization remains incomplete.'\nWhich statement is best supported? (#%d)", dom, idx + 1),
                    Difficulty.MEDIUM,
                    "Eliminate Extreme Words ('banned', 'zero', 'every') -> Option summarizing gains + remaining hurdle is supported.",
                    opts, cIdx);
        } else {
            String[] opts = {"To highlight both progress and remaining hurdles", "To criticize engineers personally", "To advertise a single brand", "To narrate a fictional story"};
            swap(opts, 0, cIdx);
            return buildQ(ch,
                    String.format("Passage Excerpt: 'Recent advances in %s demonstrate measurable value, yet widespread adoption depends on lowering upfront capital expenses.'\nWhat is the primary purpose of this excerpt? (#%d)", dom, idx + 1),
                    Difficulty.MEDIUM,
                    "Purpose matches the balanced contrast ('measurable value, yet...').",
                    opts, cIdx);
        }
    }

    private static Question genParaJumbles(Chapter ch, int idx) {
        String[] names = {"Aarav", "Meera", "Kiran", "Nisha", "Rohan", "Divya"};
        String n = names[idx % names.length];
        int cIdx = idx % 4;
        String[] opts = {"BCAD", "CABD", "ABCD", "DACB"};
        swap(opts, 0, cIdx);
        return buildQ(ch,
                String.format("Arrange the sentences P-S into a coherent paragraph:\nA: Consequently, she/he launched a pilot project that grew rapidly.\nB: %s noticed that rural clinics lacked timely diagnostic reports.\nC: To solve this, %s designed a lightweight offline mobile tool.\nD: Today, the platform serves over 200 community clinics. (#%d)", n, n, idx + 1),
                Difficulty.MEDIUM,
                String.format("Opener + Mandatory Pair Trick: B introduces %s & problem -> C ('To solve this') -> A ('Consequently') -> D ('Today') => BCAD.", n),
                opts, cIdx);
    }

    private static Question genIdioms(Chapter ch, int idx) {
        String[][] items = {
                {"Idiom: 'To bite the bullet'", "Endure a difficult situation with courage", "Avoid responsibility completely", "Shoot randomly without aiming", "Speak softly in a meeting"},
                {"Idiom: 'Once in a blue moon'", "Very rarely", "Every single night", "During heavy rainfall", "Without any warning"},
                {"Idiom: 'Break the ice'", "Initiate conversation in a tense or awkward group", "Destroy frozen food", "Cancel a scheduled meeting", "Cause an argument"},
                {"One-word: 'A person who loves books'", "Bibliophile", "Misanthrope", "Agnostic", "Polyglot"},
                {"One-word: 'Government run by the wealthy'", "Plutocracy", "Democracy", "Autocracy", "Theocracy"},
                {"One-word: 'Fear of confined spaces'", "Claustrophobia", "Hydrophobia", "Acrophobia", "Xenophobia"},
                {"Idiom: 'Hit the nail on the head'", "State the exact right answer or point", "Injure oneself while working", "Delay a project intentionally", "Leave work early"},
                {"Idiom: 'Burn the midnight oil'", "Work or study late into the night", "Waste fuel carelessly", "Start a fire outdoors", "Sleep before sunset"}
        };
        String[] it = items[idx % items.length];
        int cIdx = idx % 4;
        String[] opts = {it[1], it[2], it[3], it[4]};
        swap(opts, 0, cIdx);
        return buildQ(ch,
                String.format("Select the exact meaning of: %s (#%d)", it[0], idx + 1),
                idx % 2 == 0 ? Difficulty.EASY : Difficulty.MEDIUM,
                "Figurative / Root Trick: Avoid literal translations; match root meaning -> " + it[1],
                opts, cIdx);
    }

    private static Question genFillBlanks(Chapter ch, int idx) {
        String[][] entries = {
                {"Although the launch faced early delays, the engineering team remained _____ about meeting Q4 targets.", "optimistic", "pessimistic", "indifferent", "hesitant", "'Although' signals contrast with 'delays' -> optimistic."},
                {"Because the manual was written with remarkable _____, even beginners configured the server effortlessly.", "clarity", "ambiguity", "complexity", "confusion", "'Effortlessly' matches positive cause -> clarity."},
                {"The manager decided to abstain _____ commenting until the financial audit concluded.", "from", "with", "over", "into", "Preposition Lock: 'abstain' always takes 'from'."},
                {"Despite intense competition, the startup's revenue grew _____ throughout the fiscal year.", "steadily", "barely", "erratically", "scarcely", "'Despite competition' contrasts with positive growth -> steadily."},
                {"Senior analysts prefer structured spreadsheets _____ unformatted text logs.", "to", "than", "over", "against", "Preposition Lock: 'prefer' always pairs with 'to' (not 'than')."}
        };
        String[] e = entries[idx % entries.length];
        int cIdx = idx % 4;
        String[] opts = {e[1], e[2], e[3], e[4]};
        swap(opts, 0, cIdx);
        return buildQ(ch,
                String.format("Fill in the blank with the most appropriate word:\n%s (#%d)", e[0], idx + 1),
                Difficulty.EASY,
                "Context / Preposition Lock: " + e[5],
                opts, cIdx);
    }

    private static Question genTableDI(Chapter ch, int idx) {
        int q1 = 200 + (idx % 10) * 20;
        int growthPct = (idx % 4 == 0) ? 20 : (idx % 4 == 1 ? 25 : (idx % 4 == 2 ? 15 : 30));
        int q2 = q1 + (q1 * growthPct) / 100;
        int q3 = q2 + 40;
        int q4 = q3 + 20;
        int total = q1 + q2 + q3 + q4;
        int avg = total / 4;
        int cIdx = idx % 4;

        if (idx % 2 == 0) {
            return buildQ(ch,
                    String.format("Quarterly Revenue Table (Rs. Lakhs): Q1=%d, Q2=%d, Q3=%d, Q4=%d.\nFind %% growth from Q1 to Q2.", q1, q2, q3, q4),
                    Difficulty.EASY,
                    String.format("((%d - %d) / %d) * 100 = %d%%.", q2, q1, q1, growthPct),
                    buildNumericOptions(growthPct, new int[]{-5, -2, 5, 10, 12, -4}, cIdx, "%"), cIdx);
        } else {
            return buildQ(ch,
                    String.format("Quarterly Output Table (Lakhs): Q1=%d, Q2=%d, Q3=%d, Q4=%d.\nFind average quarterly output.", q1, q2, q3, q4),
                    Difficulty.MEDIUM,
                    String.format("Total = %d. Average = %d / 4 = %d Lakhs.", total, total, avg),
                    buildNumericOptions(avg, new int[]{-15, -5, 10, 20, 25, -20}, cIdx, " Lakhs"), cIdx);
        }
    }

    private static Question genChartDI(Chapter ch, int idx) {
        int[] pcts = {10, 15, 20, 25, 30, 35, 40, 45};
        int p = pcts[idx % pcts.length];
        int totalBudget = 100000 + (idx / pcts.length) * 20000;
        int cIdx = idx % 4;

        if (idx % 2 == 0) {
            int angle = (int) Math.round(p * 3.6);
            return buildQ(ch,
                    String.format("In a budget Pie Chart, Infrastructure is %d%% of Rs. %d. Find its central angle.", p, totalBudget),
                    Difficulty.EASY,
                    String.format("1%% = 3.6 deg Trick: %d * 3.6 = %d deg.", p, angle),
                    buildNumericOptions(angle, new int[]{-18, -9, 9, 18, 27, -27}, cIdx, "°"), cIdx);
        } else {
            int val = (totalBudget * p) / 100;
            return buildQ(ch,
                    String.format("In a Pie Chart of total budget Rs. %d, R&D allocation is %d%%. Find R&D spend.", totalBudget, p),
                    Difficulty.MEDIUM,
                    String.format("%d%% of %d = Rs. %d.", p, totalBudget, val),
                    buildNumericOptions(val, new int[]{-4000, -2000, 2000, 4000, 5000, -5000}, cIdx, "Rs. "), cIdx);
        }
    }

    private static Question genLineGraphDI(Chapter ch, int idx) {
        int y1 = 100 + (idx % 8) * 25;
        int pct = (idx % 3 == 0) ? 20 : (idx % 3 == 1 ? 40 : 50);
        int y2 = y1 + (y1 * pct) / 100;
        int y3 = y2 + 30;
        int y4 = y3 + 10;
        int cIdx = idx % 4;

        if (idx % 2 == 0) {
            return buildQ(ch,
                    String.format("Line Graph (Exports in Tons): 2021=%d, 2022=%d, 2023=%d, 2024=%d.\nWhat is the percentage increase from 2021 to 2022?", y1, y2, y3, y4),
                    Difficulty.EASY,
                    String.format("((%d - %d) / %d) * 100 = %d%%.", y2, y1, y1, pct),
                    buildNumericOptions(pct, new int[]{-10, -5, 5, 10, 15, -8}, cIdx, "%"), cIdx);
        } else {
            int netChange = y4 - y1;
            return buildQ(ch,
                    String.format("Line Graph (Sales Units): Jan=%d, Feb=%d, Mar=%d, Apr=%d.\nFind the net absolute increase from Jan to Apr.", y1, y2, y3, y4),
                    Difficulty.MEDIUM,
                    String.format("Net Change = Apr - Jan = %d - %d = %d units.", y4, y1, netChange),
                    buildNumericOptions(netChange, new int[]{-20, -10, 10, 20, 30, -15}, cIdx, " units"), cIdx);
        }
    }

    private static Question genCaseletDI(Chapter ch, int idx) {
        int total = 100 + (idx % 6) * 20;
        int setA = (int) (total * 0.6);
        int setB = (int) (total * 0.5);
        int both = (int) (total * 0.25);
        int neither = total - (setA + setB - both);
        int onlyA = setA - both;
        int cIdx = idx % 4;

        if (idx % 2 == 0) {
            return buildQ(ch,
                    String.format("Caselet: Out of %d employees, %d use Python, %d use Java, and %d use both. How many use neither language?", total, setA, setB, both),
                    Difficulty.MEDIUM,
                    String.format("Venn Grid Trick: At least one = %d + %d - %d = %d. Neither = %d - %d = %d.", setA, setB, both, setA + setB - both, total, setA + setB - both, neither),
                    buildNumericOptions(neither, new int[]{-5, -3, 4, 6, 8, -4}, cIdx, ""), cIdx);
        } else {
            return buildQ(ch,
                    String.format("Caselet: Among %d students, %d play Cricket, %d play Football, and %d play both. How many play ONLY Cricket?", total, setA, setB, both),
                    Difficulty.EASY,
                    String.format("Only Cricket = n(Cricket) - n(Both) = %d - %d = %d.", setA, both, onlyA),
                    buildNumericOptions(onlyA, new int[]{-5, -3, 3, 5, 8, -4}, cIdx, ""), cIdx);
        }
    }
}
