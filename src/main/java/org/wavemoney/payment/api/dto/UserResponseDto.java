package org.wavemoney.payment.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {

    private String userId;

    private String name;

    private String email;

    private String phoneNumber;

}