package com.octopus.user_identity_service.enums;

public enum AccountStatus {
    ACTIVE("Active account"),
    INACTIVE("Inactive account"),
    SUSPENDED("Suspended account"),
    CLOSED("Closed account"),
    PENDING("Pending activation");

    private final String description;

    AccountStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
