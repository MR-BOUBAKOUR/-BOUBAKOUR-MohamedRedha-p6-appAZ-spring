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
import com.payMyBuddy.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final UserService userService;
    private final AccountMapper accountMapper;

    public AccountResponseDTO findAccountById(Integer accountId) {
        return accountRepository.findById(accountId)
                .map(accountMapper::toAccountResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé."));
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

    public List<ReceiversAccountsResponseDTO> findAccountsForCurrentUserAndHisContacts(Integer userId) {

        UserResponseDTO userResponseDTO = userService.findUserById(userId);
        Set<ContactResponseDTO> contactsResponseDTO = userResponseDTO.getContacts();

        // Création d'une liste pour tous les contacts et leurs comptes
        List<ReceiversAccountsResponseDTO> accountsDTO = new ArrayList<>();

        // Créer une liste pour les comptes de l'utilisateur
        List<AccountResponseDTO> selfAccountsResponseDTO =
                accountRepository.findByUserId(userId).stream()
                        .map(accountMapper::toAccountResponseDTO)
                        .toList();

        // Ajout des comptes de l'utilisateur connecté
        accountsDTO.add(new ReceiversAccountsResponseDTO(
                userResponseDTO.getUsername(), // Nom de l'utilisateur connecté
                selfAccountsResponseDTO       // Ses comptes
        ));
        // Ajout des comptes des contacts
        accountsDTO.addAll(
                contactsResponseDTO.stream()
                        .map(contact -> {
                            Set<Account> accounts = accountRepository.findByUserId(contact.getContactId());
                            List<AccountResponseDTO> beneficiaryAccountsResponseDTO = accounts.stream()
                                    .map(accountMapper::toAccountResponseDTO)
                                    .toList();

                            return new ReceiversAccountsResponseDTO(
                                    contact.getUsername(),
                                    beneficiaryAccountsResponseDTO
                            );
                        })
                        .toList()
        );

        return accountsDTO;
    }
}
