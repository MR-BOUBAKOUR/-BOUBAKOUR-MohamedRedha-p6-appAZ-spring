package com.payMyBuddy.dto.user;

import lombok.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserCreateDTO {

    @Null(message = "ID should be null when creating a new user.")
    private Integer id;

    @NotBlank(message = "Username is required.")
    @Size(min = 3, max = 50, message = "Username should be between 3 and 50 characters.")
    private String username;

    @NotBlank(message = "Email is required.")
    @Email(message = "Please provide a valid email address.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password should be at least 8 characters long.")
    private String password;

    private BigDecimal balance = BigDecimal.ZERO;

}
