package com.payMyBuddy.dto.account;

import jakarta.validation.constraints.*;
import lombok.*;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountCreateDTO {

    @Null(message = "L'ID doit être nul lors de la création du compte.")
    private Integer id;

    @NotBlank(message = "Le nom est obligatoire.")
    @Size(min = 6, message = "Le nom doit contenir au moins 6 caractères.")
    private String name;

}
