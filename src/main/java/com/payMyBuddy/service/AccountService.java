package com.payMyBuddy.service;

import com.payMyBuddy.dto.account.AccountCreateDTO;
import com.payMyBuddy.dto.account.AccountResponseDTO;
import com.payMyBuddy.dto.account.BalanceUpdateDTO;
import com.payMyBuddy.dto.user.ContactAccountsResponseDTO;
import com.payMyBuddy.dto.user.ContactResponseDTO;
import com.payMyBuddy.dto.user.UserResponseDTO;
import com.payMyBuddy.exception.ConflictException;
import com.payMyBuddy.exception.ResourceNotFoundException;
import com.payMyBuddy.mapper.AccountMapper;
import com.payMyBuddy.model.Account;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.AccountRepository;
import com.payMyBuddy.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final AccountMapper accountMapper;

    public AccountResponseDTO findAccountById(Integer accountId) {
        return accountRepository
            .findById(accountId)
            .map(accountMapper::toResponseDTO)
            .orElseThrow(
                    () -> new ResourceNotFoundException("Compte non trouvé.")
            );
    }

    public void createAccount(AccountCreateDTO accountCreateDTO, Integer userId) {
        User user = userRepository
            .findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé."));

        if (accountRepository.existsByNameAndUser_Id(accountCreateDTO.getName(), userId)) {
            throw new ConflictException("Vous avez déjà un compte avec ce nom. Veuillez en choisir un autre.");
        }

        Account account = accountMapper.toEntityFromCreateDTO(accountCreateDTO);

        account.setUser(user);
        account.setBalance(BigDecimal.ZERO);
        account.setCreatedAt(Instant.now());

        accountRepository.save(account);
    }

    public void deleteAccount(Integer accountId) {
        Account account = accountRepository
            .findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé."));

        accountRepository.delete(account);
    }

    public void updateBalanceAccount(BalanceUpdateDTO balanceUpdateDTO) {
        Account account = accountRepository
            .findById(balanceUpdateDTO.getAccountId())
            .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé."));

        account.setBalance(
            account.getBalance()
                    .add(balanceUpdateDTO.getAmount())
        );

        accountRepository.save(account);
    }

    public List<ContactAccountsResponseDTO> findContactsAccountsForUser(Integer userId) {

        UserResponseDTO userResponseDTO = userService.findUserById(userId);

        logger.warn("-----------------------------------------------------");
        logger.warn("userResponseDTO: {}", userResponseDTO);
        logger.warn("-----------------------------------------------------");

        Set<ContactResponseDTO> contactsResponseDTO = userResponseDTO.getContacts();

        logger.warn("-----------------------------------------------------");
        logger.warn("contactsResponseDTO: {}", contactsResponseDTO);
        logger.warn("-----------------------------------------------------");

        List<ContactAccountsResponseDTO> contactsAccountsResponseDTO = contactsResponseDTO.stream()
                .map(contact -> {

                    Set<Account> accounts = accountRepository.findByUserId(contact.getContactId());

                    List<AccountResponseDTO> accountsResponseDTO = accounts.stream()
                            .map(accountMapper::toResponseDTO)
                            .toList();

                    return new ContactAccountsResponseDTO(
                            contact.getUsername(),
                            accountsResponseDTO
                    );
                })
                .toList();

        logger.warn("-----------------------------------------------------");
        logger.warn("contactsAccountsResponseDTO: {}", contactsAccountsResponseDTO);
        logger.warn("-----------------------------------------------------");

        return contactsAccountsResponseDTO;


    }
}
