package com.payMyBuddy.service;

import com.payMyBuddy.dto.user.ContactCreateDTO;
import com.payMyBuddy.dto.user.UserCreateDTO;
import com.payMyBuddy.dto.user.UserResponseDTO;
import com.payMyBuddy.exception.ResourceNotFoundException;
import com.payMyBuddy.mapper.UserMapper;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public UserResponseDTO findUserById(Integer id) {
        return userRepository.findById(id)
            .map(userMapper::toResponseDTO)
            .orElse(null);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void createUser(UserCreateDTO userCreateDTO) {
        User user = userMapper.toEntityFromCreateDTO(userCreateDTO);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
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
