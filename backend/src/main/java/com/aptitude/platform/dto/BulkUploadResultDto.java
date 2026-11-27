package com.aptitude.platform.dto;

import java.util.ArrayList;
import java.util.List;

public class BulkUploadResultDto {
    private int totalProcessed;
    private int successfullyAdded;
    private int skippedCount;
    private List<String> errors = new ArrayList<>();

    public BulkUploadResultDto() {}

    public BulkUploadResultDto(int totalProcessed, int successfullyAdded, int skippedCount, List<String> errors) {
        this.totalProcessed = totalProcessed;
        this.successfullyAdded = successfullyAdded;
        this.skippedCount = skippedCount;
        this.errors = errors;
    }

    public int getTotalProcessed() { return totalProcessed; }
    public void setTotalProcessed(int totalProcessed) { this.totalProcessed = totalProcessed; }

    public int getSuccessfullyAdded() { return successfullyAdded; }
    public void setSuccessfullyAdded(int successfullyAdded) { this.successfullyAdded = successfullyAdded; }

    public int getSkippedCount() { return skippedCount; }
    public void setSkippedCount(int skippedCount) { this.skippedCount = skippedCount; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}
