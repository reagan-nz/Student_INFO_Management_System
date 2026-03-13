package com.java.mapper;

import com.java.entity.ForumPost;
import com.java.entity.ForumReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ForumMapper {

    List<ForumPost> listPosts(@Param("search") String search,
                              @Param("offset") int offset,
                              @Param("pageSize") int pageSize);

    long countPosts(@Param("search") String search);

    ForumPost findPostById(@Param("id") Long id);

    void insertPost(ForumPost post);

    void updatePost(ForumPost post);

    void deletePost(@Param("id") Long id);

    List<ForumReply> listRepliesByPostId(@Param("postId") Long postId);

    void insertReply(ForumReply reply);

    void deleteReply(@Param("id") Long id);

    void deleteRepliesByPostId(@Param("postId") Long postId);
}
