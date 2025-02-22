package com.payMyBuddy.controller;

import com.payMyBuddy.dto.user.UserCreateDTO;
import com.payMyBuddy.dto.user.UserLoginDTO;
import com.payMyBuddy.security.SecurityUtils;
import com.payMyBuddy.service.UserService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;
    private final SecurityUtils securityUtils;

    @Autowired
    public AuthController(UserService userService, SecurityUtils securityUtils) {
        this.userService = userService;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        if (securityUtils.getCurrentUserId() != null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("user", new UserLoginDTO());
        return "login-form";
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        if (securityUtils.getCurrentUserId() != null) {
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
        if (securityUtils.getCurrentUserId() != null) {
            return "redirect:/dashboard";
        }

        if (bindingResult.hasErrors()) {
            return "signup-form";
        }

        if (userService.existsByEmail(userCreateDTO.getEmail())) {
            bindingResult.rejectValue(
                    "email",
                    "error.user",
                    "Adresse email déjà utilisée. Veuillez en choisir une autre."
            );
            return "signup-form";
        }

        userService.createUser(userCreateDTO);
        return "redirect:/login?success=true";
    }
}