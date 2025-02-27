package com.payMyBuddy.service;

import com.payMyBuddy.mapper.UserMapper;
import com.payMyBuddy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserService userService;

    @Test
    void findUserByIdInternalUse_test() {
    }

    @Test
    void findUserById_test() {
    }

    @Test
    void findUserByEmailInternalUse_test() {
    }

    @Test
    void existsByEmail_test() {
    }

    @Test
    void createUser_test() {
    }

    @Test
    void createContact_test() {
    }

    @Test
    void deleteContact_test() {
    }
}