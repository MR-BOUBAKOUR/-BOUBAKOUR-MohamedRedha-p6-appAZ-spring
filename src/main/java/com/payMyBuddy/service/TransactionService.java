package com.payMyBuddy.service;

import com.payMyBuddy.dto.transaction.TransactionCreateDTO;
import com.payMyBuddy.dto.transaction.TransactionResponseDTO;
import com.payMyBuddy.exception.InsufficientBalanceException;
import com.payMyBuddy.exception.SelfSendingAmountException;
import com.payMyBuddy.mapper.TransactionMapper;
import com.payMyBuddy.model.Account;
import com.payMyBuddy.model.Transaction;
import com.payMyBuddy.model.TransactionType;
import com.payMyBuddy.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    private final AccountService accountService;

    public List<TransactionResponseDTO> findTransactionsForCurrentUser(Integer currentUserId, int limit) {

        List<TransactionResponseDTO> transactions = transactionRepository
            .findBySenderAccount_User_IdOrReceiverAccount_User_Id(currentUserId, currentUserId)
            .stream()
            .map(transactionMapper::toResponseDTO)
            .sorted(Comparator.comparing(TransactionResponseDTO::getCreatedAt).reversed())
            .toList();

        if (limit > 0) {
            return transactions
                .stream()
                .limit(limit)
                .toList();
        }
        return transactions;
    }

    public void createTransaction(TransactionCreateDTO transactionCreateDTO) {

        Account senderAccount = accountService.findAccountByIdInternalUse(transactionCreateDTO.getSenderAccountId());
        Account receiverAccount = accountService.findAccountByIdInternalUse(transactionCreateDTO.getReceiverAccountId());

        Transaction transaction = transactionMapper.toEntityFromCreateDTO(transactionCreateDTO);

        if (senderAccount.getId().equals(receiverAccount.getId())) {
            throw new SelfSendingAmountException("Virement interdit sur le même compte.");
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

        accountService.saveAccount(senderAccount);
        accountService.saveAccount(receiverAccount);
    }
}
