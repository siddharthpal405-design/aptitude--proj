package com.aptitude.platform.dto;

import java.util.ArrayList;
import java.util.List;

public class StudyMaterialDto {

    public static class FormulaItem {
        private String label;
        private String expression;

        public FormulaItem() {}
        public FormulaItem(String label, String expression) {
            this.label = label;
            this.expression = expression;
        }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public String getExpression() { return expression; }
        public void setExpression(String expression) { this.expression = expression; }
    }

    public static class WorkedExample {
        private String problem;
        private String solution;

        public WorkedExample() {}
        public WorkedExample(String problem, String solution) {
            this.problem = problem;
            this.solution = solution;
        }

        public String getProblem() { return problem; }
        public void setProblem(String problem) { this.problem = problem; }

        public String getSolution() { return solution; }
        public void setSolution(String solution) { this.solution = solution; }
    }

    private String overview;
    private List<FormulaItem> formulas = new ArrayList<>();
    private List<String> shortcuts = new ArrayList<>();
    private WorkedExample workedExample;

    public StudyMaterialDto() {}

    public StudyMaterialDto(String overview, List<FormulaItem> formulas, List<String> shortcuts, WorkedExample workedExample) {
        this.overview = overview;
        this.formulas = formulas;
        this.shortcuts = shortcuts;
        this.workedExample = workedExample;
    }

    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    public List<FormulaItem> getFormulas() { return formulas; }
    public void setFormulas(List<FormulaItem> formulas) { this.formulas = formulas; }

    public List<String> getShortcuts() { return shortcuts; }
    public void setShortcuts(List<String> shortcuts) { this.shortcuts = shortcuts; }

    public WorkedExample getWorkedExample() { return workedExample; }
    public void setWorkedExample(WorkedExample workedExample) { this.workedExample = workedExample; }
}
