package com.octopus.user_identity_service.enums;

public enum SecurityQuestionType {
    PREDEFINED("Predefined security question"),
    CUSTOM("Custom security question");

    private final String description;

    SecurityQuestionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
