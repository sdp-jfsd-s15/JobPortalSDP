package com.klef.JobPortal.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@Entity
@Table(name = "job_applicants")
public class JobApplicants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    public long jobId;

    @Lob
    @Column(columnDefinition = "TEXT")
    public String applicantsJson; // Stores the list of applicants as JSON

    public int applicantSize;

    @Lob
    @Column(columnDefinition = "TEXT")
    public String selectedApplicantsJson; // Stores the selected applicants as JSON

    public int selectedApplicantsSize;

    @Lob
    @Column(columnDefinition = "TEXT")
    public String rejectedApplicantsJson; // Stores the rejected applicants as JSON

    public int rejectedApplicantsSize;

    public String createdAt;
    public String updatedAt;

    public String professional;

    @Transient
    public List<Users> applicants; // Transient list for applicants (not persisted)

    @Transient
    public List<Users> selectedApplicants; // Transient list for selected applicants

    @Transient
    public List<Users> rejectedApplicants; // Transient list for rejected applicants

    // Helper methods for serialization and deserialization
    public void setApplicants(List<Users> applicants) {
        this.applicants = applicants;
        this.applicantsJson = serializeToJson(applicants);
        this.applicantSize = applicants.size();
    }

    public void setSelectedApplicants(List<Users> selectedApplicants) {
        this.selectedApplicants = selectedApplicants;
        this.selectedApplicantsJson = serializeToJson(selectedApplicants);
        this.selectedApplicantsSize = selectedApplicants.size();
    }

    public void setRejectedApplicants(List<Users> rejectedApplicants) {
        this.rejectedApplicants = rejectedApplicants;
        this.rejectedApplicantsJson = serializeToJson(rejectedApplicants);
        this.rejectedApplicantsSize = rejectedApplicants.size();
    }

    public List<Users> getApplicants() {
        if (this.applicants == null && this.applicantsJson != null) {
            this.applicants = deserializeFromJson(this.applicantsJson, new TypeReference<List<Users>>() {});
        }
        return this.applicants;
    }

    public List<Users> getSelectedApplicants() {
        if (this.selectedApplicants == null && this.selectedApplicantsJson != null) {
            this.selectedApplicants = deserializeFromJson(this.selectedApplicantsJson, new TypeReference<List<Users>>() {});
        }
        return this.selectedApplicants;
    }

    public List<Users> getRejectedApplicants() {
        if (this.rejectedApplicants == null && this.rejectedApplicantsJson != null) {
            this.rejectedApplicants = deserializeFromJson(this.rejectedApplicantsJson, new TypeReference<List<Users>>() {});
        }
        return this.rejectedApplicants;
    }

    private String serializeToJson(List<Users> users) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(users);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize to JSON", e);
        }
    }

    private <T> T deserializeFromJson(String json, TypeReference<T> typeReference) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, typeReference);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize from JSON", e);
        }
    }
}
