package com.java.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ForumPost {

    private Long id;
    private Long userId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** Populated by JOIN */
    private String authorName;
    private int replyCount;
}
