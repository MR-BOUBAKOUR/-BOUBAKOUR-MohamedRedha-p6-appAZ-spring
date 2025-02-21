package com.payMyBuddy.service;

import com.payMyBuddy.dto.user.UserCreateDTO;
import com.payMyBuddy.dto.user.UserResponseDTO;
import com.payMyBuddy.mapper.UserMapper;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public Optional<UserResponseDTO> findUserById(Integer id) {
        return userRepository.findById(id).map(userMapper::toResponseDTO);
    }

    public void createUser(UserCreateDTO userCreateDTO) {
        User user = userMapper.toEntityFromCreateDTO(userCreateDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
    }
}
