package com.payMyBuddy.mapper;

import com.payMyBuddy.dto.account.AccountCreateDTO;
import com.payMyBuddy.dto.account.AccountResponseDTO;
import com.payMyBuddy.dto.account.AccountUpdateDTO;
import com.payMyBuddy.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountResponseDTO toResponseDTO(Account account);

    Account toEntityFromUpdateDTO(AccountUpdateDTO accountUpdateDTO);
    Account toEntityFromCreateDTO(AccountCreateDTO accountCreateDTO);
}