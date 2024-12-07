package com.klef.JobPortal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import com.klef.JobPortal.utils.ListToStringConverter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "job")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String company;
    public String title;
    @Column(columnDefinition = "TEXT")
    public String description;
    @Column(columnDefinition = "TEXT")
    public String qualifications;
    public int vacancy;
    public int appliedApplicantsSize;
    public String location;
    public String type;
    public String category;
    public String experience;
    public String salary;
    public String createdBy;
    public String createdAt;
    public String updatedAt;
    public boolean isPublish;

    @Convert(converter = ListToStringConverter.class)
    public List<String> skills;

    @Convert(converter = ListToStringConverter.class)
    public List<String> filters;

}
