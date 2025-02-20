package com.payMyBuddy.mapper;

import com.payMyBuddy.dto.user.UserCreateDTO;
import com.payMyBuddy.dto.user.UserResponseDTO;
import com.payMyBuddy.model.User;
import org.mapstruct.Mapper;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface UserMapper {

    default Optional<UserResponseDTO> toResponseDTO(Optional<User> user) {
        if (user.isEmpty()) {
            return Optional.empty();
        }
        User userEntity = user.get();
        UserResponseDTO dto = new UserResponseDTO(userEntity.getId(), userEntity.getUsername(), userEntity.getEmail(), userEntity.getAccounts());
        return Optional.of(dto);
    }

    User toEntityFromResponseDTO(UserResponseDTO userResponseDTO);
    User toEntityFromCreateDTO(UserCreateDTO userCreateDTO);
}