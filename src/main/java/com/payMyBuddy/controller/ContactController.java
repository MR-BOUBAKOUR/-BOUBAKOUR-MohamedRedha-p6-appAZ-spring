package com.payMyBuddy.controller;

import com.payMyBuddy.dto.user.UserResponseDTO;
import com.payMyBuddy.security.SecurityUtils;
import com.payMyBuddy.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContactController {

    private final UserService userService;
    private final SecurityUtils securityUtils;

    @Autowired
    public ContactController(UserService userService, SecurityUtils securityUtils) {
        this.userService = userService;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/contacts")
    public String showContacts(Model model) {
        Integer userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }

        UserResponseDTO userResponseDTO = userService.findUserById(userId);

        model.addAttribute("user", userResponseDTO);
        return "contacts";
    }
}