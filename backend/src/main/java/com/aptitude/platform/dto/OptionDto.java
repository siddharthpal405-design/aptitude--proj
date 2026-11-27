package com.aptitude.platform.dto;

import java.util.UUID;

public class OptionDto {
    private UUID id;
    private String optionKey;
    private String optionText;

    public OptionDto() {}

    public OptionDto(UUID id, String optionKey, String optionText) {
        this.id = id;
        this.optionKey = optionKey;
        this.optionText = optionText;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getOptionKey() { return optionKey; }
    public void setOptionKey(String optionKey) { this.optionKey = optionKey; }

    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }
}
