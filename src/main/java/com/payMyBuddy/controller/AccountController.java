package com.payMyBuddy.controller;

import com.payMyBuddy.dto.account.AccountCreateDTO;
import com.payMyBuddy.dto.account.AccountResponseDTO;
import com.payMyBuddy.dto.account.BalanceUpdateDTO;
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
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

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
        UserResponseDTO user = userService.findUserById(userId);
        if (user == null) {
            return "redirect:/login";
        }

        List<AccountResponseDTO> accounts = user.getAccounts().stream()
                .sorted(Comparator.comparing(AccountResponseDTO::getCreatedAt).reversed())
                .toList();

        model.addAttribute("createAccount", new AccountCreateDTO());
        model.addAttribute("updateBalance", new BalanceUpdateDTO());
        model.addAttribute("user", user);
        model.addAttribute("accounts", accounts);
        return "accounts";
    }

    @PostMapping("/createAccount")
    public String createAccount(
        @Valid @ModelAttribute("createAccount") AccountCreateDTO newAccount,
        BindingResult bindingResult,
        Model model
    ) {
        Integer userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }
        UserResponseDTO user = userService.findUserById(userId);
        if (user == null) {
            return "redirect:/login";
        }
        if (bindingResult.hasErrors()) {

            List<AccountResponseDTO> accounts = user.getAccounts().stream()
                    .sorted(Comparator.comparing(AccountResponseDTO::getCreatedAt).reversed())
                    .toList();

            model.addAttribute("updateBalance", new BalanceUpdateDTO());
            model.addAttribute("createAccount", newAccount);
            model.addAttribute("accounts", accounts);
            model.addAttribute("user", user);
            return "accounts";
        }

        accountService.createAccount(newAccount, userId);
        return "redirect:/accounts";
    }

    @PutMapping("/accounts/deposit")
    public String showDeposit(
            @Valid @ModelAttribute("updateBalance") BalanceUpdateDTO addedBalance,
            BindingResult bindingResult,
            Model model
    ) {
        Integer userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }
        UserResponseDTO user = userService.findUserById(userId);
        if (user == null) {
            return "redirect:/login";
        }
        if (bindingResult.hasErrors()) {

            List<AccountResponseDTO> accounts = user.getAccounts().stream()
                    .sorted(Comparator.comparing(AccountResponseDTO::getCreatedAt).reversed())
                    .toList();

            model.addAttribute("createAccount", new AccountCreateDTO());
            model.addAttribute("updateBalance", addedBalance);
            model.addAttribute("accounts", accounts);
            model.addAttribute("user", user);
            return "accounts";
        }

        accountService.updateBalanceAccount(addedBalance);
        return "redirect:/accounts";
    }

    @DeleteMapping("/accounts/{accountId}")
    public String deleteAccount(@PathVariable Integer accountId) {
        Integer userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }

        accountService.deleteAccount(accountId);
        return "redirect:/accounts";
    }
}