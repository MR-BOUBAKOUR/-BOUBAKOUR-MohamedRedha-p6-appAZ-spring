package com.payMyBuddy.service;

import com.payMyBuddy.dto.transaction.TransactionCreateDTO;
import com.payMyBuddy.dto.transaction.TransactionResponseDTO;
import com.payMyBuddy.exception.ResourceNotFoundException;
import com.payMyBuddy.mapper.TransactionMapper;
import com.payMyBuddy.model.Account;
import com.payMyBuddy.model.Transaction;
import com.payMyBuddy.model.TransactionType;
import com.payMyBuddy.repository.AccountRepository;
import com.payMyBuddy.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TransactionService {

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

    public void createTransaction(Integer userId, TransactionCreateDTO transactionCreateDTO) {
        Account sender = accountRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Compte émetteur non trouvé"));
        Account receiver = accountRepository.findById(transactionCreateDTO.getReceiverAccountId())
            .orElseThrow(() -> new ResourceNotFoundException("Compte récepteur non trouvé"));

        Transaction transaction = transactionMapper.toEntityFromCreateDTO(transactionCreateDTO);

        transaction.setSenderAccount(sender);
        transaction.setReceiverAccount(receiver);
        transaction.setType(
                sender.getId().equals(receiver.getId())
                    ? TransactionType.SELF_TRANSFER
                    : TransactionType.BENEFICIARY_TRANSFER
        );
        transaction.setCreatedAt(Instant.now());

        transactionRepository.save(transaction);
    }
}
