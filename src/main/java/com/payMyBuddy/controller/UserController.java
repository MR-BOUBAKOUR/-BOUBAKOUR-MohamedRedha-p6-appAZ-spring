package com.payMyBuddy.controller;

import com.payMyBuddy.dto.user.UserCreateDTO;
import com.payMyBuddy.dto.user.UserResponseDTO;
import com.payMyBuddy.model.User;
import com.payMyBuddy.security.CustomUserDetails;
import com.payMyBuddy.service.UserService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    @GetMapping("/signup")
    public String showSignupForm(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }

        model.addAttribute("user", new UserCreateDTO());
        return "signup-form";
    }

    @PostMapping("/processSignup")
    public String processSignup(
            @Valid @ModelAttribute("user") UserCreateDTO userCreateDTO,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "signup-form";
        }

        userService.saveUser(userCreateDTO);
        return "redirect:/login?success=true";
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

    @GetMapping("/access-denied")
    public String showAccessDenied() {
        return "access-denied";
    }

}

/*
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    logger.info("-------------------------------------------------------------------------");
    logger.info(userCreateDTO.toString());
    logger.info("-------------------------------------------------------------------------");
*/