package com.klef.JobPortal.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table
public class Events {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    public String userName;

    public String eventType;

    public String url;

    public String ipAddress;

    public String httpMethod;

    public String createdAt;

    public Events() {}

}
