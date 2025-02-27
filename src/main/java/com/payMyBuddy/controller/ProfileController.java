package com.payMyBuddy.controller;

import com.payMyBuddy.dto.transaction.TransactionResponseDTO;
import com.payMyBuddy.dto.user.UserPasswordUpdateDTO;
import com.payMyBuddy.dto.user.UserResponseDTO;
import com.payMyBuddy.security.SecurityUtils;
import com.payMyBuddy.service.TransactionService;
import com.payMyBuddy.service.UserService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ProfileController {

    private final UserService userService;
    private final SecurityUtils securityUtils;

    @Autowired
    public ProfileController(UserService userService, TransactionService transactionService, SecurityUtils securityUtils) {
        this.userService = userService;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        Integer userId = securityUtils.getCurrentUserId();
        UserResponseDTO user = userService.findUserById(userId);

        model.addAttribute("passwordUpdate", new UserPasswordUpdateDTO());
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profileUpdate")
    public String profileUpdate(
            @Valid @ModelAttribute("passwordUpdate") UserPasswordUpdateDTO userPasswordUpdateDTO,
            BindingResult bindingResult,
            Model model
    ) {
        Integer userId = securityUtils.getCurrentUserId();

        if (bindingResult.hasErrors()) {
            UserResponseDTO user = userService.findUserById(userId);

            model.addAttribute("passwordUpdate", userPasswordUpdateDTO);
            model.addAttribute("user", user);
            return "profile";
        }

        userService.updatePasswordByUserId(userPasswordUpdateDTO, userId);
        return "redirect:/profile?success=true";
    }
}