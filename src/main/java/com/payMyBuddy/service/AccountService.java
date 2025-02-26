package com.payMyBuddy.service;

import com.payMyBuddy.dto.account.AccountCreateDTO;
import com.payMyBuddy.dto.account.AccountResponseDTO;
import com.payMyBuddy.dto.account.BalanceUpdateDTO;
import com.payMyBuddy.dto.account.ReceiversAccountsResponseDTO;
import com.payMyBuddy.dto.user.ContactResponseDTO;
import com.payMyBuddy.dto.user.UserResponseDTO;
import com.payMyBuddy.exception.ConflictException;
import com.payMyBuddy.exception.ResourceNotFoundException;
import com.payMyBuddy.mapper.AccountMapper;
import com.payMyBuddy.model.Account;
import com.payMyBuddy.model.User;
import com.payMyBuddy.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    private final UserService userService;

    public Account findAccountByIdInternalUse(Integer accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé."));
    }

    public void createAccount(AccountCreateDTO accountCreateDTO, Integer userId) {

        User user = userService.findUserByIdInternalUse(userId);

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

        Account account = findAccountByIdInternalUse(accountId);
        accountRepository.delete(account);
    }

    public void updateBalanceAccount(BalanceUpdateDTO balanceUpdateDTO) {

        Account account = findAccountByIdInternalUse(balanceUpdateDTO.getAccountId());
        account.setBalance(
            account.getBalance()
                .add(balanceUpdateDTO.getAmount())
        );

        accountRepository.save(account);
    }

    public List<ReceiversAccountsResponseDTO> findAccountsForCurrentUserAndHisContacts(Integer userId) {

        List<ReceiversAccountsResponseDTO> allAccountsDTO = new ArrayList<>();

        UserResponseDTO currentUser = userService.findUserById(userId);
        List<AccountResponseDTO> selfAccounts =
            accountRepository.findByUserId(userId).stream()
                .map(accountMapper::toAccountResponseDTO)
                .toList();

        allAccountsDTO.add(
            new ReceiversAccountsResponseDTO(
                currentUser.getUsername(),
                selfAccounts
        ));

        Set<ContactResponseDTO> contacts = currentUser.getContacts();

        allAccountsDTO.addAll(
            contacts.stream()
                .map(contact -> {

                    Set<Account> accounts = accountRepository.findByUserId(contact.getContactId());
                    List<AccountResponseDTO> beneficiaryAccounts = accounts.stream()
                        .map(accountMapper::toAccountResponseDTO)
                        .toList();

                    return new ReceiversAccountsResponseDTO(
                        contact.getUsername(),
                        beneficiaryAccounts
                    );

                }).toList()
        );

        return allAccountsDTO;
    }

    public void saveAccount(Account account) {
        accountRepository.save(account);
    }
}
