package com.payMyBuddy.mapper;

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
    UserResponseDTO toResponseDTO(User user);

    User toEntityFromUpdateDTO(UserUpdateDTO userUpdateDTO);
    User toEntityFromCreateDTO(UserCreateDTO userCreateDTO);
}