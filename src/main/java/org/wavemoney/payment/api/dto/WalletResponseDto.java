package org.wavemoney.payment.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponseDto implements Serializable {

    private String walletId;      // UUID
    private String userId;

    private BigDecimal balance;
    private String currency;      // USD, EUR, etc.

    private String status;
}
