package com.payMyBuddy.service;

import com.payMyBuddy.dto.account.AccountCreateDTO;
import com.payMyBuddy.dto.user.ContactCreateDTO;
import com.payMyBuddy.dto.user.UserCreateDTO;
import com.payMyBuddy.dto.user.UserPasswordUpdateDTO;
import com.payMyBuddy.dto.user.UserResponseDTO;
import com.payMyBuddy.exception.EmailAlreadyExistException;
import com.payMyBuddy.exception.IncorrectPasswordException;
import com.payMyBuddy.exception.ResourceNotFoundException;
import com.payMyBuddy.mapper.UserMapper;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {

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

    public User findByUserIdInternalUse(Integer userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé."));
    }

    public UserResponseDTO findByUserId(Integer userId) {
        return userRepository.findById(userId)
            .map(userMapper::toUserResponseDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé."));
    }

    public User findUserByEmailInternalUse(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé."));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void createUser(UserCreateDTO newUser) {

        if (existsByEmail(newUser.getEmail())) {
            throw new EmailAlreadyExistException("Adresse email déjà utilisée. Veuillez en choisir une autre.");
        }

        User user = userMapper.toEntityFromCreateDTO(newUser);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        accountService.createAccount(
            new AccountCreateDTO("Pay My Buddy"),
            user.getId()
        );
    }

    public void createContact(Integer userId, ContactCreateDTO contactCreateDTO) {

        User user = findByUserIdInternalUse(userId);
        User contact = findUserByEmailInternalUse(contactCreateDTO.getEmail());

        // bidirectional relation
        user.addContact(contact);

        userRepository.save(user);
        userRepository.save(contact);
    }

    public void deleteContact(Integer userId, Integer contactId) {

        User user = findByUserIdInternalUse(userId);
        User contact = findByUserIdInternalUse(contactId);

        // bidirectional relation
        user.removeContact(contact);

        userRepository.save(user);
        userRepository.save(contact);
    }

    public void updatePasswordByUserId(UserPasswordUpdateDTO userPasswordUpdateDTO, Integer userId) {

        User user = findByUserIdInternalUse(userId);
        if (!passwordEncoder.matches(
            userPasswordUpdateDTO.getActualPassword(),
            user.getPassword()
        )) {
            throw new IncorrectPasswordException("Le mot de passe actuel est incorrect");
        }

        user.setPassword(passwordEncoder.encode(userPasswordUpdateDTO.getNewPassword()));

        userRepository.save(user);
    }
}
