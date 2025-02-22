package com.payMyBuddy.dto.user;

import lombok.*;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContactResponseDTO {

    private Integer id;
    private String username;
    private String email;

}
