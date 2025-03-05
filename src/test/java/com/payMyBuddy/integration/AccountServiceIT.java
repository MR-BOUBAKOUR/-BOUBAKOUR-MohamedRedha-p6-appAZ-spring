package com.payMyBuddy.integration;

import com.payMyBuddy.dto.account.AccountCreateDTO;
import com.payMyBuddy.dto.account.BalanceUpdateDTO;
import com.payMyBuddy.dto.account.ReceiversAccountsResponseDTO;
import com.payMyBuddy.dto.user.ContactCreateDTO;
import com.payMyBuddy.dto.user.UserCreateDTO;
import com.payMyBuddy.exception.ConflictException;
import com.payMyBuddy.exception.ResourceNotFoundException;
import com.payMyBuddy.model.Account;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.AccountRepository;
import com.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.service.AccountService;
import com.payMyBuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class AccountServiceIT {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountRepository accountRepository;

    private User user;
    private AccountCreateDTO createdAccount;

    @BeforeEach
    void setUp() {

        // Given
        UserCreateDTO createdUser = new UserCreateDTO();
        createdUser.setEmail("user@example.com");
        createdUser.setUsername("user");
        createdUser.setPassword("123123");
        createdUser.setConfirmPassword("123123");

        createdAccount = new AccountCreateDTO();
        createdAccount.setName("Test Account");

        userService.createUser(createdUser);
        user = userService.findByUserEmailInternalUse(createdUser.getEmail());

    }

    @Test
    @DisplayName("Création d'un compte")
    void createAccount_test() {

        // When
        accountService.createAccount(createdAccount, user.getId());
        Set<Account> userAccounts = accountRepository.findByUserId(user.getId());

        Account targetAccount = userAccounts.stream()
                .filter(account -> account.getName().equals("Test Account"))
                .findFirst()
                .orElse(null);

        // Then
        assertFalse(userAccounts.isEmpty(), "Un compte doit être créé pour l'utilisateur");

        assert targetAccount != null;

        assertEquals(createdAccount.getName(), targetAccount.getName());
        assertEquals(BigDecimal.ZERO, targetAccount.getBalance());
        assertNotNull(targetAccount.getCreatedAt());
    }

    @Test
    @DisplayName("Création de compte avec un nom existant - Doit lever une exception")
    void createAccount_withExistingName_test() {

        accountService.createAccount(createdAccount, user.getId());

        // When
        AccountCreateDTO duplicateAccount = new AccountCreateDTO();
        duplicateAccount.setName("Test Account");

        // Then
        assertThrows(ConflictException.class, () -> {
            accountService.createAccount(duplicateAccount, user.getId());
        });
    }

    @Test
    @DisplayName("Suppression d'un compte")
    void deleteAccount_test() {

        // Given
        accountService.createAccount(createdAccount, user.getId());
        Set<Account> userAccounts = accountRepository.findByUserId(user.getId());

        Account targetAccount = userAccounts.stream()
                .filter(account -> account.getName().equals("Test Account"))
                .findFirst()
                .orElse(null);
        assert targetAccount != null;

        // When
        accountService.deleteAccount(targetAccount.getId());

        // Then
        assertFalse(accountRepository.findByUserId(user.getId()).contains(targetAccount));
    }

    @Test
    @DisplayName("Suppression d'un compte inexistant - Doit lever une exception")
    void deleteAccount_notFound_test() {
        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> {
            accountService.deleteAccount(9999);
        });
    }

    @Test
    @DisplayName("Mise à jour du solde d'un compte")
    void updateBalanceAccount_test() {

        // Given
        accountService.createAccount(createdAccount, user.getId());
        Set<Account> userAccounts = accountRepository.findByUserId(user.getId());

        Account targetAccount = userAccounts.stream()
                .filter(account -> account.getName().equals("Test Account"))
                .findFirst()
                .orElse(null);
        assert targetAccount != null;

        BalanceUpdateDTO balanceUpdateDTO = new BalanceUpdateDTO();
        balanceUpdateDTO.setAccountId(targetAccount.getId());
        balanceUpdateDTO.setAmount(BigDecimal.valueOf(100.00));

        // When
        accountService.updateBalanceAccount(balanceUpdateDTO);

        // Then
        Account updatedAccount = accountRepository.findById(targetAccount.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé."));

        assertEquals(BigDecimal.valueOf(100.00), updatedAccount.getBalance());
    }

    @Test
    @DisplayName("Recherche des comptes de l'utilisateur et de ses contacts")
    void findAccountsForCurrentUserAndHisContacts_test() {

        // Given
            // Create a second user and account
        UserCreateDTO createdContact = new UserCreateDTO();
        createdContact.setEmail("contact@example.com");
        createdContact.setUsername("contact");
        createdContact.setPassword("456456");
        createdContact.setConfirmPassword("456456");

        userService.createUser(createdContact);
        User contact = userService.findByUserEmailInternalUse(createdContact.getEmail());

            // Add contact
        userService.createContact(user.getId(), new ContactCreateDTO(contact.getEmail()));

            // Create accounts for both users
        AccountCreateDTO createdContactAccount = new AccountCreateDTO();
        createdContactAccount.setName("Contact Account");

        accountService.createAccount(createdAccount, user.getId());
        accountService.createAccount(createdContactAccount, contact.getId());

        // When
        List<ReceiversAccountsResponseDTO> receiversAccounts =
                accountService.findAccountsForCurrentUserAndHisContacts(user.getId());

        // Then
        assertEquals(2, receiversAccounts.size());
        assertTrue(receiversAccounts.stream()
                .anyMatch(account -> account.getOwnerName().equals(user.getUsername())));
        assertTrue(receiversAccounts.stream()
                .anyMatch(account -> account.getOwnerName().equals(contact.getUsername())));
    }
}