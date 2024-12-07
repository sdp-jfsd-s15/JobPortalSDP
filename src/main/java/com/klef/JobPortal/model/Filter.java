package com.klef.JobPortal.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Builder
@NoArgsConstructor // Required for JPA
@AllArgsConstructor
@Entity
@Table(name = "filter")
public class Filter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String category;

    @OneToMany(mappedBy = "filter", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<FilterItem> filterItems;

    public List<FilterItem> getFilterItems() {
        return filterItems;
    }
}
