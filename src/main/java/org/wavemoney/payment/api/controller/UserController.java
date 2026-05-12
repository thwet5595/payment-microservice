package org.wavemoney.payment.api.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wavemoney.payment.api.dto.UserRequestDto;
import org.wavemoney.payment.api.dto.UserResponseDto;
import org.wavemoney.payment.api.services.UserService;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto){
        return ResponseEntity.ok(userService.createUser(userRequestDto));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable String id) {
        userService.logCacheStatus(id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{phoneNumber}")
    public ResponseEntity<UserResponseDto> getUserByPhoneNumber(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(userService.getUserByPhoneNumber(phoneNumber));
    }



}
