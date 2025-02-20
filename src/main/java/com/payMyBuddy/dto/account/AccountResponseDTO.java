package com.payMyBuddy.dto.account;

import com.payMyBuddy.model.Account;
import com.payMyBuddy.model.User;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountResponseDTO {

    private Integer id;
    private User user;
    private BigDecimal balance;
    private String type;
    private LocalDateTime createdAt;
}
