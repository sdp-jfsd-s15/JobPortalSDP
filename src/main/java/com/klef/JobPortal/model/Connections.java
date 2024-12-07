package com.klef.JobPortal.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "connections")
public class Connections {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String userName;

    @ElementCollection
    @CollectionTable(
            name = "connection_info",
            joinColumns = @JoinColumn(name = "connections_id")
    )
    public List<ConnectionInfo> connections;

    public String createdAt;

    public String updatedAt;

    public boolean isActive;
}

