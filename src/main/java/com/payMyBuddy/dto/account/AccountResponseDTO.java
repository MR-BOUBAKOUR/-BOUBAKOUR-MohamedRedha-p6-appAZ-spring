package com.payMyBuddy.dto.account;

import com.payMyBuddy.model.Account;
import com.payMyBuddy.model.User;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountResponseDTO {

    private Integer id;
    private BigDecimal balance;
    private String name;
    private LocalDateTime createdAt;

    public String getFormattedCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return createdAt != null ? createdAt.format(formatter) : null;
    }

}
