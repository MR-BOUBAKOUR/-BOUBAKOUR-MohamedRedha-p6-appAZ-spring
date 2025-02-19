package com.payMyBuddy.controller;

import com.payMyBuddy.dto.user.UserResponseDTO;
import com.payMyBuddy.model.User;
import com.payMyBuddy.security.CustomUserDetails;
import com.payMyBuddy.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm(Authentication authentication, Model model) {

        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }

        model.addAttribute("user", new User());
        return "login-form";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        model.addAttribute("email", userDetails.getEmail());

        Optional<UserResponseDTO> user = userService.findUserById(userDetails.getId());
        model.addAttribute("user", user.orElse(null));

        return "dashboard";
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

    @GetMapping("/access-denied")
    public String showAccessDenied() {
        return "access-denied";
    }

}
