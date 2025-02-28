package com.payMyBuddy.controller;

import com.payMyBuddy.dto.account.AccountCreateDTO;
import com.payMyBuddy.dto.account.BalanceUpdateDTO;
import com.payMyBuddy.dto.user.UserResponseDTO;
import com.payMyBuddy.security.CustomUserDetailsService;
import com.payMyBuddy.security.SecurityConfig;
import com.payMyBuddy.security.SecurityUtils;
import com.payMyBuddy.service.AccountService;
import com.payMyBuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AccountController.class)
@Import(SecurityConfig.class)
@WithMockUser
class AccountControllerTest {

    @MockitoBean
    private AccountService accountService;
    @MockitoBean
    private UserService userService;

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private SecurityUtils securityUtils;
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();
    }

    @Test
    void showAccounts_shouldDisplayAccountsPage() throws Exception {
        // Given
        Integer userId = 1;
        UserResponseDTO user = new UserResponseDTO();
        user.setAccounts(Set.of());
        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(userService.findUserById(userId)).thenReturn(user);

        // When & Then
        mockMvc.perform(get("/accounts"))
            .andExpect(status().isOk())
            .andExpect(view().name("accounts"))
            .andExpect(model().attributeExists("accounts", "user"));
    }

    @Test
    void createAccount_whenValidData_shouldCreateAccountAndRedirect() throws Exception {
        // Given
        Integer userId = 1;
        when(securityUtils.getCurrentUserId()).thenReturn(userId);

        // When & Then
        doNothing().when(accountService).createAccount(any(AccountCreateDTO.class), eq(userId));

        mockMvc.perform(post("/createAccount")
                .param("name", "Compte Test")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/accounts"));
    }

    @Test
    void createAccount_whenValidationErrors_shouldReturnToAccountsPage() throws Exception {
        // Given
        Integer userId = 1;
        UserResponseDTO user = new UserResponseDTO();
        user.setAccounts(Set.of());

        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(userService.findUserById(userId)).thenReturn(user);

        // When & Then
        mockMvc.perform(post("/createAccount")
                .param("name", "")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isOk())
            .andExpect(view().name("accounts"))
            .andExpect(model().attributeExists("accounts", "user", "createAccount", "updateBalance"))
            .andExpect(model().attributeHasFieldErrors("createAccount", "name"));
    }

    @Test
    void createDeposit_whenValidData_shouldUpdateBalanceAndRedirect() throws Exception {
        // Given
        Integer userId = 1;
        UserResponseDTO user = new UserResponseDTO();
        user.setAccounts(Set.of());

        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(userService.findUserById(userId)).thenReturn(user);

        // When & Then
        doNothing().when(accountService).updateBalanceAccount(any(BalanceUpdateDTO.class));

        mockMvc.perform(put("/accounts/deposit")
                .param("amount", "100")
                .param("accountId", "1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/accounts"));
    }

    @Test
    void createDeposit_whenValidationErrors_shouldReturnToAccountsPage() throws Exception {
        // Given
        Integer userId = 1;
        UserResponseDTO user = new UserResponseDTO();
        user.setAccounts(Set.of());

        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(userService.findUserById(userId)).thenReturn(user);

        // When & Then
        mockMvc.perform(put("/accounts/deposit")
                .param("amount", "-100")
                .param("accountId", "1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isOk())
            .andExpect(view().name("accounts"))
            .andExpect(model().attributeExists("accounts", "user", "createAccount", "updateBalance"))
            .andExpect(model().attributeHasFieldErrors("updateBalance", "amount"));
    }

    @Test
    void deleteAccount_shouldDeleteAccountAndRedirect() throws Exception {
        // Given
        Integer accountId = 1;
        doNothing().when(accountService).deleteAccount(accountId);

        // When & Then
        mockMvc.perform(delete("/accounts/{accountId}", accountId)
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/accounts"));
    }

}