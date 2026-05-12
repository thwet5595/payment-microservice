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

    @NotBlank(message = "Name is required!")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required!")
    private String email;

    @NotBlank(message = "Phone number is required!")
    private String phoneNumber;

    @NotBlank(message = "First name is required!")
    private String firstName;

    @NotBlank(message = "Last name is required!")
    private String lastName;

    @NotBlank(message = "Address is required!")
    private String address;

    @NotBlank(message = "City is required!")
    private String city;

    @NotBlank(message = "Zip code is required!")
    private String zipCode;

    @NotBlank(message = "Front ID image is required!")
    private String frontId;

    @NotBlank(message = "Back ID image is required!")
    private String backId;

    @NotBlank(message = "Selfie image is required!")
    private String selfie;

}