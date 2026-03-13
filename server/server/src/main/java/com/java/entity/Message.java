package com.java.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Message {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private Integer isRead;
    private LocalDateTime createdAt;

    /** Populated by JOIN */
    private String senderName;
    private String receiverName;
}
