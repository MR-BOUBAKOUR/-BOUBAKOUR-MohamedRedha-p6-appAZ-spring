package com.payMyBuddy.controller;

import com.payMyBuddy.dto.user.ContactCreateDTO;
import com.payMyBuddy.dto.user.UserResponseDTO;
import com.payMyBuddy.security.SecurityUtils;
import com.payMyBuddy.service.UserService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
        UserResponseDTO userResponseDTO = userService.findUserById(userId);

        model.addAttribute("createContact", new ContactCreateDTO());
        model.addAttribute("user", userResponseDTO);
        return "contacts";
    }

    @PostMapping("/createContact")
    public String createContact(
            @Valid @ModelAttribute("createContact") ContactCreateDTO contactCreateDTO,
            BindingResult bindingResult,
            Model model
    ) {
        Integer userId = securityUtils.getCurrentUserId();
        UserResponseDTO userResponseDTO = userService.findUserById(userId);

        if (bindingResult.hasErrors()) {
            model.addAttribute("createContact", contactCreateDTO);
            model.addAttribute("user", userResponseDTO);
            return "contacts";
        }

        userService.createContact(userId, contactCreateDTO);
        return "redirect:/contacts";
    }

    @DeleteMapping("/contacts/{contactId}")
    public String deleteAccount(@PathVariable Integer contactId) {
        Integer userId = securityUtils.getCurrentUserId();

        userService.deleteContact(userId, contactId);
        return "redirect:/contacts";
    }
}