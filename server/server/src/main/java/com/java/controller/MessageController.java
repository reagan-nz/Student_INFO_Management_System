package com.java.controller;

import com.java.common.Result;
import com.java.entity.Message;
import com.java.entity.User;
import com.java.service.MessageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/conversations")
    public Result<List<Map<String, Object>>> conversations(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return Result.success(messageService.listConversations(user.getId()));
    }

    @GetMapping("/{otherUserId}")
    public Result<List<Message>> messages(@PathVariable Long otherUserId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        messageService.markAsRead(user.getId(), otherUserId);
        return Result.success(messageService.listMessages(user.getId(), otherUserId));
    }

    @PostMapping
    public Result<?> send(@RequestBody Message message, HttpSession session) {
        User user = (User) session.getAttribute("user");
        message.setSenderId(user.getId());
        messageService.sendMessage(message);
        return Result.success();
    }

    @PutMapping("/read/{otherUserId}")
    public Result<?> markRead(@PathVariable Long otherUserId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        messageService.markAsRead(user.getId(), otherUserId);
        return Result.success();
    }

    @GetMapping("/unread-count")
    public Result<Long> unreadCount(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return Result.success(messageService.countUnread(user.getId()));
    }

    @GetMapping("/users")
    public Result<List<Map<String, Object>>> users() {
        return Result.success(messageService.listAllUsers());
    }
}
