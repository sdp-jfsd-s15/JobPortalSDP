package com.klef.JobPortal.repository;

import com.klef.JobPortal.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("""
           SELECT c FROM Chat c
           JOIN c.chatHistory ch
           WHERE (c.senderUserName = :user1 AND c.receiverUserName = :user2)
              OR (c.senderUserName = :user2 AND c.receiverUserName = :user1)
           ORDER BY ch.sentDateAndTime ASC
           """)
    List<Chat> findChatHistory(@Param("user1") String user1, @Param("user2") String user2);
}
