package com.payMyBuddy.dto.user;

import com.payMyBuddy.dto.account.AccountResponseDTO;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContactAccountsResponseDTO {

    private String contactName;
    private List<AccountResponseDTO> accounts;

}
