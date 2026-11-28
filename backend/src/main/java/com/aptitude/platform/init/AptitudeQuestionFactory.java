package com.aptitude.platform.init;

import com.aptitude.platform.model.Chapter;
import com.aptitude.platform.model.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Facade delegating Study Materials and Question Generation across all 34 aptitude chapters.
 */
public final class AptitudeQuestionFactory {

    private static final Set<String> QUANT_SLUGS = Set.of(
            "percentages", "profit-and-loss", "time-and-work", "number-systems", "ratio-and-proportion",
            "simple-and-compound-interest", "time-speed-and-distance", "averages-and-mixtures",
            "ages-and-partnerships", "permutation-and-combination", "probability",
            "algebra-and-equations", "geometry-and-mensuration", "progressions-and-series"
    );

    private static final Set<String> REASONING_SLUGS = Set.of(
            "blood-relations", "syllogisms", "coding-decoding", "number-and-letter-series",
            "direction-sense", "seating-arrangement", "clocks-and-calendars",
            "order-and-ranking", "data-sufficiency"
    );

    private AptitudeQuestionFactory() {}

    public static String buildStudyMaterialJson(String slug) {
        return StudyMaterialRegistry.buildStudyMaterialJson(slug);
    }

    public static List<Question> generateQuestionsForChapter(Chapter chapter, int neededCount) {
        List<Question> list = new ArrayList<>(neededCount);
        String slug = chapter.getSlug();
        for (int i = 0; i < neededCount; i++) {
            if (QUANT_SLUGS.contains(slug)) {
                list.add(QuantQuestionFactory.create(chapter, slug, i));
            } else if (REASONING_SLUGS.contains(slug)) {
                list.add(ReasoningQuestionFactory.create(chapter, slug, i));
            } else {
                list.add(VerbalDiQuestionFactory.create(chapter, slug, i));
            }
        }
        return list;
    }
}
