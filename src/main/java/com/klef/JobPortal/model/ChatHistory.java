package com.klef.JobPortal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;

import java.util.UUID;

@Embeddable
public class ChatHistory {
    public String sendersUserName;
    public String receiversUserName;
    @Lob
    @Column(columnDefinition = "TEXT")
    public String chatMessage;
    public String sentDateAndTime;
    public String status;
    public String messageId;

    public ChatHistory() {

    }
}
