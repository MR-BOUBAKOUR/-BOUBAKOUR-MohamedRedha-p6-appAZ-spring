package com.payMyBuddy.dto.account;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BalanceUpdateDTO {

    @NotNull(message = "L'ID du compte est obligatoire.")
    private Integer accountId;

    @NotNull(message = "Le montant est obligatoire.")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    private BigDecimal amount;

}
