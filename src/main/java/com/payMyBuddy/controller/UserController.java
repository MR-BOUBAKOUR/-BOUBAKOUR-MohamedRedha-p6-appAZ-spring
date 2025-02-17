package com.payMyBuddy.controller;

import com.payMyBuddy.model.User;
import com.payMyBuddy.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    UserService userService;

    @GetMapping("/login")
    public String showLoginForm(Authentication authentication, Model model) {

        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }

        model.addAttribute("user", new User());

        return "login-form";
    }

    @GetMapping("/signup")
    public String showSignupForm(Authentication authentication, Model model) {

        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }

        model.addAttribute("user", new User());

        return "signup-form";
    }

    @PostMapping("/processSignup")
    public String processSignup(@ModelAttribute("user") User user) {
        return "redirect:/login?success=true";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/access-denied")
    public String showAccessDenied() {
        return "access-denied";
    }

}
