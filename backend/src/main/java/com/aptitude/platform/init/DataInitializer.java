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
        if (userRepository.count() > 0) {
            // If already initialized, make sure mock tests exist
            if (mockTestRepository.count() == 0) {
                seedMockTests();
            }
            return;
        }

        System.out.println(">>> Initializing Aptitude Platform Database with Seed Data (Phase 1 & Phase 2)...");

        // 1. Seed Users
        User admin = new User("admin@aptitude.com", passwordEncoder.encode("Admin@123"), "System Admin", Role.ROLE_ADMIN);
        userRepository.save(admin);
        userStreakRepository.save(new UserStreak(admin));

        User student = new User("student@test.com", passwordEncoder.encode("Student@123"), "Aarav Sharma", Role.ROLE_STUDENT);
        userRepository.save(student);
        userStreakRepository.save(new UserStreak(student));

        // 2. Seed Categories & Chapters
        Category quant = new Category(
                "Quantitative Aptitude",
                "quantitative-aptitude",
                "Master numerical ability, arithmetic, algebra, and advanced problem solving.",
                "Calculator"
        );
        categoryRepository.save(quant);

        Chapter percentages = new Chapter(quant, "Percentages", "percentages", "Concept of percentages, percentage change, successive discounts, and base conversions.", 1);
        Chapter profitLoss = new Chapter(quant, "Profit & Loss", "profit-and-loss", "Cost price, selling price, marked price, profit/loss percentage, and dishonest merchant problems.", 2);
        Chapter timeWork = new Chapter(quant, "Time & Work", "time-and-work", "Work efficiency, unitary method, pipes and cisterns, and wages problems.", 3);
        Chapter numberSystems = new Chapter(quant, "Number Systems", "number-systems", "Divisibility rules, unit digits, remainders, LCM & HCF, and prime factors.", 4);
        Chapter ratioProp = new Chapter(quant, "Ratio & Proportion", "ratio-and-proportion", "Direct and inverse proportions, compound ratio, and mixture/allegations.", 5);

        chapterRepository.save(percentages);
        chapterRepository.save(profitLoss);
        chapterRepository.save(timeWork);
        chapterRepository.save(numberSystems);
        chapterRepository.save(ratioProp);

        Category reasoning = new Category(
                "Logical Reasoning",
                "logical-reasoning",
                "Enhance analytical deduction, abstract reasoning, and pattern recognition skills.",
                "Brain"
        );
        categoryRepository.save(reasoning);

        Chapter bloodRelations = new Chapter(reasoning, "Blood Relations", "blood-relations", "Family tree deductions, coded relations, and pointing-to-photograph puzzles.", 1);
        Chapter syllogisms = new Chapter(reasoning, "Syllogisms", "syllogisms", "Venn diagrams, standard premise deductions, 'some/all/no' conclusions.", 2);
        Chapter codingDecoding = new Chapter(reasoning, "Coding-Decoding", "coding-decoding", "Letter shifts, numerical cipher substitutes, and matrix code systems.", 3);

        chapterRepository.save(bloodRelations);
        chapterRepository.save(syllogisms);
        chapterRepository.save(codingDecoding);

        Category verbal = new Category(
                "Verbal Ability",
                "verbal-ability",
                "Sharpen English comprehension, grammar rules, vocabulary, and verbal reasoning.",
                "BookOpen"
        );
        categoryRepository.save(verbal);

        Chapter spottingErrors = new Chapter(verbal, "Spotting Errors", "spotting-errors", "Subject-verb agreement, tenses, prepositional phrases, and grammatical inaccuracies.", 1);
        Chapter sentenceImprovement = new Chapter(verbal, "Sentence Improvement", "sentence-improvement", "Phrasal verbs, idioms, clause re-structuring, and concise phrasing.", 2);

        chapterRepository.save(spottingErrors);
        chapterRepository.save(sentenceImprovement);

        // 3. Seed Realistic High-Quality MCQs
        seedPercentagesQuestions(percentages);
        seedProfitLossQuestions(profitLoss);
        seedTimeWorkQuestions(timeWork);
        seedNumberSystemsQuestions(numberSystems);
        seedRatioProportionQuestions(ratioProp);
        seedBloodRelationsQuestions(bloodRelations);
        seedSyllogismsQuestions(syllogisms);
        seedCodingDecodingQuestions(codingDecoding);
        seedSpottingErrorsQuestions(spottingErrors);

        // 4. Seed Mock Tests
        seedMockTests();

        System.out.println(">>> Seed Data Successfully Initialized!");
    }

    private void seedMockTests() {
        List<Question> allQuestions = questionRepository.findAll();
        if (allQuestions.isEmpty()) return;

        // Mock Test 1: Full Syllabus Mock Test (15 Questions, 30 Mins)
        MockTest fullMock = new MockTest(
                "Full Syllabus Aptitude Mock Test #1",
                "Simulated real exam covering Quantitative Aptitude, Logical Reasoning, and Verbal Ability with negative marking (-0.25).",
                30,
                15,
                0.25,
                Difficulty.MEDIUM
        );
        List<Question> mock1Questions = new ArrayList<>(allQuestions.subList(0, Math.min(15, allQuestions.size())));
        fullMock.setQuestions(mock1Questions);
        mockTestRepository.save(fullMock);

        // Mock Test 2: Arithmetic Speed Challenge (10 Questions, 15 Mins)
        MockTest speedMock = new MockTest(
                "Arithmetic & Reasoning Speed Sprint",
                "High-speed challenge focusing on rapid calculations, percentages, ratios, and syllogisms under tight time constraints.",
                15,
                10,
                0.25,
                Difficulty.HARD
        );
        List<Question> mock2Questions = new ArrayList<>();
        for (int i = 0; i < Math.min(10, allQuestions.size()); i++) {
            mock2Questions.add(allQuestions.get((i * 2) % allQuestions.size()));
        }
        speedMock.setQuestions(mock2Questions);
        mockTestRepository.save(speedMock);
    }

    private void seedPercentagesQuestions(Chapter chapter) {
        addQuestion(
                chapter,
                "If the price of sugar increases by 25%, by what percentage must a household reduce its consumption so that the expenditure on sugar remains the same?",
                Difficulty.EASY,
                "Let the original price = Rs. 100/kg and original consumption = 100 kg. Original expenditure = 100 * 100 = Rs. 10,000.\nNew price = 100 + 25 = Rs. 125/kg.\nNew consumption = 10,000 / 125 = 80 kg.\nReduction in consumption = 100 - 80 = 20 kg.\nPercentage reduction = (20 / 100) * 100 = 20%.\nFormula: [R / (100 + R)] * 100 = [25 / 125] * 100 = 20%.",
                new String[]{"15%", "20%", "25%", "16.67%"},
                "B"
        );

        addQuestion(
                chapter,
                "In an examination, 35% of the students failed in Mathematics and 45% failed in English. If 20% of the students failed in both subjects, what percentage of students passed in both subjects?",
                Difficulty.MEDIUM,
                "Let total students = 100%.\nPercentage failed in Math n(M) = 35%.\nPercentage failed in English n(E) = 45%.\nPercentage failed in both n(M ∩ E) = 20%.\nTotal percentage failed in at least one subject n(M ∪ E) = n(M) + n(E) - n(M ∩ E) = 35 + 45 - 20 = 60%.\nTherefore, percentage passed in both subjects = 100% - 60% = 40%.",
                new String[]{"40%", "35%", "50%", "45%"},
                "A"
        );

        addQuestion(
                chapter,
                "A number is first increased by 20% and then decreased by 20%. What is the net percentage change in the number?",
                Difficulty.EASY,
                "Let initial number = 100.\nAfter 20% increase = 100 * 1.20 = 120.\nAfter 20% decrease = 120 - (0.20 * 120) = 120 - 24 = 96.\nNet change = 100 - 96 = 4% decrease.\nShortcut formula: When a value is increased by x% and decreased by x%, net change is always a decrease of (x²/100)% = (20²/100)% = 4% decrease.",
                new String[]{"No change", "2% decrease", "4% decrease", "4% increase"},
                "C"
        );

        addQuestion(
                chapter,
                "Fresh fruit contains 68% water and dry fruit contains 20% water. How much dry fruit can be obtained from 100 kg of fresh fruits?",
                Difficulty.HARD,
                "The quantity of fruit pulp remains constant in both fresh and dry fruits.\nIn 100 kg fresh fruit: Water = 68%, Pulp = 100 - 68 = 32% = 32 kg.\nIn dry fruit: Water = 20%, so Pulp = 80%.\nLet the quantity of dry fruit obtained be x kg.\n80% of x = 32 kg\n(80 / 100) * x = 32\nx = (32 * 100) / 80 = 40 kg.",
                new String[]{"32 kg", "40 kg", "52 kg", "60 kg"},
                "B"
        );

        addQuestion(
                chapter,
                "If A's salary is 50% more than B's salary, then by what percentage is B's salary less than A's salary?",
                Difficulty.EASY,
                "Let B's salary = 100.\nThen A's salary = 100 + 50 = 150.\nDifference in salary = 150 - 100 = 50.\nPercentage by which B's salary is less than A = (Difference / A's salary) * 100 = (50 / 150) * 100 = 33.33% (or 33 1/3%).",
                new String[]{"25%", "33.33%", "50%", "40%"},
                "B"
        );
    }

    private void seedProfitLossQuestions(Chapter chapter) {
        addQuestion(
                chapter,
                "A trader sells an article for Rs. 840 and gains 20%. What was the cost price of the article?",
                Difficulty.EASY,
                "Selling Price (SP) = Rs. 840, Gain% = 20%.\nCost Price (CP) = SP * 100 / (100 + Gain%)\nCP = (840 * 100) / 120 = 7 * 100 = Rs. 700.",
                new String[]{"Rs. 680", "Rs. 700", "Rs. 720", "Rs. 750"},
                "B"
        );

        addQuestion(
                chapter,
                "By selling 33 meters of cloth, a shopkeeper gains the selling price of 11 meters. Find his gain percentage.",
                Difficulty.MEDIUM,
                "Let SP of 1 meter cloth = Re. 1.\nSP of 33 meters cloth = Rs. 33.\nGain = SP of 11 meters = Rs. 11.\nCP of 33 meters cloth = SP - Gain = 33 - 11 = Rs. 22.\nGain percentage = (Gain / CP) * 100 = (11 / 22) * 100 = 50%.",
                new String[]{"25%", "33.33%", "50%", "66.67%"},
                "C"
        );

        addQuestion(
                chapter,
                "A dishonest dealer claims to sell his goods at cost price, but uses a weight of 900 grams for a 1 kg weight. What is his real gain percentage?",
                Difficulty.MEDIUM,
                "True value = 1000 grams. False value used = 900 grams. Error = 100 grams.\nGain percentage = [Error / (True Value - Error)] * 100\n= [100 / 900] * 100 = 100 / 9 = 11.11% (or 11 1/9%).",
                new String[]{"10%", "11.11%", "12.5%", "9.09%"},
                "B"
        );
    }

    private void seedTimeWorkQuestions(Chapter chapter) {
        addQuestion(
                chapter,
                "A can complete a piece of work in 12 days and B can do the same work in 18 days. If they work together, in how many days will the work be completed?",
                Difficulty.EASY,
                "A's 1-day work = 1/12.\nB's 1-day work = 1/18.\nCombined 1-day work = 1/12 + 1/18 = (3 + 2) / 36 = 5/36.\nTotal days required = 36 / 5 = 7.2 days (or 7 1/5 days).",
                new String[]{"6 days", "7.2 days", "8 days", "9 days"},
                "B"
        );

        addQuestion(
                chapter,
                "A and B together can do a job in 8 days. B alone can do it in 12 days. In how many days can A alone finish the work?",
                Difficulty.EASY,
                "A + B's 1-day work = 1/8.\nB's 1-day work = 1/12.\nA's 1-day work = (1/8) - (1/12) = (3 - 2) / 24 = 1/24.\nHence, A alone takes 24 days.",
                new String[]{"16 days", "20 days", "24 days", "30 days"},
                "C"
        );
    }

    private void seedNumberSystemsQuestions(Chapter chapter) {
        addQuestion(
                chapter,
                "What is the unit digit in the product (7^95 - 3^58)?",
                Difficulty.MEDIUM,
                "Cyclicity of 7 is 4: 7^1=7, 7^2=9, 7^3=3, 7^4=1.\n95 mod 4 = 3, so unit digit of 7^95 = 3.\nCyclicity of 3 is 4: 3^1=3, 3^2=9, 3^3=7, 3^4=1.\n58 mod 4 = 2, so unit digit of 3^58 = 9.\nUnit digit of (7^95 - 3^58) = (3 - 9) -> (13 - 9) = 4.",
                new String[]{"0", "4", "6", "7"},
                "B"
        );

        addQuestion(
                chapter,
                "Find the greatest number that will divide 43, 91 and 183 so as to leave the same remainder in each case.",
                Difficulty.MEDIUM,
                "Required number = HCF of |91 - 43|, |183 - 91|, and |183 - 43|.\n= HCF of 48, 92, and 140.\n48 = 4 * 12\n92 = 4 * 23\n140 = 4 * 35\nSince 12, 23, and 35 have no common factors, HCF = 4.",
                new String[]{"4", "7", "9", "13"},
                "A"
        );
    }

    private void seedRatioProportionQuestions(Chapter chapter) {
        addQuestion(
                chapter,
                "If A : B = 2 : 3, B : C = 4 : 5, and C : D = 6 : 7, find A : D.",
                Difficulty.EASY,
                "A/D = (A/B) * (B/C) * (C/D) = (2/3) * (4/5) * (6/7) = (2 * 4 * 2) / (1 * 5 * 7) = 16 / 35.\nTherefore, A : D = 16 : 35.",
                new String[]{"16 : 35", "12 : 35", "8 : 15", "16 : 45"},
                "A"
        );
    }

    private void seedBloodRelationsQuestions(Chapter chapter) {
        addQuestion(
                chapter,
                "Pointing to a photograph of a boy, Suresh said, 'He is the son of the only son of my mother.' How is Suresh related to that boy?",
                Difficulty.EASY,
                "Break down the statement from the end:\n'My mother' -> Suresh's mother.\n'The only son of my mother' -> Suresh himself (since Suresh is a male and the only son).\n'He is the son of [Suresh]' -> The boy is Suresh's son.\nTherefore, Suresh is the Father of that boy.",
                new String[]{"Brother", "Uncle", "Father", "Cousin"},
                "C"
        );

        addQuestion(
                chapter,
                "If A is the brother of B, B is the sister of C, and C is the father of D, how is D related to A?",
                Difficulty.MEDIUM,
                "A, B, and C are siblings (A is male, B is female, C is male).\nC is the father of D.\nTherefore, A is the paternal uncle of D, and D is either the Nephew or Niece of A (gender of D not specified).\nGiven standard options, D is nephew/niece.",
                new String[]{"Nephew or Niece", "Brother", "Son", "Cousin"},
                "A"
        );
    }

    private void seedSyllogismsQuestions(Chapter chapter) {
        addQuestion(
                chapter,
                "Statements:\n1. All cats are dogs.\n2. All dogs are birds.\nConclusions:\nI. All cats are birds.\nII. All birds are cats.",
                Difficulty.EASY,
                "Draw concentric circles:\nInner circle = Cats.\nMiddle circle = Dogs (since all cats are dogs).\nOuter circle = Birds (since all dogs are birds).\nConclusion I: Since the Cats circle is entirely within Birds, 'All cats are birds' is TRUE.\nConclusion II: Birds circle is larger than Cats, so only some birds are cats. Thus II is FALSE.\nAnswer: Only conclusion I follows.",
                new String[]{"Only conclusion I follows", "Only conclusion II follows", "Both I and II follow", "Neither I nor II follows"},
                "A"
        );
    }

    private void seedCodingDecodingQuestions(Chapter chapter) {
        addQuestion(
                chapter,
                "In a certain code language, if 'COMPUTER' is written as 'RFUVQNPC', then how is 'MEDICINE' written in that code?",
                Difficulty.MEDIUM,
                "Pattern analysis:\nWord: C O M P U T E R\nReverse order of letters: R E T U P M O C\nAdd +1 to middle letters, keep first and last intact:\nR, E(+1=F), T(+1=U), U(+1=V), P(+1=Q), M(+1=N), O(+1=P), C -> RFUVQNPC.\nApplying to MEDICINE:\nReverse: E N I C I D E M\nFirst/last keep same: E, N(+1=O), I(+1=J), C(+1=D), I(+1=J), D(+1=E), E(+1=F), M -> EOJDJEFM.",
                new String[]{"EOJDJEFM", "EOJDEJFM", "MFEJDJOE", "EOJDJFEM"},
                "A"
        );
    }

    private void seedSpottingErrorsQuestions(Chapter chapter) {
        addQuestion(
                chapter,
                "Identify the part with error:\n(A) Neither of the two candidates / (B) have submitted / (C) their application form / (D) on time.",
                Difficulty.MEDIUM,
                "The pronoun 'Neither' is singular and takes a singular verb.\nIn part (B), 'have submitted' is plural and incorrect.\nThe correct sentence should be: 'Neither of the two candidates has submitted his/her application form on time.'\nHence, error is in part (B).",
                new String[]{"Part (A)", "Part (B)", "Part (C)", "Part (D)"},
                "B"
        );
    }

    private void addQuestion(Chapter chapter, String text, Difficulty diff, String explanation, String[] options, String correctKey) {
        Question q = new Question(chapter, text, diff, explanation);
        q = questionRepository.save(q);

        String[] keys = {"A", "B", "C", "D"};
        for (int i = 0; i < options.length; i++) {
            boolean isCorrect = keys[i].equalsIgnoreCase(correctKey);
            QuestionOption opt = new QuestionOption(keys[i], options[i], isCorrect);
            q.addOption(opt);
        }
        questionRepository.save(q);
    }
}
