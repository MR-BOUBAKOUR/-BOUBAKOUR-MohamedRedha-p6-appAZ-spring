package com.payMyBuddy.controller;

import com.payMyBuddy.dto.transaction.TransactionCreateDTO;
import com.payMyBuddy.dto.transaction.TransactionResponseDTO;
import com.payMyBuddy.dto.account.ReceiversAccountsResponseDTO;
import com.payMyBuddy.dto.user.UserResponseDTO;
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

        UserResponseDTO user = userService.findUserById(userId);
        List<TransactionResponseDTO> transactions = transactionService.findTransactionsForCurrentUser(userId, 0);
        List<ReceiversAccountsResponseDTO> receiversAccounts = accountService.findAccountsForCurrentUserAndHisContacts(userId);

        model.addAttribute("transactionCreate", new TransactionCreateDTO());
        model.addAttribute("receiversAccounts", receiversAccounts);
        model.addAttribute("transactions", transactions);
        model.addAttribute("user", user);
        return "transactions";
    }

    @PostMapping("/createTransaction")
    public String createTransaction(
        @Valid @ModelAttribute("transactionCreate") TransactionCreateDTO transaction,
        BindingResult bindingResult,
        Model model
    ) {

        if (bindingResult.hasErrors()) {
            Integer userId = securityUtils.getCurrentUserId();

            UserResponseDTO user = userService.findUserById(userId);
            List<TransactionResponseDTO> transactions = transactionService.findTransactionsForCurrentUser(userId, 0);
            List<ReceiversAccountsResponseDTO> receiversAccounts = accountService.findAccountsForCurrentUserAndHisContacts(userId);
            
            model.addAttribute("transactionCreate", transaction);
            model.addAttribute("receiversAccounts", receiversAccounts);
            model.addAttribute("transactions", transactions);
            model.addAttribute("user", user);
            return "transactions";
        }

        transactionService.createTransaction(transaction);
        return "redirect:/transactions";
    }
}
