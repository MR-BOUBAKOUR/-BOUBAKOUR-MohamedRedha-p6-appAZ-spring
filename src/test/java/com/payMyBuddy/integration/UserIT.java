package com.payMyBuddy.integration;

import com.payMyBuddy.dto.user.ContactCreateDTO;
import com.payMyBuddy.dto.user.UserCreateDTO;
import com.payMyBuddy.dto.user.UserPasswordUpdateDTO;
import com.payMyBuddy.exception.EmailAlreadyExistException;
import com.payMyBuddy.exception.IncorrectPasswordException;
import com.payMyBuddy.exception.ResourceNotFoundException;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.service.AccountService;
import com.payMyBuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class UserIT {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserCreateDTO user;

    @BeforeEach
    void setUp() {
        user = new UserCreateDTO();
        user.setEmail("user@example.com");
        user.setUsername("user");
        user.setPassword("123123");
        user.setConfirmPassword("123123");
    }

    @Test
    @DisplayName("Création d'un utilisateur")
    void createUser_test() {

        // Given
        assertFalse(userService.existsByEmail(user.getEmail()));

        // When
        userService.createUser(user);

        // Then
        assertTrue(userService.existsByEmail(user.getEmail()));

        User createdUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé."));

        assertEquals(user.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getAccounts(), "Un compte PayMyBuddy est crée automatiquement.");
        assertTrue(passwordEncoder.matches(user.getPassword(), createdUser.getPassword()));

    }

    @Test
    @DisplayName("Création d'un utilisateur avec email existant - Doit lever une exception")
    void createUser_EmailAlreadyExists_test() {

        // Given
        userService.createUser(user);

        // When
        UserCreateDTO duplicateUser = new UserCreateDTO();
        duplicateUser.setEmail(user.getEmail());
        duplicateUser.setUsername("user2");
        duplicateUser.setPassword("456456");
        duplicateUser.setConfirmPassword("456456");

        // Then
        assertThrows(EmailAlreadyExistException.class, () -> {
            userService.createUser(duplicateUser);
        });
    }

    @Test
    @DisplayName("Création de contact")
    void createContact_test() {

        // Given

        UserCreateDTO user1DTO = new UserCreateDTO();
        user1DTO.setUsername("User1");
        user1DTO.setEmail("user1@example.com");
        user1DTO.setPassword("123123");
        user1DTO.setConfirmPassword("123123");

        UserCreateDTO user2DTO = new UserCreateDTO();
        user2DTO.setEmail("user2@example.com");
        user2DTO.setUsername("User2");
        user2DTO.setPassword("456456");
        user2DTO.setConfirmPassword("456456");

        userService.createUser(user1DTO);
        userService.createUser(user2DTO);

        User user1 = userRepository.findByEmail(user1DTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé."));

        ContactCreateDTO contactCreateDTO = new ContactCreateDTO();
        contactCreateDTO.setEmail(user2DTO.getEmail());

        // When
        userService.createContact(user1.getId(), contactCreateDTO);

        User updatedUser = userRepository.findById(user1.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé."));

        // Then

        assertTrue(updatedUser.getContacts().stream()
                .anyMatch(contact -> contact.getEmail().equals(user2DTO.getEmail())));
    }

    @Test
    @DisplayName("Mise à jour de mot de passe")
    void updatePassword_test() {

        // Given
        userService.createUser(user);

        User createdUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé."));

        UserPasswordUpdateDTO passwordUpdateDTO = new UserPasswordUpdateDTO();
        passwordUpdateDTO.setActualPassword("123123");
        passwordUpdateDTO.setNewPassword("789789");
        passwordUpdateDTO.setConfirmNewPassword("789789");

        // When
        userService.updatePasswordByUserId(passwordUpdateDTO, createdUser.getId());

        // Then
        User updatedUser = userRepository.findById(createdUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé."));

        assertTrue(passwordEncoder.matches(
                passwordUpdateDTO.getNewPassword(),
                updatedUser.getPassword()
        ));
    }

    @Test
    @DisplayName("Mise à jour de mot de passe avec mot de passe incorrect - Doit lever une exception")
    void updatePassword_whenNotValid_test() {

        // Given
        userService.createUser(user);

        User createdUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé."));

        UserPasswordUpdateDTO passwordUpdateDTO = new UserPasswordUpdateDTO();
        passwordUpdateDTO.setActualPassword("123123");
        passwordUpdateDTO.setNewPassword("456456");
        passwordUpdateDTO.setConfirmNewPassword("789789");

        // When
        userService.updatePasswordByUserId(passwordUpdateDTO, createdUser.getId());

        // Then
        assertThrows(IncorrectPasswordException.class, () -> {
            userService.updatePasswordByUserId(passwordUpdateDTO, createdUser.getId());
        });
    }
}