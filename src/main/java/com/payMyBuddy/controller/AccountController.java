package com.payMyBuddy.controller;

import com.payMyBuddy.dto.account.AccountCreateDTO;
import com.payMyBuddy.dto.user.UserResponseDTO;
import com.payMyBuddy.security.SecurityUtils;
import com.payMyBuddy.service.AccountService;
import com.payMyBuddy.service.UserService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final UserService userService;
    private final AccountService accountService;
    private final SecurityUtils securityUtils;

    @Autowired
    public AccountController(UserService userService, AccountService accountService, SecurityUtils securityUtils) {
        this.userService = userService;
        this.accountService = accountService;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/accounts")
    public String showAccounts(Model model) {
        Integer userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }
        UserResponseDTO userResponseDTO = userService.findUserById(userId);
        if (userResponseDTO == null) {
            return "redirect:/login";
        }

        model.addAttribute("createAccount", new AccountCreateDTO());
        model.addAttribute("user", userResponseDTO);
        return "accounts";
    }

    @PostMapping("/createAccount")
    public String createAccount(
            @Valid @ModelAttribute("createAccount") AccountCreateDTO accountCreateDTO,
            BindingResult bindingResult,
            Model model
    ) {
        Integer userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }
        UserResponseDTO userResponseDTO = userService.findUserById(userId);
        if (userResponseDTO == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userResponseDTO);
            return "accounts";
        }

        accountService.createAccount(accountCreateDTO, userId);
        return "redirect:/accounts";
    }

    @PostMapping("/account/delete/{id}")
    public String deleteAccount(@PathVariable Integer id) {
        Integer userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }

        accountService.deleteAccount(id);
        return "redirect:/accounts";
    }

    @GetMapping("/deposit")
    public String showDeposit(Model model) {
        Integer userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }
        UserResponseDTO userResponseDTO = userService.findUserById(userId);
        if (userResponseDTO == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", userResponseDTO);
        return "deposit";
    }
}