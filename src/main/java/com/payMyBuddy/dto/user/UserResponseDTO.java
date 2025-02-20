package com.payMyBuddy.dto.user;

import com.payMyBuddy.model.Account;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserResponseDTO {

    private Integer id;

    private String username;

    private String email;

    private Set<Account> accounts;
}
