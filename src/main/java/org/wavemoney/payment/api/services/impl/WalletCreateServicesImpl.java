package org.wavemoney.payment.api.services.impl;

import org.springframework.stereotype.Service;
import org.wavemoney.payment.api.dto.WalletRequestDto;
import org.wavemoney.payment.api.dto.WalletResponseDto;
import org.wavemoney.payment.api.model.Wallet;
import org.wavemoney.payment.api.repository.WalletCreateRepository;
import org.wavemoney.payment.api.services.WalletCreateServices;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class WalletCreateServicesImpl implements WalletCreateServices {

    private final WalletCreateRepository walletCreateRepository;

    public WalletCreateServicesImpl(WalletCreateRepository walletCreateRepository) {
        this.walletCreateRepository = walletCreateRepository;
    }

    @Override
    public WalletResponseDto createWallet(WalletRequestDto walletRequestDto) {

        Wallet wallet = new Wallet();

        wallet.setWalletId(UUID.randomUUID().toString());
        wallet.setUserId(walletRequestDto.getUserId());  //where getUserId came from
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency("MMK");
        wallet.setStatus("ACTIVE");
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setUpdatedAt(LocalDateTime.now());

        // Save to MongoDB
        Wallet savedWallet = walletCreateRepository.save(wallet);

        // Convert Entity -> DTO
        WalletResponseDto dto = new WalletResponseDto();
//
//        dto.setId(savedWallet.getId());
        dto.setWalletId(savedWallet.getWalletId());
        dto.setUserId(savedWallet.getUserId());

        dto.setBalance(savedWallet.getBalance());
        dto.setCurrency(savedWallet.getCurrency());

        dto.setStatus(savedWallet.getStatus());

        return dto;
    }

    @Override
    public WalletResponseDto getWalletByWalletId(String walletId) {

        Wallet wallet= walletCreateRepository.findByWalletId(walletId).orElseThrow();

        WalletResponseDto dto= new WalletResponseDto();

        dto.setWalletId(walletId);
        dto.setBalance(wallet.getBalance());
        dto.setBalance(wallet.getBalance());
        dto.setCurrency(wallet.getCurrency());
        dto.setStatus(wallet.getStatus());

        return dto;

    }
}