package com.payMyBuddy.service;

import com.payMyBuddy.dto.account.AccountCreateDTO;
import com.payMyBuddy.dto.account.AccountResponseDTO;
import com.payMyBuddy.dto.account.BalanceUpdateDTO;
import com.payMyBuddy.mapper.AccountMapper;
import com.payMyBuddy.model.Account;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.AccountRepository;
import com.payMyBuddy.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;

    @Autowired
    public AccountService(AccountRepository accountRepository, UserRepository userRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.accountMapper = accountMapper;
    }

    public AccountResponseDTO findAccountById(Integer accountId) {
        return accountRepository
            .findById(accountId)
            .map(accountMapper::toResponseDTO)
            .orElseThrow(
                    () -> new RuntimeException("Account not found")
            );
    }

    public void createAccount(AccountCreateDTO accountCreateDTO, Integer userId) {
        User user = userRepository
            .findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Account account = accountMapper.toEntityFromCreateDTO(accountCreateDTO);
        account.setUser(user);
        account.setBalance(BigDecimal.ZERO);
        account.setCreatedAt(LocalDateTime.now());

        accountRepository.save(account);
    }

    public void deleteAccount(Integer accountId) {
        Account account = accountRepository
            .findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));

        accountRepository.delete(account);
    }

    public void updateBalanceAccount(BalanceUpdateDTO balanceUpdateDTO) {
        Account account = accountRepository
            .findById(balanceUpdateDTO.getAccountId())
            .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(
            account.getBalance()
                    .add(balanceUpdateDTO.getAmount())
        );

        accountRepository.save(account);
    }
}
