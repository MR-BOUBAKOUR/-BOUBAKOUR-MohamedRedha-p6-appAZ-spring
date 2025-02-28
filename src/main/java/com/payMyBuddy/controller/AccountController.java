package com.payMyBuddy.controller;

import com.payMyBuddy.dto.account.AccountCreateDTO;
import com.payMyBuddy.dto.account.AccountResponseDTO;
import com.payMyBuddy.dto.account.BalanceUpdateDTO;
import com.payMyBuddy.dto.user.UserResponseDTO;
import com.payMyBuddy.security.SecurityUtils;
import com.payMyBuddy.service.AccountService;
import com.payMyBuddy.service.UserService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

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
        UserResponseDTO user = userService.findUserById(userId);

        prepareAccountsAndModels(model, user, new AccountCreateDTO(), new BalanceUpdateDTO());
        return "accounts";
    }

    @PostMapping("/createAccount")
    public String createAccount(
        @Valid @ModelAttribute("createAccount") AccountCreateDTO newAccount,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        Integer userId = securityUtils.getCurrentUserId();
        UserResponseDTO user = userService.findUserById(userId);

        if (bindingResult.hasErrors()) {
            prepareAccountsAndModels(model, user, newAccount, new BalanceUpdateDTO());
            return "accounts";
        }

        accountService.createAccount(newAccount, userId);
        redirectAttributes.addFlashAttribute("successMessage", "Compte créé avec succès !");
        return "redirect:/accounts";
    }

    @PutMapping("/accounts/deposit")
    public String createDeposit(
            @Valid @ModelAttribute("updateBalance") BalanceUpdateDTO addedBalance,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Integer userId = securityUtils.getCurrentUserId();
        UserResponseDTO user = userService.findUserById(userId);

        if (bindingResult.hasErrors()) {
            prepareAccountsAndModels(model, user, new AccountCreateDTO(), addedBalance);
            return "accounts";
        }

        accountService.updateBalanceAccount(addedBalance);
        redirectAttributes.addFlashAttribute("successMessage", "Dépôt effectué avec succès !");
        return "redirect:/accounts";
    }

    @DeleteMapping("/accounts/{accountId}")
    public String deleteAccount(
        @PathVariable Integer accountId,
        RedirectAttributes redirectAttributes
    ) {
        accountService.deleteAccount(accountId);
        redirectAttributes.addFlashAttribute("successMessage", "Compte supprimé avec succès !");
        return "redirect:/accounts";
    }

    private void prepareAccountsAndModels(
        Model model,
        UserResponseDTO user,
        AccountCreateDTO createAccount,
        BalanceUpdateDTO updateBalance
    ) {

        List<AccountResponseDTO> accounts = user.getAccounts().stream()
                .sorted(Comparator.comparing(AccountResponseDTO::getCreatedAt).reversed())
                .toList();

        model.addAttribute("createAccount", createAccount);
        model.addAttribute("updateBalance", updateBalance);
        model.addAttribute("accounts", accounts);
        model.addAttribute("user", user);
    }
}