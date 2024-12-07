package com.klef.JobPortal.Controller;

import com.klef.JobPortal.model.Chat;
import com.klef.JobPortal.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/v1/api/chat")
public class ChatRestController {

    @Autowired
    private ChatRepository chatRepository;

    @GetMapping("/history/{sender}/{receiver}")
    public List<Chat> getChatHistory(
            @PathVariable("sender") String sender,
            @PathVariable("receiver") String receiver
    ) {
        // Fetch the chat history sorted by date
        return chatRepository.findChatHistory(sender, receiver);
    }
}
