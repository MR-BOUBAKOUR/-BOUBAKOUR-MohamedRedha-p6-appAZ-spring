package com.payMyBuddy.dto.account;

import com.payMyBuddy.model.User;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountCreateDTO {

    @Null(message = "L'ID doit être nul lors de la création du compte.")
    private Integer id;

    @NotBlank(message = "Le type est obligatoire.")
    @Size(min = 6, message = "Le type doit contenir au moins 6 caractères.")
    private String type;

}
