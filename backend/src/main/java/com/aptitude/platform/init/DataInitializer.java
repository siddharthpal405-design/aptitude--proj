package com.aptitude.platform.init;

import com.aptitude.platform.model.*;
import com.aptitude.platform.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final int MIN_QUESTIONS_PER_CHAPTER = 100;

    private final UserRepository userRepository;
    private final UserStreakRepository userStreakRepository;
    private final CategoryRepository categoryRepository;
    private final ChapterRepository chapterRepository;
    private final QuestionRepository questionRepository;
    private final MockTestRepository mockTestRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            UserStreakRepository userStreakRepository,
            CategoryRepository categoryRepository,
            ChapterRepository chapterRepository,
            QuestionRepository questionRepository,
            MockTestRepository mockTestRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userStreakRepository = userStreakRepository;
        this.categoryRepository = categoryRepository;
        this.chapterRepository = chapterRepository;
        this.questionRepository = questionRepository;
        this.mockTestRepository = mockTestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println(">>> Syncing Full 34-Chapter Aptitude Syllabus, Short Notes & 100+ Question Banks...");

        // 1. Ensure default users exist
        if (userRepository.count() == 0) {
            User admin = new User("admin@aptitude.com", passwordEncoder.encode("Admin@123"), "System Admin", Role.ROLE_ADMIN);
            userRepository.save(admin);
            userStreakRepository.save(new UserStreak(admin));

            User student = new User("student@test.com", passwordEncoder.encode("Student@123"), "Aarav Sharma", Role.ROLE_STUDENT);
            userRepository.save(student);
            userStreakRepository.save(new UserStreak(student));
        }

        // 2. Ensure all 4 Subjects & all 34 Chapters exist
        Category quant = ensureCategory(
                "Quantitative Aptitude",
                "quantitative-aptitude",
                "Numerical ability, arithmetic ratios, percentages, algebra, geometry, and probability.",
                "Calculator"
        );
        ensureChapter(quant, "Percentages", "percentages", "Percentage conversions, successive changes, and expenditure balances.", 1);
        ensureChapter(quant, "Profit & Loss", "profit-and-loss", "Cost price, selling price, discounts, and dishonest dealer equations.", 2);
        ensureChapter(quant, "Time & Work", "time-and-work", "Work rates, unitary method, pipes and cisterns, and efficiency ratios.", 3);
        ensureChapter(quant, "Number Systems", "number-systems", "Divisibility rules, unit digits, HCF/LCM, and trailing zeroes.", 4);
        ensureChapter(quant, "Ratio & Proportion", "ratio-and-proportion", "Proportions, mixtures, partnerships, and comparative ratios.", 5);
        ensureChapter(quant, "Simple & Compound Interest", "simple-and-compound-interest", "SI/CI calculations, 2-year difference formula, effective rates, and Rule of 72.", 6);
        ensureChapter(quant, "Time, Speed & Distance", "time-speed-and-distance", "Average speeds, unit conversions, train crossings, and boats & streams.", 7);
        ensureChapter(quant, "Averages, Mixtures & Alligations", "averages-and-mixtures", "Deviation averages, replacement formulas, and alligation cross rules.", 8);
        ensureChapter(quant, "Ages & Partnerships", "ages-and-partnerships", "Constant age difference shortcuts and capital-time profit ratios.", 9);
        ensureChapter(quant, "Permutation & Combination", "permutation-and-combination", "Arrangements, selections (nCr), circular seating, and repeated letters.", 10);
        ensureChapter(quant, "Probability", "probability", "Coin tosses, two-dice sums, playing cards, and complement probability.", 11);
        ensureChapter(quant, "Algebra & Equations", "algebra-and-equations", "Quadratic roots, reciprocal identities, and linear system shortcuts.", 12);
        ensureChapter(quant, "Geometry & Mensuration", "geometry-and-mensuration", "Pythagorean triplets, dimension scaling, 2D areas, and 3D volumes.", 13);
        ensureChapter(quant, "Progressions (AP, GP & HP)", "progressions-and-series", "Arithmetic & geometric sequences, middle-term sums, and infinite series.", 14);

        Category reasoning = ensureCategory(
                "Logical Reasoning",
                "logical-reasoning",
                "Analytical deduction, puzzles, series, spatial sense, clocks, and data sufficiency.",
                "Cpu"
        );
        ensureChapter(reasoning, "Blood Relations", "blood-relations", "Family tree mapping, coded relations, and generational puzzles.", 1);
        ensureChapter(reasoning, "Syllogisms", "syllogisms", "Venn diagram deductions, universal/particular premise conclusions.", 2);
        ensureChapter(reasoning, "Coding-Decoding", "coding-decoding", "Alphabetical shifts, mirror reversals, and substitution ciphers.", 3);
        ensureChapter(reasoning, "Number & Letter Series", "number-and-letter-series", "Difference ladders, square/cube anchors, and letter progressions.", 4);
        ensureChapter(reasoning, "Direction Sense Test", "direction-sense", "NESW cancellation, shortest Euclidean displacement, and shadow rules.", 5);
        ensureChapter(reasoning, "Seating Arrangement", "seating-arrangement", "Linear and circular seating constraints, facing center/outward rules.", 6);
        ensureChapter(reasoning, "Clocks & Calendars", "clocks-and-calendars", "Hand angle formula, odd-day cycles, leap years, and calendar repeats.", 7);
        ensureChapter(reasoning, "Order, Ranking & Comparison", "order-and-ranking", "Row totals, positional ranks, swaps, and overlap calculations.", 8);
        ensureChapter(reasoning, "Data Sufficiency", "data-sufficiency", "Evaluating statement uniqueness independently and in combination.", 9);

        Category verbal = ensureCategory(
                "Verbal Ability",
                "verbal-ability",
                "English grammar accuracy, vocabulary, comprehension, para jumbles, and idioms.",
                "BookOpen"
        );
        ensureChapter(verbal, "Spotting Errors", "spotting-errors", "Subject-verb agreement, tenses, prepositions, and syntax.", 1);
        ensureChapter(verbal, "Sentence Improvement", "sentence-improvement", "Concise phrasing, conditionals, and eliminating redundancy.", 2);
        ensureChapter(verbal, "Synonyms & Antonyms", "synonyms-and-antonyms", "Word roots, prefixes, suffixes, and contextual polarity.", 3);
        ensureChapter(verbal, "Reading Comprehension", "reading-comprehension", "Main idea, author tone analysis, and logical inference.", 4);
        ensureChapter(verbal, "Para Jumbles", "para-jumbles", "Sentence rearrangement using mandatory pairs and connectors.", 5);
        ensureChapter(verbal, "Idioms & Phrases", "idioms-and-phrases", "Figurative expressions and Latin/Greek one-word substitutions.", 6);
        ensureChapter(verbal, "Fill in the Blanks", "fill-in-the-blanks", "Context clues, transition words, and fixed prepositional locks.", 7);

        Category di = ensureCategory(
                "Data Interpretation",
                "data-interpretation",
                "Quantitative extraction from tables, charts, line trends, and mixed caselets.",
                "BarChart3"
        );
        ensureChapter(di, "Tables & Percentage Share", "tables-and-percentage-share", "Tabular revenue comparisons, YoY growth, and quarterly averages.", 1);
        ensureChapter(di, "Bar & Pie Charts", "bar-and-pie-charts", "Central angle conversions, sector values, and comparative chart ratios.", 2);
        ensureChapter(di, "Line Graphs & Trend Analysis", "line-graphs", "Period-over-period growth slopes and trend comparisons.", 3);
        ensureChapter(di, "Caselets & Mixed DI", "caselets-and-mixed-di", "Translating paragraph caselets into 2x2 grids and Venn diagrams.", 4);

        // 3. Refresh Short Notes & Speed Tricks and Top Up Every Chapter to >= 100 MCQs
        List<Chapter> allChapters = chapterRepository.findAll();
        int totalAdded = 0;

        for (Chapter chapter : allChapters) {
            chapter.setStudyMaterialJson(AptitudeQuestionFactory.buildStudyMaterialJson(chapter.getSlug()));
            chapterRepository.save(chapter);

            long existingCount = questionRepository.countByChapterId(chapter.getId());
            if (existingCount < MIN_QUESTIONS_PER_CHAPTER) {
                int needed = (int) (MIN_QUESTIONS_PER_CHAPTER - existingCount);
                List<Question> generated = AptitudeQuestionFactory.generateQuestionsForChapter(chapter, needed);
                questionRepository.saveAll(generated);
                totalAdded += needed;
                System.out.println("    -> Scaled '" + chapter.getTitle() + "' by +" + needed + " questions (Total: " + MIN_QUESTIONS_PER_CHAPTER + ")");
            }
        }

        // 4. Ensure Mock Tests exist
        if (mockTestRepository.count() == 0) {
            seedMockTests();
        }

        System.out.println(">>> Full Syllabus Ready! Added " + totalAdded + " new questions across " + allChapters.size() + " chapters.");
    }

    private Category ensureCategory(String name, String slug, String description, String icon) {
        return categoryRepository.findBySlug(slug).orElseGet(() -> {
            Category c = new Category(name, slug, description, icon);
            return categoryRepository.save(c);
        });
    }

    private Chapter ensureChapter(Category category, String title, String slug, String description, int order) {
        return chapterRepository.findBySlug(slug).orElseGet(() -> {
            Chapter ch = new Chapter(category, title, slug, description, order);
            ch.setStudyMaterialJson(AptitudeQuestionFactory.buildStudyMaterialJson(slug));
            return chapterRepository.save(ch);
        });
    }

    private void seedMockTests() {
        List<Question> allQuestions = questionRepository.findAll();
        if (allQuestions.isEmpty()) return;

        MockTest fullMock = new MockTest(
                "Full Syllabus Aptitude Mock Test #1",
                "Simulated full-length exam covering Quantitative Aptitude, Logical Reasoning, Verbal Ability, and Data Interpretation (-0.25 negative marking).",
                30,
                15,
                0.25,
                Difficulty.MEDIUM
        );
        List<Question> mock1Questions = new ArrayList<>();
        int step1 = Math.max(1, allQuestions.size() / 15);
        for (int i = 0; i < 15 && i * step1 < allQuestions.size(); i++) {
            mock1Questions.add(allQuestions.get(i * step1));
        }
        fullMock.setQuestions(mock1Questions);
        mockTestRepository.save(fullMock);

        MockTest speedMock = new MockTest(
                "Arithmetic & Reasoning Speed Sprint",
                "High-speed assessment focusing on rapid calculations, percentages, ratios, and syllogisms under tight time constraints.",
                15,
                10,
                0.25,
                Difficulty.HARD
        );
        List<Question> mock2Questions = new ArrayList<>();
        int step2 = Math.max(1, allQuestions.size() / 10);
        for (int i = 0; i < 10 && (i * step2 + 3) < allQuestions.size(); i++) {
            mock2Questions.add(allQuestions.get(i * step2 + 3));
        }
        speedMock.setQuestions(mock2Questions);
        mockTestRepository.save(speedMock);
    }
}
