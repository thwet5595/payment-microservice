package org.wavemoney.payment.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponseDto {

    private String walletId;      // UUID
    private String userId;

    private BigDecimal balance;
    private String currency;      // USD, EUR, etc.

    private String status;
}
