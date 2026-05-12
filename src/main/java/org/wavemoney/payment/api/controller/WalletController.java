package org.wavemoney.payment.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wavemoney.payment.api.dto.WalletRequestDto;
import org.wavemoney.payment.api.dto.WalletResponseDto;
import org.wavemoney.payment.api.services.WalletCreateServices;

@RestController
public class WalletController {

    @Autowired
    public WalletCreateServices walletCreateServices;

    @PostMapping("/api/wallet")
    public ResponseEntity<WalletResponseDto> createWallet(@RequestBody WalletRequestDto walletRequestDto) {
        return ResponseEntity.ok(walletCreateServices.createWallet(walletRequestDto));
    }

    @GetMapping("/api/wallet/{phoneNumber}")
    public ResponseEntity<WalletResponseDto> getWallet(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(walletCreateServices.getWallet(phoneNumber));
    }


}