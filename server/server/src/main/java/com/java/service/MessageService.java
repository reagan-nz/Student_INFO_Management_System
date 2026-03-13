package com.java.service;

import com.java.entity.Message;
import com.java.mapper.MessageMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MessageService {

    private final MessageMapper messageMapper;

    public MessageService(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    public List<Map<String, Object>> listConversations(Long userId) {
        return messageMapper.listConversations(userId);
    }

    public List<Message> listMessages(Long userId, Long otherUserId) {
        return messageMapper.listMessages(userId, otherUserId);
    }

    public long countUnread(Long userId) {
        return messageMapper.countUnread(userId);
    }

    public void sendMessage(Message message) {
        messageMapper.insertMessage(message);
    }

    public void markAsRead(Long userId, Long otherUserId) {
        messageMapper.markAsRead(userId, otherUserId);
    }

    public List<Map<String, Object>> listAllUsers() {
        return messageMapper.listAllUsers();
    }
}
