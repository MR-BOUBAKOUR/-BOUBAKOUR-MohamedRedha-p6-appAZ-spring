package com.payMyBuddy.mapper;

import com.payMyBuddy.dto.account.AccountCreateDTO;
import com.payMyBuddy.dto.account.AccountResponseDTO;
import com.payMyBuddy.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    default Optional<AccountResponseDTO> toResponseDTO(Optional<Account> account) {
        if (account.isEmpty()) {
            return Optional.empty();
        }
        Account accountEntity = account.get();
        AccountResponseDTO dto = new AccountResponseDTO(accountEntity.getId(), accountEntity.getUser(), accountEntity.getBalance(), accountEntity.getName(), accountEntity.getCreatedAt());
        return Optional.of(dto);
    }

    Account toEntityFromResponseDTO(AccountResponseDTO accountResponseDTO);
    Account toEntityFromCreateDTO(AccountCreateDTO accountCreateDTO);
}