package com.payMyBuddy.mapper;

import com.payMyBuddy.dto.user.ContactResponseDTO;
import com.payMyBuddy.dto.user.UserCreateDTO;
import com.payMyBuddy.dto.user.UserResponseDTO;
import com.payMyBuddy.dto.user.UserUpdateDTO;
import com.payMyBuddy.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface UserMapper {

    @Mapping(target = "accounts", source = "accounts")
    @Mapping(target = "contacts", source = "contacts")
    UserResponseDTO toResponseDTO(User user);

    @Mapping(target = "contactId", source = "id")
    ContactResponseDTO toContactResponseDTO(User contact);

    User toEntityFromUpdateDTO(UserUpdateDTO userUpdateDTO);
    User toEntityFromCreateDTO(UserCreateDTO userCreateDTO);
}