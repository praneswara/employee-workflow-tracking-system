package com.example.tmsproject.controller;

import com.example.tmsproject.model.User;
import com.example.tmsproject.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(User user, HttpSession session, Model model) {
        User existing = userService.findByUsername(user.getUsername());
        if (existing != null && existing.getPassword().equals(user.getPassword())) {
            session.setAttribute("loggedUser", existing);
            return existing.getRole().equals("ADMIN") ?
                    "redirect:/admin/dashboard" :
                    "redirect:/user/dashboard";
        }
        model.addAttribute("error", "Invalid Credentials");
        return "login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(User user) {
        user.setRole("USER");
        userService.saveUser(user);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}