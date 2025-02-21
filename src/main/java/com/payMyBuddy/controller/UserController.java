/*
package com.payMyBuddy.controller;


import com.payMyBuddy.dto.account.AccountCreateDTO;
import com.payMyBuddy.dto.user.UserCreateDTO;
import com.payMyBuddy.dto.user.UserResponseDTO;
import com.payMyBuddy.model.Account;
import com.payMyBuddy.model.User;
import com.payMyBuddy.security.CustomUserDetails;
import com.payMyBuddy.service.AccountService;
import com.payMyBuddy.service.UserService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;
import java.util.Set;

@Controller
public class UserController {

    private final UserService userService;
    private final AccountService accountService;

    @Autowired
    public UserController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @GetMapping("/login")
    public String showLoginForm(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }

        model.addAttribute("user", new User());
        return "login-form";
    }

    @GetMapping("/signup")
    public String showSignupForm(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }

        model.addAttribute("user", new UserCreateDTO());
        return "signup-form";
    }

    @PostMapping("/processSignup")
    public String processSignup(
            @Valid @ModelAttribute("user") UserCreateDTO userCreateDTO,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "signup-form";
        }

        userService.saveUser(userCreateDTO);
        return "redirect:/login?success=true";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        model.addAttribute("email", userDetails.getEmail());

        Optional<UserResponseDTO> user = userService.findUserById(userDetails.getId());

        model.addAttribute("user", user.orElse(null));

        return "dashboard";
    }

    @GetMapping("/accounts")
    public String showAccounts(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        model.addAttribute("email", userDetails.getEmail());

        Optional<UserResponseDTO> optionalUser = userService.findUserById(userDetails.getId());
        if (optionalUser.isPresent()) {
            UserResponseDTO user = optionalUser.get();
            Set<Account> accounts = user.getAccounts();

            model.addAttribute("user", user);
            model.addAttribute("accounts", accounts);
            model.addAttribute("createAccount", new AccountCreateDTO());

        return "accounts";
        }

        return "redirect:/login";
    }

    @PostMapping("/createAccount")
    public String createAccount(
            @Valid @ModelAttribute("createAccount") AccountCreateDTO accountCreateDTO,
            BindingResult bindingResult,
            Model model
    ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        model.addAttribute("email", userDetails.getEmail());

        Optional<UserResponseDTO> optionalUser = userService.findUserById(userDetails.getId());
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }
        UserResponseDTO user = optionalUser.get();

        model.addAttribute("user", user);
        model.addAttribute("accounts", user.getAccounts());

        // If there are validation errors, return to the form with the populated model
        if (bindingResult.hasErrors()) {
            return "accounts";
        }

        accountService.createAccount(accountCreateDTO, userDetails.getId());

        return "redirect:/accounts";

    }

    @GetMapping("/access-denied")
    public String showAccessDenied() {
        return "access-denied";
    }

}

 */