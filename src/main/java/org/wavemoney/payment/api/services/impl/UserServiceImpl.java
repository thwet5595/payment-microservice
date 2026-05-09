package org.wavemoney.payment.api.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wavemoney.payment.api.dto.UserRequestDto;
import org.wavemoney.payment.api.dto.UserResponseDto;
import org.wavemoney.payment.api.model.User;
import org.wavemoney.payment.api.repository.UserRepository;
import org.wavemoney.payment.api.services.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    // save user
    @Override
    public UserResponseDto createUser(
            UserRequestDto userRequestDto) {

        System.out.println("UserServiceImpl createUser");

        // DTO -> Entity
        User user = User.builder()
                .name(userRequestDto.getName())
                .email(userRequestDto.getEmail())
                .phoneNumber(userRequestDto.getPhoneNumber())
                .build();

        // Save to MongoDB
        User savedUser = userRepository.save(user);

        // Entity -> Response DTO
        UserResponseDto response = UserResponseDto.builder()
                                    .name(savedUser.getName())
                                    .email(savedUser.getEmail())
                                    .phoneNumber(savedUser.getPhoneNumber())
                                    .userId(savedUser.getUserId())
                                    .build();
        return response;
    }

//    get user detail information
@Override
public UserResponseDto getUserById(String id) {

    User user = userRepository.findByUserId(id)
            .orElseThrow(() ->
                    new RuntimeException("User not found with id: " + id));


    UserResponseDto response = UserResponseDto.builder()
            .userId(user.getUserId())
            .name(user.getName())
            .email(user.getEmail())
            .phoneNumber(user.getPhoneNumber())
            .build();

    return response;
}
}
