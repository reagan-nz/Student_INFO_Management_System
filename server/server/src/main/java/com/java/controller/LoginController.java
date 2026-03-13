package com.java.controller;

import com.java.common.Result;
import com.java.entity.User;
import com.java.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> body, HttpSession session) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            return Result.error("Username and password are required");
        }

        User user = userService.login(username, password);
        if (user == null) {
            return Result.error("Invalid username or password");
        }

        session.setAttribute("user", user);

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        return Result.success(data);
    }

    @PostMapping("/logout")
    public Result<?> logout(HttpSession session) {
        session.invalidate();
        return Result.success();
    }

    @GetMapping("/userinfo")
    public Result<?> userinfo(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "Not logged in");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        return Result.success(data);
    }
}
