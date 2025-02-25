package com.payMyBuddy.controller;

import com.payMyBuddy.dto.transaction.TransactionResponseDTO;
import com.payMyBuddy.dto.user.UserResponseDTO;
import com.payMyBuddy.security.SecurityUtils;
import com.payMyBuddy.service.TransactionService;
import com.payMyBuddy.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
public class DashboardController {

    private final UserService userService;
    private final TransactionService transactionService;
    private final SecurityUtils securityUtils;

    @Autowired
    public DashboardController(UserService userService, TransactionService transactionService, SecurityUtils securityUtils) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        Integer userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            return "redirect:/login";
        }
        UserResponseDTO userResponseDTO = userService.findUserById(userId);
        if (userResponseDTO == null) {
            return "redirect:/login";
        }

        List<TransactionResponseDTO> recentTransactions = transactionService
            .findTransactionsForCurrentUser(userId)
            .stream()
            .sorted(Comparator.comparing(TransactionResponseDTO::getCreatedAt).reversed())
            .limit(5)
            .toList();

        model.addAttribute("user", userResponseDTO);
        model.addAttribute("recentTransactions", recentTransactions);
        return "dashboard";
    }
}