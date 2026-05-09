package org.wavemoney.payment.api.services;

import org.springframework.stereotype.Service;
import org.wavemoney.payment.api.dto.UserRequestDto;
import org.wavemoney.payment.api.dto.UserResponseDto;

@Service
public interface UserService
{
     UserResponseDto createUser(UserRequestDto user);
     UserResponseDto getUserById(String id);
}
