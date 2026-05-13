package org.wavemoney.payment.api.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequestDto {

    @NotBlank(message = "From wallet phone is required")
    private String fromPhoneNumber;

    @NotBlank(message = "To wallet phone is required")
    private String toPhoneNumber;

    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    private String description;
}
