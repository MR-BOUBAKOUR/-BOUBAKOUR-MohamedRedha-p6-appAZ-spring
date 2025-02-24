package com.payMyBuddy.service;

import com.payMyBuddy.dto.transaction.TransactionCreateDTO;
import com.payMyBuddy.dto.transaction.TransactionResponseDTO;
import com.payMyBuddy.exception.InsufficientBalanceException;
import com.payMyBuddy.exception.ResourceNotFoundException;
import com.payMyBuddy.exception.SelfSendingAmountException;
import com.payMyBuddy.mapper.TransactionMapper;
import com.payMyBuddy.model.Account;
import com.payMyBuddy.model.Transaction;
import com.payMyBuddy.model.TransactionType;
import com.payMyBuddy.repository.AccountRepository;
import com.payMyBuddy.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    public List<TransactionResponseDTO> findTransactionsForCurrentUser(Integer currentUserId) {

        return transactionRepository
                .findBySenderAccount_User_IdOrReceiverAccount_User_Id(currentUserId, currentUserId)
                .stream()
                .map(transactionMapper::toResponseDTO)
                .toList();

    }

    public void createTransaction(TransactionCreateDTO transactionCreateDTO) {
        Account senderAccount = accountRepository.findById(transactionCreateDTO.getSenderAccountId())
            .orElseThrow(() -> new ResourceNotFoundException("Compte émetteur non trouvé"));
        Account receiverAccount = accountRepository.findById(transactionCreateDTO.getReceiverAccountId())
            .orElseThrow(() -> new ResourceNotFoundException("Compte récepteur non trouvé"));

        Transaction transaction = transactionMapper.toEntityFromCreateDTO(transactionCreateDTO);

        if (senderAccount.getId().equals(receiverAccount.getId())) {
            throw new SelfSendingAmountException("Virement interdit sur le même compte .");
        }
        if (senderAccount.getBalance().compareTo(transaction.getAmount()) < 0) {
            throw new InsufficientBalanceException("Solde insuffisant. Veuillez alimenter votre compte.");
        }

        // the transaction part
        transaction.setSenderAccount(senderAccount);
        transaction.setReceiverAccount(receiverAccount);
        transaction.setType(
            senderAccount.getUser().getId().equals(receiverAccount.getUser().getId())
                ? TransactionType.SELF_TRANSFER
                : TransactionType.BENEFICIARY_TRANSFER
        );
        transaction.setCreatedAt(Instant.now());

        transactionRepository.save(transaction);

        // updating of the accounts
        senderAccount.setBalance(
            senderAccount.getBalance()
                .subtract(
                    transaction.getAmount()
                ));
        receiverAccount.setBalance(receiverAccount
            .getBalance()
                .add(
                    transaction.getAmount()
            ));

        accountRepository.save(receiverAccount);
        accountRepository.save(senderAccount);
    }
}
