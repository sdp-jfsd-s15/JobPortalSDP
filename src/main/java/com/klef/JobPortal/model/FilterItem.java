package com.klef.JobPortal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor // Required for JPA
@AllArgsConstructor
@Entity
@Table(name = "filter_item")
public class FilterItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String filterName;

    @ManyToOne
    @JoinColumn(name = "filter_id", nullable = false)
    public Filter filter;

    public String getFilterName() {
        return filterName;
    }
}
