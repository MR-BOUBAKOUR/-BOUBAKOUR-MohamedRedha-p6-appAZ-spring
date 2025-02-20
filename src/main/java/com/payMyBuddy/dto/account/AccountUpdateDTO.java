package com.payMyBuddy.dto.account;

import com.payMyBuddy.model.User;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountUpdateDTO {

    private Integer id;
    private User user;
    private BigDecimal balance;
    private String type;
    private LocalDateTime createdAt;
}
