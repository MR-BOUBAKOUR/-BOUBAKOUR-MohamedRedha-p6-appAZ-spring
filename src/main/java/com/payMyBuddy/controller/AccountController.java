package com.payMyBuddy.controller;

import com.payMyBuddy.dto.account.AccountCreateDTO;
import com.payMyBuddy.dto.user.UserResponseDTO;
import com.payMyBuddy.security.SecurityUtils;
import com.payMyBuddy.service.AccountService;
import com.payMyBuddy.service.UserService;

import jakarta.validation.Valid;
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

        Optional<UserResponseDTO> optionalUser = userService.findUserById(userId);
        if (optionalUser.isPresent()) {
            UserResponseDTO userResponseDTO = optionalUser.get();
            model.addAttribute("createAccount", new AccountCreateDTO());
            model.addAttribute("user", userResponseDTO);
            model.addAttribute("accounts", userResponseDTO.getAccounts());
            return "accounts";
        }

        return "redirect:/login";
    }

    @GetMapping("/deposit")
    public String showDeposit(Model model) {
        Integer userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<UserResponseDTO> optionalUser = userService.findUserById(userId);
        if (optionalUser.isPresent()) {
            UserResponseDTO userResponseDTO = optionalUser.get();
            model.addAttribute("user", userResponseDTO);
            model.addAttribute("accounts", userResponseDTO.getAccounts());
            return "deposit";
        }

        return "redirect:/login";
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

        if (bindingResult.hasErrors()) {

            Optional<UserResponseDTO> optionalUser = userService.findUserById(userId);
            if (optionalUser.isPresent()) {
                UserResponseDTO userResponseDTO = optionalUser.get();
                model.addAttribute("user", userResponseDTO);
                model.addAttribute("accounts", userResponseDTO.getAccounts());
                return "accounts";
            }

            return "redirect:/login";
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
}