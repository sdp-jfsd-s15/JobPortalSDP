package com.klef.JobPortal.model;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("Admin"),
    PROFESSIONAL("Professional"),
    USER("Users"),
    CUSTOMER_SUPPORT("Customer Support");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

}
