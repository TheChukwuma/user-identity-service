package com.octopus.user_identity_service.enums;

public enum RoleType {
    ADMIN("Administrator with full system access"),
    USER("Regular user with basic access"),
    MODERATOR("User with moderation capabilities"),
    GUEST("Limited access user");

    private final String description;

    RoleType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
