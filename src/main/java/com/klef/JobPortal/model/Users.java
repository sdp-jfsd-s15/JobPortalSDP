package com.klef.JobPortal.model;

import jakarta.persistence.*;
import lombok.*;
import com.klef.JobPortal.model.Role;
import com.klef.JobPortal.model.ContactInfo;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long user_id;

    @Column(insertable=false, updatable=false) // Only one definition of email
    public String email;

    @Column(nullable = false, length = 255, unique = true)
    public String userName;

    @Column(nullable = false, length = 20)
    public String firstName;

    @Column(length = 20)
    public String middleName;

    @Column(nullable = false, length = 20)
    public String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public Role role;

    @Lob
    @Column(columnDefinition = "TEXT")
    public String summary;

    @Column(length = 20)
    public String gender;

    @Lob
    @Column(columnDefinition = "TEXT")
    public String about;

    @Column(length = 500)
    public String resumeUrl;

    @Column(length = 500)
    public String profileImageUrl;

    @Column(length = 500)
    public String backgroundImageUrl;

    public String createdAt;

    public String updatedAt;

    public boolean isActive;

    @Column()
    public int connectionsCount;

    @Embedded
    public ContactInfo contactInfo;

    public String location;

    @ElementCollection
    @CollectionTable(name = "user_applied_jobs", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "job_id")
    public List<Long> appliedJobs;
}

