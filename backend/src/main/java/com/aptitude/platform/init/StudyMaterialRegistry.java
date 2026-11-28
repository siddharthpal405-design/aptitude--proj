package com.aptitude.platform.init;

import com.aptitude.platform.dto.StudyMaterialDto;
import com.aptitude.platform.dto.StudyMaterialDto.FormulaItem;
import com.aptitude.platform.dto.StudyMaterialDto.WorkedExample;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Registry providing concise Short Notes, Formulas, Speed-Solving Tricks (<30s),
 * and Worked Examples for all 34 aptitude chapters.
 */
public final class StudyMaterialRegistry {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private StudyMaterialRegistry() {}

    public static String buildStudyMaterialJson(String slug) {
        try {
            return MAPPER.writeValueAsString(getStudyMaterial(slug));
        } catch (Exception e) {
            return "{}";
        }
    }

    public static StudyMaterialDto getStudyMaterial(String slug) {
        return switch (slug) {
            // ================= QUANTITATIVE APTITUDE (14) =================
            case "percentages" -> new StudyMaterialDto(
                    "Percentages express values per 100. Converting common percentages into fractions cuts calculation time by 70%.",
                    List.of(
                            new FormulaItem("Percentage Value", "x% of y = (x / 100) * y"),
                            new FormulaItem("Price Up R% (Same Spend)", "Consumption Drop = [R / (100 + R)] * 100%"),
                            new FormulaItem("Successive Change (a%, b%)", "Net = a + b + (ab / 100)%"),
                            new FormulaItem("Compound Growth (t yrs)", "Final = P * (1 + R/100)^t")
                    ),
                    List.of(
                            "Fraction Trick: Memorize 1/6 = 16.67%, 1/7 = 14.28%, 1/8 = 12.5%, 1/9 = 11.11%, 1/12 = 8.33%.",
                            "Mirror Change (+x% then -x%): Always a net loss of (x^2 / 100)%—no base needed.",
                            "Split & Solve: To compute 15% of 480, do 10% (48) + half of it (24) = 72 mentally."
                    ),
                    new WorkedExample(
                            "If sugar price rises by 25%, by what % must consumption drop so expenditure stays constant?",
                            "[R / (100 + R)] * 100 = (25 / 125) * 100 = 20%."
                    )
            );
            case "profit-and-loss" -> new StudyMaterialDto(
                    "Profit and Loss relates Cost Price (CP), Selling Price (SP), and Marked Price (MP). Always base profit/loss on CP and discounts on MP.",
                    List.of(
                            new FormulaItem("Profit / Loss %", "((|SP - CP|) / CP) * 100"),
                            new FormulaItem("CP-MP Direct Link", "CP / MP = (100 - Discount%) / (100 + Profit%)"),
                            new FormulaItem("False Weight Gain %", "[Error / (True Weight - Error)] * 100"),
                            new FormulaItem("Equivalent Discount", "D = d1 + d2 - (d1 * d2) / 100")
                    ),
                    List.of(
                            "Same SP, one at +x% and one at -x%: Always an overall loss of (x^2 / 100)%.",
                            "False Weight Trick: Divide the weight saved by the actual weight delivered, then multiply by 100.",
                            "Goods Sold vs Gain Units: If SP of N items gives profit of SP of K items, Profit% = [K / (N - K)] * 100."
                    ),
                    new WorkedExample(
                            "A trader sells an item for Rs. 840 at 20% profit. Find CP.",
                            "CP = (840 * 100) / 120 = Rs. 700."
                    )
            );
            case "time-and-work" -> new StudyMaterialDto(
                    "Time & Work measures completion rates. Replace messy fractions with integer Total Work Units using the LCM method.",
                    List.of(
                            new FormulaItem("2-Worker Shortcut", "Combined Time = (a * b) / (a + b)"),
                            new FormulaItem("Man-Days-Hours Chain", "(M1 * D1 * H1) / W1 = (M2 * D2 * H2) / W2"),
                            new FormulaItem("Pipes (Fill x, Empty y)", "Net Time = (x * y) / (y - x)"),
                            new FormulaItem("Efficiency Relation", "Time is inversely proportional to Efficiency")
                    ),
                    List.of(
                            "LCM Unit Trick: Assume Total Work = LCM of individual days to solve purely with integers.",
                            "Ratio Split: If A is k times faster than B and together they finish in T days, B alone takes T*(k+1) days.",
                            "Wages Rule: Split wages strictly in the ratio of work completed, not days worked."
                    ),
                    new WorkedExample(
                            "A takes 12 days and B takes 18 days. Find combined time.",
                            "LCM(12,18) = 36 units. A=3 u/d, B=2 u/d -> 5 u/d -> 36/5 = 7.2 days."
                    )
            );
            case "number-systems" -> new StudyMaterialDto(
                    "Number Systems covers divisibility rules, unit digit cyclicity, HCF/LCM identities, and modular arithmetic.",
                    List.of(
                            new FormulaItem("HCF-LCM Product", "HCF(a, b) * LCM(a, b) = a * b"),
                            new FormulaItem("Sum of First n Naturals", "n * (n + 1) / 2"),
                            new FormulaItem("Division Algorithm", "Dividend = (Divisor * Quotient) + Remainder"),
                            new FormulaItem("Trailing Zeroes in n!", "floor(n/5) + floor(n/25) + floor(n/125)")
                    ),
                    List.of(
                            "Unit Digit Cycle of 4: For bases ending in 2, 3, 7, 8, replace exponent E with (E mod 4; use 4 if 0).",
                            "Divisibility by 11: Alternate digit sums difference is 0 or multiple of 11.",
                            "Remainder Power Trick: Reduce base mod divisor first (e.g., 2^4 = 16 = 1 mod 5)."
                    ),
                    new WorkedExample(
                            "Find unit digit of 7^95.",
                            "95 mod 4 = 3. Last digit of 7^3 (343) = 3."
                    )
            );
            case "ratio-and-proportion" -> new StudyMaterialDto(
                    "Ratios compare quantities of identical units and power mixture, partnership, and distribution problems.",
                    List.of(
                            new FormulaItem("Fourth Proportional (a:b::c:x)", "x = (b * c) / a"),
                            new FormulaItem("Mean Proportional (a, b)", "x = sqrt(a * b)"),
                            new FormulaItem("Merge A:B and B:C", "A:C = (A/B) * (B/C)"),
                            new FormulaItem("Duplicate Ratio", "a^2 : b^2")
                    ),
                    List.of(
                            "N-Method for A:B:C: Multiply down-left, diagonal, down-right to merge two ratios in 3 seconds.",
                            "Invariant Trick: In mixtures where only water is added, keep original milk quantity constant."
                    ),
                    new WorkedExample(
                            "If A:B = 2:3 and B:C = 4:5, find A:C.",
                            "A/C = (2/3) * (4/5) = 8 : 15."
                    )
            );
            case "simple-and-compound-interest" -> new StudyMaterialDto(
                    "Simple Interest stays constant yearly on Principal, whereas Compound Interest grows exponentially on accumulated amount.",
                    List.of(
                            new FormulaItem("Simple Interest", "SI = (P * R * T) / 100"),
                            new FormulaItem("Compound Amount", "A = P * (1 + R/100)^T"),
                            new FormulaItem("2-Year CI - SI Diff", "Diff = P * (R / 100)^2"),
                            new FormulaItem("Effective 2-Yr CI Rate", "R_eff = 2R + (R^2 / 100)%")
                    ),
                    List.of(
                            "2-Year CI Shortcut: At 10% p.a. for 2 yrs, CI = 21% of P (since 10+10+100/100=21%), SI = 20%, Diff = 1%.",
                            "Rule of 72: Approximate years for money to double at CI = 72 / R%.",
                            "Equal Ratio Growth: Under CI, money multiplies by the exact same factor in every equal time interval."
                    ),
                    new WorkedExample(
                            "Find the difference between CI and SI on Rs. 10,000 at 10% p.a. for 2 years.",
                            "Diff = P * (R/100)^2 = 10000 * (10/100)^2 = 10000 * 0.01 = Rs. 100."
                    )
            );
            case "time-speed-and-distance" -> new StudyMaterialDto(
                    "Relates Speed, Time, and Distance including relative velocity for trains and downstream/upstream speeds for boats.",
                    List.of(
                            new FormulaItem("Core Formula", "Distance = Speed * Time"),
                            new FormulaItem("Unit Conversion", "km/h * (5/18) = m/s; m/s * (18/5) = km/h"),
                            new FormulaItem("Avg Speed (Equal Dist)", "V_avg = (2 * x * y) / (x + y)"),
                            new FormulaItem("Boats & Streams", "Boat = (Down + Up)/2; Stream = (Down - Up)/2")
                    ),
                    List.of(
                            "Never use (x+y)/2 for equal-distance average speed; always use Harmonic Mean 2xy/(x+y).",
                            "Opposite Trains: Add speeds AND add lengths. Same-direction Trains: Subtract speeds, add lengths.",
                            "Late/Early Shortcut: Distance = [(S1 * S2) / |S1 - S2|] * Time_Difference_in_Hours."
                    ),
                    new WorkedExample(
                            "A car travels A to B at 40 km/h and returns at 60 km/h. Find average speed.",
                            "V_avg = (2 * 40 * 60) / (40 + 60) = 4800 / 100 = 48 km/h."
                    )
            );
            case "averages-and-mixtures" -> new StudyMaterialDto(
                    "Averages find the central mean, while Alligation calculates the exact mixing ratio of two components at different prices or concentrations.",
                    List.of(
                            new FormulaItem("Arithmetic Average", "Avg = Sum of Observations / Count"),
                            new FormulaItem("Alligation Cross Rule", "Qty_Cheaper / Qty_Dearer = (Dearer - Mean) / (Mean - Cheaper)"),
                            new FormulaItem("Repeated Replacement", "Final Pure = Initial * (1 - Removed / Total)^n")
                    ),
                    List.of(
                            "Deviation Trick: Pick a clean anchor number near the middle; average the positive/negative differences and add back.",
                            "Consecutive Odd/Even Avg: For any AP or consecutive numbers, Average = exact Middle Number = (First + Last)/2.",
                            "New Entrant Trick: If average rises by +d when 1 person joins N people, New Weight = Old Avg + (N+1)*d."
                    ),
                    new WorkedExample(
                            "In what ratio must rice at Rs. 40/kg be mixed with Rs. 60/kg rice to get a mixture worth Rs. 48/kg?",
                            "Ratio = (60 - 48) : (48 - 40) = 12 : 8 = 3 : 2."
                    )
            );
            case "ages-and-partnerships" -> new StudyMaterialDto(
                    "Age problems leverage constant age differences across time, while Partnerships split profit proportional to Capital * Time.",
                    List.of(
                            new FormulaItem("Age Invariant", "Age(A) - Age(B) stays constant across all years"),
                            new FormulaItem("Partnership Share", "Profit Ratio = (Invest_1 * Time_1) : (Invest_2 * Time_2)"),
                            new FormulaItem("Average Age Shift", "After k years, average age increases by +k")
                    ),
                    List.of(
                            "Constant Gap Trick: Make the ratio unit difference equal between 'Before' and 'After' ratios to skip algebra completely.",
                            "For Partnerships, express all time durations in the same unit (months) before multiplying with capital."
                    ),
                    new WorkedExample(
                            "A and B invest Rs. 20,000 for 6 months and Rs. 15,000 for 8 months respectively. Find profit ratio.",
                            "Ratio = (20000 * 6) : (15000 * 8) = 120,000 : 120,000 = 1 : 1."
                    )
            );
            case "permutation-and-combination" -> new StudyMaterialDto(
                    "Permutations count ordered arrangements (nPr), while Combinations count unordered selections (nCr).",
                    List.of(
                            new FormulaItem("Permutations (Ordered)", "nPr = n! / (n - r)!"),
                            new FormulaItem("Combinations (Selected)", "nCr = n! / (r! * (n - r)!)"),
                            new FormulaItem("Circular Arrangement", "(n - 1)! ways"),
                            new FormulaItem("Word with Repeats", "n! / (p! * q! * ...)")
                    ),
                    List.of(
                            "AND = Multiply; OR = Add.",
                            "Symmetry Shortcut: nCr = nC(n-r). Compute 10C8 as 10C2 = (10*9)/2 = 45.",
                            "Together Block Trick: Tie 'k' items that must stay together as 1 super-unit -> arrange (n-k+1)! * k!."
                    ),
                    new WorkedExample(
                            "In how many ways can 5 people sit around a circular table?",
                            "Circular arrangements = (n - 1)! = (5 - 1)! = 4! = 24 ways."
                    )
            );
            case "probability" -> new StudyMaterialDto(
                    "Probability quantifies the likelihood of an event occurring between 0 (impossible) and 1 (certain).",
                    List.of(
                            new FormulaItem("Basic Probability", "P(E) = Favorable Outcomes / Total Outcomes"),
                            new FormulaItem("Complement Rule", "P(at least 1) = 1 - P(none)"),
                            new FormulaItem("Addition Rule", "P(A U B) = P(A) + P(B) - P(A n B)"),
                            new FormulaItem("Independent Events", "P(A and B) = P(A) * P(B)")
                    ),
                    List.of(
                            "'At Least One' Shortcut: Always calculate 1 - P(none) instead of summing multiple positive cases.",
                            "Two Dice Sum Map: Total=36. Ways for sum S = S-1 (for S<=7) and 13-S (for S>7). Sum=7 has 6 ways.",
                            "Standard Deck: 52 cards = 26 Red + 26 Black; 4 Suits of 13; 12 Face cards (J, Q, K)."
                    ),
                    new WorkedExample(
                            "Two fair coins are tossed. What is the probability of getting at least one head?",
                            "P(at least 1 H) = 1 - P(no H) = 1 - P(TT) = 1 - 1/4 = 3/4."
                    )
            );
            case "algebra-and-equations" -> new StudyMaterialDto(
                    "Covers linear systems, quadratic root relationships, and algebraic identities used to simplify complex expressions.",
                    List.of(
                            new FormulaItem("Quadratic Roots Sum/Prod", "a + b = -B/A; a * b = C/A"),
                            new FormulaItem("Reciprocal Square", "If x + 1/x = k, then x^2 + 1/x^2 = k^2 - 2"),
                            new FormulaItem("Reciprocal Cube", "If x + 1/x = k, then x^3 + 1/x^3 = k^3 - 3k"),
                            new FormulaItem("Discriminant (Equal Roots)", "D = B^2 - 4AC = 0")
                    ),
                    List.of(
                            "Option Plug-In: For multi-variable equations, substitute clean numbers (0, 1, 2) or test options directly.",
                            "Never expand (x + 1/x) manually—apply k^2 - 2 or k^3 - 3k directly in 5 seconds."
                    ),
                    new WorkedExample(
                            "If x + 1/x = 5, find x^2 + 1/x^2.",
                            "Using k^2 - 2: 5^2 - 2 = 25 - 2 = 23."
                    )
            );
            case "geometry-and-mensuration" -> new StudyMaterialDto(
                    "Computes lengths, angles, areas, surface areas, and volumes for 2D polygons, circles, and 3D solids.",
                    List.of(
                            new FormulaItem("Equilateral Triangle Area", "(sqrt(3) / 4) * a^2"),
                            new FormulaItem("Circle Area & Circum.", "Area = pi * r^2; C = 2 * pi * r"),
                            new FormulaItem("Cylinder & Cone Volume", "V_cyl = pi*r^2*h; V_cone = (1/3)*pi*r^2*h"),
                            new FormulaItem("Cube Diagonal", "d = a * sqrt(3)")
                    ),
                    List.of(
                            "Pythagorean Triplets: Memorize (3,4,5), (5,12,13), (8,15,17), (7,24,25) and their multiples.",
                            "Area Scaling Trick: If side/radius increases by x%, Area increases by (2x + x^2/100)%.",
                            "Volume Scaling Trick: Doubling radius multiplies sphere/cube volume by 8x (2^3)."
                    ),
                    new WorkedExample(
                            "If the radius of a circle increases by 20%, find the percentage increase in area.",
                            "Area % increase = 2(20) + (20^2)/100 = 40 + 4 = 44%."
                    )
            );
            case "progressions-and-series" -> new StudyMaterialDto(
                    "Covers Arithmetic Progression (constant difference), Geometric Progression (constant ratio), and Harmonic sequences.",
                    List.of(
                            new FormulaItem("AP nth Term & Sum", "Tn = a + (n-1)d; Sn = (n/2) * (First + Last)"),
                            new FormulaItem("GP nth Term", "Tn = a * r^(n-1)"),
                            new FormulaItem("Infinite GP (|r| < 1)", "S_inf = a / (1 - r)"),
                            new FormulaItem("Mean Relation", "GM^2 = AM * HM")
                    ),
                    List.of(
                            "Middle-Term Sum Trick: For any AP with odd count n, Sum = n * Middle_Term.",
                            "For 3 numbers in AP, assume (a-d, a, a+d); for GP, assume (a/r, a, ar) to cancel variables fast."
                    ),
                    new WorkedExample(
                            "Find the sum of an infinite GP with first term 12 and common ratio 1/3.",
                            "S_inf = a / (1 - r) = 12 / (1 - 1/3) = 12 / (2/3) = 18."
                    )
            );

            // ================= LOGICAL REASONING (9) =================
            case "blood-relations" -> new StudyMaterialDto(
                    "Blood Relations maps multi-generational family relationships from verbal or coded symbolic descriptions.",
                    List.of(
                            new FormulaItem("Generation +1", "Father, Mother, Uncle, Aunt"),
                            new FormulaItem("Generation 0", "Brother, Sister, Cousin, Spouse, In-law"),
                            new FormulaItem("Generation -1", "Son, Daughter, Nephew, Niece"),
                            new FormulaItem("Generation +2 / -2", "Grandparents / Grandchildren")
                    ),
                    List.of(
                            "Reverse Parse: Always start decoding pointing-to-person clues from the word 'my'.",
                            "Strict Gender Rule: Never assume gender from a person's name alone—use (+) Male and (-) Female."
                    ),
                    new WorkedExample(
                            "Suresh said: 'He is the son of the only son of my mother.' How is Suresh related to him?",
                            "'Only son of my mother' = Suresh. So the boy is Suresh's son -> Suresh is Father."
                    )
            );
            case "syllogisms" -> new StudyMaterialDto(
                    "Syllogisms evaluate deductive validity of conclusions drawn strictly from given categorical premises.",
                    List.of(
                            new FormulaItem("All S are P", "Circle S completely inside P"),
                            new FormulaItem("No S is P", "S and P are completely disjoint"),
                            new FormulaItem("Some S are P", "S and P overlap"),
                            new FormulaItem("Either-Or Condition", "Same terms + both false alone + (Some & No) or (All & Some not)")
                    ),
                    List.of(
                            "Minimum Overlap Rule: Draw circles with minimal forced intersection; a conclusion is definite only if true in ALL diagrams.",
                            "Two 'Some' statements alone never produce a definite conclusion between the outer terms."
                    ),
                    new WorkedExample(
                            "All cats are dogs. All dogs are birds. Conclusion: All cats are birds.",
                            "Cats is inside Dogs inside Birds -> Definite True."
                    )
            );
            case "coding-decoding" -> new StudyMaterialDto(
                    "Coding-Decoding analyzes letter shifts, opposite-letter pairing, word reversals, and digit mappings.",
                    List.of(
                            new FormulaItem("EJOTY Anchors", "E=5, J=10, O=15, T=20, Y=25"),
                            new FormulaItem("Opposite Pair Sum", "Forward(L) + Opposite(L) = 27 (A<->Z, B<->Y)"),
                            new FormulaItem("Circular Shift", "(Pos + Shift) mod 26")
                    ),
                    List.of(
                            "Check the First and Last letters first—70% of MCQ options can be eliminated in 5 seconds without decoding the whole word.",
                            "Memorize EJOTY to pinpoint any letter index instantaneously."
                    ),
                    new WorkedExample(
                            "If CAT = 24 (3+1+20), find DOG.",
                            "D(4) + O(15) + G(7) = 26."
                    )
            );
            case "number-and-letter-series" -> new StudyMaterialDto(
                    "Identifies progression rules in numerical or alphabetical sequences and detects missing or odd-one-out terms.",
                    List.of(
                            new FormulaItem("Difference Ladder", "Check 1st diff, then 2nd diff"),
                            new FormulaItem("Square/Cube Anchors", "n^2 +/- k or n^3 +/- k"),
                            new FormulaItem("Multiply & Offset", "T(i+1) = T(i) * k +/- d")
                    ),
                    List.of(
                            "Growth Speed Check: Slow gradual rise = Addition/Difference; Rapid exponential jump = Multiplication or Squares/Cubes.",
                            "Alternating Up-Down Pattern: Split the sequence into Odd-index and Even-index sub-series immediately."
                    ),
                    new WorkedExample(
                            "Find next term: 3, 7, 15, 31, 63, ?",
                            "Rule: *2 + 1. Next = 63 * 2 + 1 = 127."
                    )
            );
            case "direction-sense" -> new StudyMaterialDto(
                    "Determines final facing direction and shortest Euclidean displacement across multi-turn movement paths.",
                    List.of(
                            new FormulaItem("Shortest Distance", "D = sqrt((East - West)^2 + (North - South)^2)"),
                            new FormulaItem("Turn Cancellation", "1 Left + 1 Right = 0 deg; 4 Right = 360 deg"),
                            new FormulaItem("Shadow Rule", "Morning Shadow -> West; Evening Shadow -> East")
                    ),
                    List.of(
                            "NESW Cancellation Trick: Skip drawing maps! Tally total North, South, East, West steps, cancel opposites, and apply Pythagoras.",
                            "Cancel Left and Right turns first before finding final orientation."
                    ),
                    new WorkedExample(
                            "Walk 4 km North, then 3 km East. Find distance from start.",
                            "Shortest distance = sqrt(4^2 + 3^2) = 5 km (North-East)."
                    )
            );
            case "seating-arrangement" -> new StudyMaterialDto(
                    "Places individuals linearly or circularly under positional constraints such as immediate neighbors, opposites, and facing orientation.",
                    List.of(
                            new FormulaItem("Facing Center (Circle)", "Left = Clockwise; Right = Anti-Clockwise"),
                            new FormulaItem("Facing Outward (Circle)", "Left = Anti-Clockwise; Right = Clockwise"),
                            new FormulaItem("Linear Opposite Ends", "North Left = West; South Left = East")
                    ),
                    List.of(
                            "Anchor Definite Clue First: Place the person with an exact end or facing-center neighbor before relative clues.",
                            "'Who/Which' vs 'And': 'A sits next to B who faces C' -> 'who' refers to B; 'and' refers to A."
                    ),
                    new WorkedExample(
                            "6 people A-F sit in a circle facing center. A is opposite D, B is immediate right of A. Who is to B's left?",
                            "Since B is to A's immediate right (anti-clockwise from A), A is to B's immediate left."
                    )
            );
            case "clocks-and-calendars" -> new StudyMaterialDto(
                    "Computes angular separation between clock hands and day-of-week shifts across months, leap years, and centuries.",
                    List.of(
                            new FormulaItem("Clock Hand Angle", "Angle = |30*H - 5.5*M| degrees"),
                            new FormulaItem("Reflex Angle", "360 - Angle"),
                            new FormulaItem("Century Odd Days", "100y=5, 200y=3, 300y=1, 400y=0"),
                            new FormulaItem("Leap Year Rule", "Divisible by 4 (and by 400 if century year)")
                    ),
                    List.of(
                            "Clock Shortcut: Plug H and M directly into |30H - 5.5M|; if result > 180, subtract from 360 for smaller angle.",
                            "Calendar Repeat Shortcut: Leap year repeats in +28 yrs; Leap+1 in +6 yrs; Leap+2/+3 in +11 yrs.",
                            "Same Day Rule: Adding 7 days (or any multiple of 7) lands on the exact same weekday."
                    ),
                    new WorkedExample(
                            "Find the angle between clock hands at 3:30.",
                            "|30(3) - 5.5(30)| = |90 - 165| = 75 degrees."
                    )
            );
            case "order-and-ranking" -> new StudyMaterialDto(
                    "Finds total row sizes, positional ranks from left/right ends, and minimum/maximum overlapping counts.",
                    List.of(
                            new FormulaItem("Total Persons", "Total = Left + Right - 1"),
                            new FormulaItem("Rank from Other End", "Other = Total - Given + 1"),
                            new FormulaItem("Minimum Overlap", "Mid = (L + R) - Total - 2 (when L+R > Total)")
                    ),
                    List.of(
                            "Always subtract 1 when adding Left and Right ranks of the same person (avoids double counting).",
                            "Swap Shortcut: New Total = (Old rank of unmoved person) + (New rank of swapped person) - 1."
                    ),
                    new WorkedExample(
                            "Ravi is 12th from left and 18th from right in a row. How many students are in the row?",
                            "Total = 12 + 18 - 1 = 29 students."
                    )
            );
            case "data-sufficiency" -> new StudyMaterialDto(
                    "Tests whether Statement I, Statement II, both combined, or neither provide enough information to uniquely answer a question.",
                    List.of(
                            new FormulaItem("Option A", "Statement I alone is sufficient"),
                            new FormulaItem("Option B", "Statement II alone is sufficient"),
                            new FormulaItem("Option C", "Both I and II together are needed"),
                            new FormulaItem("Option D / E", "Either alone (D) / Neither sufficient (E)")
                    ),
                    List.of(
                            "Stop Early Trick: NEVER compute the full numerical answer—stop the moment you confirm uniqueness!",
                            "Zero Contamination: Completely erase Statement I from memory when evaluating Statement II alone.",
                            "Yes/No Trap: A statement giving a definite 'NO' is still 100% sufficient."
                    ),
                    new WorkedExample(
                            "What is x? I: x^2 = 16. II: x > 0.",
                            "I alone gives x = +4 or -4 (not unique). II alone gives infinitely many. Combined: x = +4 (unique) -> Both needed."
                    )
            );

            // ================= VERBAL ABILITY (7) =================
            case "spotting-errors" -> new StudyMaterialDto(
                    "Identifies grammatical inaccuracies across subject-verb agreement, pronouns, tenses, and fixed prepositions.",
                    List.of(
                            new FormulaItem("Singular Indefinites", "Each / Either / Neither / Everyone + Singular Verb"),
                            new FormulaItem("Nearest Subject Rule", "Neither A nor B + Verb matching B"),
                            new FormulaItem("Fixed Prepositions", "Prefer to, Senior to, Abstain from, Comprise (no 'of')")
                    ),
                    List.of(
                            "Ignore Prepositional Fluff: Cross out 'of...' phrases mentally to match the true Subject with its Verb.",
                            "'As well as / Along with' always matches the FIRST subject."
                    ),
                    new WorkedExample(
                            "Neither of the two applicants (A) / have submitted (B) / their form (C) / on time (D).",
                            "'Neither' is singular -> Part (B) should be 'has submitted'."
                    )
            );
            case "sentence-improvement" -> new StudyMaterialDto(
                    "Replaces awkward, redundant, or ungrammatical clauses with concise standard English phrasing.",
                    List.of(
                            new FormulaItem("Type-3 Conditional", "If + had V3, ... would have + V3"),
                            new FormulaItem("Correlative Pairs", "No sooner ... than; Hardly/Scarcely ... when"),
                            new FormulaItem("Gerund Triggers", "look forward to / with a view to + V-ing")
                    ),
                    List.of(
                            "Redundancy Cut: Immediately reject 'return back', 'repeat again', 'cousin brother', 'final conclusion'.",
                            "'It is high time' always requires Simple Past (V2)."
                    ),
                    new WorkedExample(
                            "No sooner had he arrived [when it rained].",
                            "'No sooner' pairs with 'than' -> 'than it rained'."
                    )
            );
            case "synonyms-and-antonyms" -> new StudyMaterialDto(
                    "Evaluates vocabulary depth through roots, prefixes, suffixes, and contextual polarity.",
                    List.of(
                            new FormulaItem("Positive Roots", "Bene- (good), Eu- (pleasant), Phil- (love)"),
                            new FormulaItem("Negative Roots", "Mal- (bad), Dys- (ill), Mis- (hate)"),
                            new FormulaItem("Scope Roots", "Omni- (all), Ambi- (both), Chron- (time)")
                    ),
                    List.of(
                            "Charge Elimination: Determine if the target word is Positive (+), Negative (-), or Neutral—eliminate opposite-charged options instantly.",
                            "Odd-One-Out Trick: If 3 options share the same meaning, the 4th outlier is usually the Antonym."
                    ),
                    new WorkedExample(
                            "Choose the Antonym of BENEVOLENT.",
                            "'Bene-' = good/kind. Opposite ('Mal-' = bad) -> MALEVOLENT."
                    )
            );
            case "reading-comprehension" -> new StudyMaterialDto(
                    "Assesses inference, main idea identification, author tone, and fact verification from structured passages.",
                    List.of(
                            new FormulaItem("Main Idea Location", "Usually anchored in First + Last sentences"),
                            new FormulaItem("Tone Analysis", "Look at descriptive adjectives & transition words"),
                            new FormulaItem("Inference Rule", "Must be 100% supported without outside assumptions")
                    ),
                    List.of(
                            "Question-First Skim: Read the question prompts before scanning the passage so you read with a target.",
                            "Extreme Word Trap: Options with 'always', 'never', 'only', 'impossible' are rarely correct in analytical passages."
                    ),
                    new WorkedExample(
                            "Passage mentions renewable energy reduces emissions but faces grid storage hurdles. What is the author's tone?",
                            "Balanced / Objective / Analytical (acknowledges both benefits and challenges)."
                    )
            );
            case "para-jumbles" -> new StudyMaterialDto(
                    "Reconstructs coherent paragraphs from jumbled sentences using logical connectors and pronoun links.",
                    List.of(
                            new FormulaItem("Mandatory Pair", "Full Name -> Acronym; Noun -> Pronoun (he/they/it)"),
                            new FormulaItem("Opening Sentence", "Independent broad statement introducing the topic"),
                            new FormulaItem("Closing Connectors", "Therefore, Thus, Consequently, In conclusion")
                    ),
                    List.of(
                            "Opener Elimination: Sentences starting with 'However', 'But', 'Also', 'This', or pronouns are almost never the opener.",
                            "Link Mandatory Pairs first, then verify which option contains that exact adjacent pair."
                    ),
                    new WorkedExample(
                            "A: He launched the app in 2024. B: Rohan always wanted to build ed-tech tools.",
                            "Noun 'Rohan' (B) precedes Pronoun 'He' (A) -> Order: BA."
                    )
            );
            case "idioms-and-phrases" -> new StudyMaterialDto(
                    "Tests figurative idiomatic expressions and concise one-word substitutions for descriptive phrases.",
                    List.of(
                            new FormulaItem("-phobia / -phile", "Fear of / Lover of"),
                            new FormulaItem("-cracy / -archy", "Government or rule by"),
                            new FormulaItem("-cide / -logy", "Killing of / Study of")
                    ),
                    List.of(
                            "Figurative Rule: Idioms are metaphorical—eliminate the option that translates the words literally.",
                            "Decode unfamiliar one-word terms by breaking Latin/Greek prefixes and suffixes."
                    ),
                    new WorkedExample(
                            "Meaning of idiom 'To bite the bullet'.",
                            "To endure a painful or difficult situation with courage."
                    )
            );
            case "fill-in-the-blanks" -> new StudyMaterialDto(
                    "Evaluates contextual vocabulary, collocations, and discourse transitions inside single-blank or cloze passages.",
                    List.of(
                            new FormulaItem("Contrast Clues", "Although, However, Yet, Despite -> opposite idea"),
                            new FormulaItem("Support Clues", "Moreover, Furthermore, Since -> matching idea"),
                            new FormulaItem("Preposition Lock", "Check preposition immediately following the blank")
                    ),
                    List.of(
                            "Predict Before Peeking: Read the sentence, guess your own word for the blank, then match the closest option.",
                            "Check part of speech (noun/verb/adjective/adverb) and prepositional collocation immediately."
                    ),
                    new WorkedExample(
                            "Although the project faced delays, the team remained _____ about its success.",
                            "'Although' signals contrast with 'delays' -> positive word: 'optimistic'."
                    )
            );

            // ================= DATA INTERPRETATION (4) =================
            case "tables-and-percentage-share" -> new StudyMaterialDto(
                    "Extracts rows and columns from structured tables to compute totals, market shares, ratios, and YoY growth.",
                    List.of(
                            new FormulaItem("Percentage Share", "(Category / Total) * 100%"),
                            new FormulaItem("YoY Growth %", "((New - Old) / Old) * 100%"),
                            new FormulaItem("Average", "Sum / Count")
                    ),
                    List.of(
                            "10% + 1% Mental Split: Compute 10% and 1% of the denominator mentally to estimate exact percentages in seconds.",
                            "Cross-Multiply Compare: Compare a/b vs c/d by comparing (a*d) vs (c*b) instead of dividing."
                    ),
                    new WorkedExample(
                            "Sales grew from 240 in Q1 to 300 in Q2. Find % growth.",
                            "((300 - 240) / 240) * 100 = (60 / 240) * 100 = 25%."
                    )
            );
            case "bar-and-pie-charts" -> new StudyMaterialDto(
                    "Converts circular pie sectors (degrees/%) and bar heights into comparative ratios and absolute budgets.",
                    List.of(
                            new FormulaItem("Degree to %", "Percentage = Angle / 3.6"),
                            new FormulaItem("% to Degree", "Central Angle = Percentage * 3.6 deg"),
                            new FormulaItem("Sector Value", "(Sector % / 100) * Total")
                    ),
                    List.of(
                            "Skip Total Multiplication for Ratios: Divide two sector percentages or angles directly.",
                            "Anchor: 10% = 36 deg, 25% = 90 deg, 50% = 180 deg."
                    ),
                    new WorkedExample(
                            "Marketing is 25% of Rs. 80,000 budget. Find angle and spend.",
                            "Angle = 25 * 3.6 = 90 deg; Spend = 0.25 * 80,000 = Rs. 20,000."
                    )
            );
            case "line-graphs" -> new StudyMaterialDto(
                    "Tracks multi-period trends, peaks, troughs, steepest growth rates, and year-over-year changes.",
                    List.of(
                            new FormulaItem("Period Change", "Value(t2) - Value(t1)"),
                            new FormulaItem("Steepest % Growth", "Max ((V2 - V1) / V1) * 100"),
                            new FormulaItem("Net Trend Change", "((Final - Initial) / Initial) * 100")
                    ),
                    List.of(
                            "Visual Slope Trick: Steepest upward line = biggest absolute jump; for biggest % jump, check the steepest rise from the smallest starting base.",
                            "Always verify Y-axis scale units (thousands vs lakhs) before computing totals."
                    ),
                    new WorkedExample(
                            "Production rises from 120 in 2022 to 180 in 2023. Find % increase.",
                            "((180 - 120) / 120) * 100 = 50%."
                    )
            );
            default -> new StudyMaterialDto(
                    "Caselets & Mixed DI translate paragraph-based quantitative datasets or dual charts into clean structured tables.",
                    List.of(
                            new FormulaItem("2-Set Venn", "n(A U B) = n(A) + n(B) - n(A n B)"),
                            new FormulaItem("Only A", "n(A) - n(A n B)"),
                            new FormulaItem("Matrix Sum", "Row Totals Sum = Column Totals Sum = Grand Total")
                    ),
                    List.of(
                            "20-Second Grid Rule: Never re-read paragraph caselets for every question—convert all text into a 2x2 table or Venn diagram upfront.",
                            "Verify row sums equal column sums before answering Question 1."
                    ),
                    new WorkedExample(
                            "In a batch of 100 students, 65 like Quant, 55 like Logic, and 30 like both. How many like neither?",
                            "At least one = 65 + 55 - 30 = 90. Neither = 100 - 90 = 10."
                    )
            );
        };
    }
}
