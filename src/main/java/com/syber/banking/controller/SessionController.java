package com.syber.banking.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {

    @PostMapping("/login")
    public String login(@RequestParam String username, HttpSession session) {
        session.setAttribute("userId", username);
        return "logged in as " + username;
    }

    @GetMapping("/me")
    public String me(HttpSession session) {
        Object userId = session.getAttribute("userId");
        return userId == null ? "not logged in " : "logged in as " + userId;
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "logged out";
    }

    @GetMapping("/session-id")
    public String sessionId(HttpSession session) {
        return session.getId();
    }


}
