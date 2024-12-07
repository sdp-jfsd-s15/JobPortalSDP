package com.klef.JobPortal.Controller;

import com.klef.JobPortal.dtos.ChatMessage;
import com.klef.JobPortal.model.Chat;
import com.klef.JobPortal.model.ChatHistory;
import com.klef.JobPortal.repository.ChatRepository;
import com.klef.JobPortal.repository.UserRepository;
import com.klef.JobPortal.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

@Controller
public class ChatController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Transactional
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/private")
    public Chat sendMessage(ChatMessage message) {
        if (userRepository.findUserByUserName(message.sendersUserName).isEmpty() ||
                userRepository.findUserByUserName(message.receiversUserName).isEmpty()) {
            throw new IllegalArgumentException("Invalid sender or receiver username.");
        }

        // Fetch or create chat
        Optional<Chat> existingChat = chatRepository.findChatHistory(
                message.sendersUserName, message.receiversUserName).stream().findFirst();

        Chat chat = existingChat.orElseGet(() -> {
            Chat newChat = new Chat();
            newChat.senderUserName = message.sendersUserName;
            newChat.receiverUserName = message.receiversUserName;
            newChat.chatHistory = new ArrayList<>();
            newChat.isActive = true;
            return newChat;
        });

        // Add message if not already present
        String messageId = UUID.randomUUID().toString();
        boolean messageExists = chat.chatHistory.stream()
                .anyMatch(history -> history.messageId.equals(messageId));

        if (!messageExists) {
            ChatHistory chatHistory = new ChatHistory();
            chatHistory.sendersUserName = message.sendersUserName;
            chatHistory.receiversUserName = message.receiversUserName;
            chatHistory.chatMessage = message.chatMessage;
            chatHistory.sentDateAndTime = DateUtils.generateTimeStamp();
            chatHistory.status = "MESSAGE";
            chatHistory.messageId = messageId; // Set the unique message ID

            chat.chatHistory.add(chatHistory);
        }

        // Save chat to the database
        return chatRepository.save(chat);
    }

    @Transactional
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/private")
    public Chat addUser(ChatMessage message) {
        // Validate sender username
        if (userRepository.findUserByUserName(message.sendersUserName).isEmpty()) {
            throw new IllegalArgumentException("Invalid user.");
        }

        // Check if a Chat already exists between the two users
        Optional<Chat> existingChat = chatRepository.findChatHistory(
                message.sendersUserName, message.receiversUserName).stream().findFirst();

        Chat chat;
        if (existingChat.isPresent()) {
            // If chat exists, use the existing Chat object
            chat = existingChat.get();
        } else {
            // If no chat exists, create a new Chat object
            chat = new Chat();
            chat.senderUserName = message.sendersUserName;
            chat.receiverUserName = message.receiversUserName;
            chat.isActive = true;
        }

        // Create a new ChatHistory entry for user joining
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.sendersUserName = message.sendersUserName;
        chatHistory.receiversUserName = message.receiversUserName;
        chatHistory.chatMessage = message.chatMessage;
        chatHistory.sentDateAndTime = DateUtils.generateTimeStamp();
        chatHistory.status = "JOIN";

        // Add the new ChatHistory entry
        if (chat.chatHistory != null) {
            chat.chatHistory.add(chatHistory);
        } else {
            chat.chatHistory = List.of(chatHistory);
        }

        // Save the updated Chat object to the database
        return chatRepository.save(chat);
    }

    @Transactional
    @MessageMapping("/chat.history")
    @SendTo("/topic/private")
    public List<ChatHistory> getChatHistory(ChatMessage message) {
        // Validate sender and receiver usernames
        if (userRepository.findUserByUserName(message.sendersUserName).isEmpty() ||
                userRepository.findUserByUserName(message.receiversUserName).isEmpty()) {
            throw new IllegalArgumentException("Invalid sender or receiver username.");
        }

        // Fetch chat history between the sender and receiver
        List<Chat> chats = chatRepository.findChatHistory(message.sendersUserName, message.receiversUserName);

        if (!chats.isEmpty()) {
            return chats.get(0).chatHistory.stream()
                    .sorted((a, b) -> a.sentDateAndTime.compareTo(b.sentDateAndTime)) // Ensure chronological order
                    .toList();
        }

        return new ArrayList<>();
    }

}
