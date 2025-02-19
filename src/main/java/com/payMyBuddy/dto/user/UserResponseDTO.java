package com.payMyBuddy.dto.user;

import lombok.*;

import java.math.BigDecimal;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserResponseDTO {

    private Integer id;

    private String username;

    private String email;

    private BigDecimal balance;
}
