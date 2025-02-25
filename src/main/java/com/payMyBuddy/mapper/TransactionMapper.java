package com.payMyBuddy.mapper;

import com.payMyBuddy.dto.transaction.TransactionCreateDTO;
import com.payMyBuddy.dto.transaction.TransactionResponseDTO;
import com.payMyBuddy.model.Transaction;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Transaction toEntityFromCreateDTO(TransactionCreateDTO transactionCreateDTO);

    @Mapping(source = "senderAccount.id", target = "senderAccountId")
    @Mapping(source = "senderAccount.name", target = "senderAccountName")
    @Mapping(source = "senderAccount.user.username", target = "senderUsername")
    @Mapping(source = "senderAccount.user.id", target = "senderId")
    @Mapping(source = "receiverAccount.id", target = "receiverAccountId")
    @Mapping(source = "receiverAccount.name", target = "receiverAccountName")
    @Mapping(source = "receiverAccount.user.username", target = "receiverUsername")
    @Mapping(source = "receiverAccount.user.id", target = "receiverId")
    TransactionResponseDTO toResponseDTO(Transaction transaction);

}