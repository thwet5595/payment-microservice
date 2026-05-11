package org.wavemoney.payment.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {

    @NotBlank(message="Name is required!")
    private String name;


    @Email(message = "Invalid email format")
    @NotBlank(message="Email is required!")
    private String email;

    @NotBlank(message="Phone number is required!")
    private String phoneNumber;

}