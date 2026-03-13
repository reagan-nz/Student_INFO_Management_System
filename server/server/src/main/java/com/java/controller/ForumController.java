package com.java.controller;

import com.java.common.PageResult;
import com.java.common.Result;
import com.java.entity.ForumPost;
import com.java.entity.ForumReply;
import com.java.entity.User;
import com.java.service.ForumService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    private final ForumService forumService;

    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    @GetMapping
    public Result<PageResult<ForumPost>> list(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(forumService.listPosts(search, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        ForumPost post = forumService.findPostById(id);
        List<ForumReply> replies = forumService.listReplies(id);
        Map<String, Object> data = new HashMap<>();
        data.put("post", post);
        data.put("replies", replies);
        return Result.success(data);
    }

    @PostMapping
    public Result<?> add(@RequestBody ForumPost post, HttpSession session) {
        User user = (User) session.getAttribute("user");
        post.setUserId(user.getId());
        forumService.addPost(post);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody ForumPost post) {
        post.setId(id);
        forumService.updatePost(post);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        forumService.deletePost(id);
        return Result.success();
    }

    @PostMapping("/{id}/replies")
    public Result<?> addReply(@PathVariable Long id, @RequestBody ForumReply reply,
                              HttpSession session) {
        User user = (User) session.getAttribute("user");
        reply.setPostId(id);
        reply.setUserId(user.getId());
        forumService.addReply(reply);
        return Result.success();
    }

    @DeleteMapping("/replies/{replyId}")
    public Result<?> deleteReply(@PathVariable Long replyId) {
        forumService.deleteReply(replyId);
        return Result.success();
    }
}
