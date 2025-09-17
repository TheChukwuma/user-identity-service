package com.octopus.user_identity_service.enums;

public enum AccountType {
    CHECKING("Checking Account"),
    SAVINGS("Savings Account"),
    BUSINESS("Business Account"),
    CREDIT("Credit Account"),
    LOAN("Loan Account");

    private final String description;

    AccountType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
