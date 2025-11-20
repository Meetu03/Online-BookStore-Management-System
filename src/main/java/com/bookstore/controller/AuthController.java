package com.bookstore.controller;

import com.bookstore.model.User;
import com.bookstore.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, HttpSession session) {
        // default role is set in service to USER
        User saved = service.register(user);
        session.setAttribute("userId", saved.getId());
        session.setAttribute("userName", saved.getName());
        session.setAttribute("userRole", saved.getRole());
        return "redirect:/books";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        Model model,
                        HttpSession session) {

        return service.login(email, password)
                .map(u -> {
                    session.setAttribute("userId", u.getId());
                    session.setAttribute("userName", u.getName());
                    session.setAttribute("userRole", u.getRole());
                    return "redirect:/books";
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Invalid email or password.");
                    return "login";
                });
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
