package com.java.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ForumReply {

    private Long id;
    private Long postId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;

    /** Populated by JOIN */
    private String authorName;
}
