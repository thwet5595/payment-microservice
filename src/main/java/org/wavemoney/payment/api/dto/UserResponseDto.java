package org.wavemoney.payment.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto implements Serializable {

    private String userId;
    private String name;
    private String email;
    private String phoneNumber;

    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String zipCode;

    private String frontId;
    private String backId;
    private String selfie;

}