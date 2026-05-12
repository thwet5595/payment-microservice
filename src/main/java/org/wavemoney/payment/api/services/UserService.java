package org.wavemoney.payment.api.services;

import org.springframework.stereotype.Service;
import org.wavemoney.payment.api.dto.UserRequestDto;
import org.wavemoney.payment.api.dto.UserResponseDto;

@Service
public interface UserService
{
     void logCacheStatus(String id);
     UserResponseDto createUser(UserRequestDto user);
     UserResponseDto getUserById(String id);
     UserResponseDto getUserByPhoneNumber(String phoneNumber);
}
