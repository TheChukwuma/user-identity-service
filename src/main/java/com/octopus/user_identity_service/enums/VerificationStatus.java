package com.octopus.user_identity_service.enums;

public enum VerificationStatus {
    VERIFIED("Account verified"),
    PENDING("Verification pending"),
    REJECTED("Verification rejected"),
    REQUIRED("Verification required");

    private final String description;

    VerificationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
