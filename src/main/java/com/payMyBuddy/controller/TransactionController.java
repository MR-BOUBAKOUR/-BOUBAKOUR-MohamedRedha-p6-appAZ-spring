package com.payMyBuddy.controller;

import com.payMyBuddy.dto.account.AccountCreateDTO;
import com.payMyBuddy.dto.account.BalanceUpdateDTO;
import com.payMyBuddy.dto.transaction.TransactionCreateDTO;
import com.payMyBuddy.dto.transaction.TransactionResponseDTO;
import com.payMyBuddy.dto.user.UserResponseDTO;
import com.payMyBuddy.dto.account.AccountResponseDTO;
import com.payMyBuddy.security.SecurityUtils;
import com.payMyBuddy.service.TransactionService;
import com.payMyBuddy.service.UserService;
import com.payMyBuddy.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TransactionController {

    private final UserService userService;
    private final TransactionService transactionService;
    private final AccountService accountService;
    private final SecurityUtils securityUtils;

    @GetMapping("/transactions")
    public String showTransactions(Model model) {
        Integer userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }
        UserResponseDTO userResponseDTO = userService.findUserById(userId);
        if (userResponseDTO == null) {
            return "redirect:/login";
        }

        List<TransactionResponseDTO> transactions = transactionService.findTransactionsForCurrentUser(userId);
        List<AccountResponseDTO> contactsAccounts = accountService.findContactsAccountsForUser(userId);

        model.addAttribute("transactionCreate", new TransactionCreateDTO());
        model.addAttribute("transactions", transactions);
        model.addAttribute("contactsAccounts", contactsAccounts);
        model.addAttribute("user", userResponseDTO);
        return "transactions";
    }

    @PostMapping("/createTransaction")
    public String createTransaction(
            @Valid @ModelAttribute("transactionCreate") TransactionCreateDTO transactionCreateDTO,
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
            List<TransactionResponseDTO> transactions = transactionService.findTransactionsForCurrentUser(userId);
            List<AccountResponseDTO> contactsAccounts = accountService.findContactsAccountsForUser(userId);

            model.addAttribute("transactions", transactions);
            model.addAttribute("contactsAccounts", contactsAccounts);
            model.addAttribute("transactionCreate", transactionCreateDTO);
            model.addAttribute("user", userResponseDTO);
            return "transactions";
        }

        transactionService.createTransaction(userId, transactionCreateDTO);
        return "redirect:/transactions";
    }
}
