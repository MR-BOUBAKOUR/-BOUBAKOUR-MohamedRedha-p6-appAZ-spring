package com.payMyBuddy.mapper;

import com.payMyBuddy.dto.user.UserResponseDTO;
import com.payMyBuddy.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "balance", source = "user.balance")
    default Optional<UserResponseDTO> toResponseDTO(Optional<User> user) {
        if (user.isEmpty()) {
            return Optional.empty();
        }
        User userEntity = user.get();
        UserResponseDTO dto = new UserResponseDTO(userEntity.getId(), userEntity.getUsername(), userEntity.getEmail(), userEntity.getBalance());
        return Optional.of(dto);
    }

    User toEntityFromResponseDTO(UserResponseDTO userResponseDTO);
}