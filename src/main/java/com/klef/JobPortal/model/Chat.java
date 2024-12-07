package com.klef.JobPortal.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "chat")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String senderUserName;
    public String receiverUserName;
    public boolean isActive;

    @ElementCollection(fetch = FetchType.EAGER) // Set the fetch type to EAGER to avoid lazy loading issues
    @CollectionTable(
            name = "chat_history",
            joinColumns = @JoinColumn(name = "chat_ids")
    )
    public List<ChatHistory> chatHistory;

    public Chat() {}
}
