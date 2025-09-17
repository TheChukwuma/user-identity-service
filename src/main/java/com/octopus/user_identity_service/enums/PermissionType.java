package com.octopus.user_identity_service.enums;

public enum PermissionType {
    // User Management
    USER_CREATE("Create users"),
    USER_READ("View users"),
    USER_UPDATE("Update users"),
    USER_DELETE("Delete users"),
    
    // Role Management
    ROLE_CREATE("Create roles"),
    ROLE_READ("View roles"),
    ROLE_UPDATE("Update roles"),
    ROLE_DELETE("Delete roles"),
    
    // Permission Management
    PERMISSION_CREATE("Create permissions"),
    PERMISSION_READ("View permissions"),
    PERMISSION_UPDATE("Update permissions"),
    PERMISSION_DELETE("Delete permissions"),
    
    // Device Management
    DEVICE_MANAGE("Manage user devices"),
    DEVICE_VIEW("View user devices"),
    
    // Account Management
    ACCOUNT_MANAGE("Manage user accounts"),
    ACCOUNT_VIEW("View user accounts"),
    
    // Security Questions
    SECURITY_QUESTION_MANAGE("Manage security questions"),
    SECURITY_QUESTION_VIEW("View security questions");

    private final String description;

    PermissionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
