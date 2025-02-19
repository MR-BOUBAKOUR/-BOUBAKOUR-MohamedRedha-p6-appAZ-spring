package com.payMyBuddy.service;

import com.payMyBuddy.dto.user.UserResponseDTO;
import com.payMyBuddy.mapper.UserMapper;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public Optional<UserResponseDTO> findUserById(Integer id) {
        return userMapper.toResponseDTO(userRepository.findById(id));
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}
