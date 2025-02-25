package com.payMyBuddy.service;

import com.payMyBuddy.dto.account.AccountCreateDTO;
import com.payMyBuddy.dto.user.ContactCreateDTO;
import com.payMyBuddy.dto.user.UserCreateDTO;
import com.payMyBuddy.dto.user.UserResponseDTO;
import com.payMyBuddy.exception.ResourceNotFoundException;
import com.payMyBuddy.mapper.UserMapper;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private final AccountService accountService;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, @Lazy AccountService accountService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();

        this.accountService = accountService;
    }

    public UserResponseDTO findUserById(Integer userId) {
        return userRepository.findById(userId)
            .map(userMapper::toUserResponseDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé."));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void createUser(UserCreateDTO newUser) {
        User user = userMapper.toEntityFromCreateDTO(newUser);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        accountService.createAccount(
                new AccountCreateDTO("Pay My Buddy"),
                user.getId()
        );
    }

    public void createContact(Integer userId, ContactCreateDTO contactCreateDTO) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé."));
        User contact = userRepository.findByEmail(contactCreateDTO.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("Bénéficiaire non trouvé."));

        // bidirectional relation
        user.addContact(contact);

        userRepository.save(user);
        userRepository.save(contact);
    }

    public void deleteContact(Integer userId, Integer contactId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé."));
        User contact = userRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Bénéficiaire non trouvé."));

        // bidirectional relation
        user.removeContact(contact);

        userRepository.save(user);
        userRepository.save(contact);
    }
}
