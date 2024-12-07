package com.klef.JobPortal.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class AddAttachments {
    private String fileName;        // e.g., "resume.pdf"
    private Double fileSize;        // e.g., 12.5 (in KB or MB)
    private String fileOpenUrl;     // e.g., "https://mydress.com/resume.pdf"
    private String thumbNailUrl;    // e.g., "https://mydress.com/resume_thumbnail.jpeg"
    private String mimeType;        // e.g., "application/pdf"
    private String fileExtension;   // e.g., "pdf"
    private String s3Key;           // e.g., "product/123_resume.pdf"
}
