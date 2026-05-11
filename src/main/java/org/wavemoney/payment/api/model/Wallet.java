package org.wavemoney.payment.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "wallet")
public class Wallet {

    @Id
    private String id;

    private String walletId;
    private String userId;

    private BigDecimal balance;

    private String currency;

    private String phoneNumber;

    private String status; // ACTIVE, FROZEN, CLOSED

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}