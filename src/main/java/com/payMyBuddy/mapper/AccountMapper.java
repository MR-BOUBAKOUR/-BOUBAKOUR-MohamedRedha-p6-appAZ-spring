package com.payMyBuddy.mapper;

import com.payMyBuddy.dto.account.AccountCreateDTO;
import com.payMyBuddy.dto.account.AccountResponseDTO;
import com.payMyBuddy.model.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountResponseDTO toAccountResponseDTO(Account account);

    Account toEntityFromCreateDTO(AccountCreateDTO accountCreateDTO);
}