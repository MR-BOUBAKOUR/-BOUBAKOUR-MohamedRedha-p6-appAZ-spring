package com.payMyBuddy.service;

import com.payMyBuddy.mapper.AccountMapper;
import com.payMyBuddy.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountMapper accountMapper;
    @InjectMocks
    private AccountService accountService;

    @Test
    void findAccountByIdInternalUse_test() {
    }

    @Test
    void createAccount_test() {
    }

    @Test
    void deleteAccount_test() {
    }

    @Test
    void updateBalanceAccount_test() {
    }

    @Test
    void findAccountsForCurrentUserAndHisContacts_test() {
    }

    @Test
    void saveAccount_test() {
    }
}