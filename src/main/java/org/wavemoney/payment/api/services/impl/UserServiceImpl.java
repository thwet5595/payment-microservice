package org.wavemoney.payment.api.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.wavemoney.payment.api.dto.UserRequestDto;
import org.wavemoney.payment.api.dto.UserResponseDto;
import org.wavemoney.payment.api.exception.validation.BadRequestException;
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
    @CachePut(value = "users", key = "#result.userId")
    public UserResponseDto createUser(UserRequestDto userRequestDto) {

        if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        if (userRepository.findByPhoneNumber(userRequestDto.getPhoneNumber()).isPresent()) {
            throw new BadRequestException("Phone already exists");
        }

        // DTO -> Entity
        User user = User.builder()
                .name(userRequestDto.getName())
                .email(userRequestDto.getEmail())
                .phoneNumber(userRequestDto.getPhoneNumber())
                .firstName(userRequestDto.getFirstName())
                .lastName(userRequestDto.getLastName())
                .address(userRequestDto.getAddress())
                .city(userRequestDto.getCity())
                .zipCode(userRequestDto.getZipCode())
                .build();

        // Save to MongoDB
        User savedUser = userRepository.save(user);

        // Entity -> Response DTO
        return UserResponseDto.builder()
                .userId(savedUser.getUserId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .phoneNumber(savedUser.getPhoneNumber())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .address(savedUser.getAddress())
                .city(savedUser.getCity())
                .zipCode(savedUser.getZipCode())
                .build();
    }

//    get user detail information
@Override
@Cacheable(value="users", key="#id")
public UserResponseDto getUserById(String id) {

    User user = userRepository.findByUserId(id)
            .orElseThrow(() ->
                    new RuntimeException("User not found with id: " + id));


    UserResponseDto response = UserResponseDto.builder()
            .userId(user.getUserId())
            .name(user.getName())
            .email(user.getEmail())
            .phoneNumber(user.getPhoneNumber())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .address(user.getAddress())
            .city(user.getCity())
            .zipCode(user.getZipCode())
            .build();

    return response;
}


    @Override
    @Cacheable(value="users", key="#phoneNumber")
    public UserResponseDto getUserByPhoneNumber(String phoneNumber) {

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() ->
                        new RuntimeException("User not found with phone: " + phoneNumber));


        UserResponseDto response = UserResponseDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .address(user.getAddress())
                .city(user.getCity())
                .zipCode(user.getZipCode())
                .build();

        return response;
    }
}
