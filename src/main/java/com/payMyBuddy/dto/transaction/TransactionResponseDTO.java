package com.payMyBuddy.dto.transaction;

import com.payMyBuddy.model.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionResponseDTO {

    private Integer id;
    private Integer senderAccountId;
    private Integer receiverAccountId;
    private BigDecimal amount;
    private String description;
    private TransactionType type;
    private Instant createdAt;

    public String getFormattedCreatedAt() {
        if (createdAt == null) return null;
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = createdAt.atZone(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return formatter.format(zdt);
    }

}
