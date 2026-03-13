package com.java.service;

import com.java.common.PageResult;
import com.java.entity.ForumPost;
import com.java.entity.ForumReply;
import com.java.mapper.ForumMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumService {

    private final ForumMapper forumMapper;

    public ForumService(ForumMapper forumMapper) {
        this.forumMapper = forumMapper;
    }

    public PageResult<ForumPost> listPosts(String search, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<ForumPost> rows = forumMapper.listPosts(search, offset, pageSize);
        long total = forumMapper.countPosts(search);
        return new PageResult<>(rows, total);
    }

    public ForumPost findPostById(Long id) {
        return forumMapper.findPostById(id);
    }

    public void addPost(ForumPost post) {
        forumMapper.insertPost(post);
    }

    public void updatePost(ForumPost post) {
        forumMapper.updatePost(post);
    }

    public void deletePost(Long id) {
        forumMapper.deleteRepliesByPostId(id);
        forumMapper.deletePost(id);
    }

    public List<ForumReply> listReplies(Long postId) {
        return forumMapper.listRepliesByPostId(postId);
    }

    public void addReply(ForumReply reply) {
        forumMapper.insertReply(reply);
    }

    public void deleteReply(Long id) {
        forumMapper.deleteReply(id);
    }
}
