package com.klef.JobPortal.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class ConnectionInfo {
    public String userName;
    public String firstName;
    public String lastName;
    public String status;
    public String createdAt;
    public String updatedAt;
}
