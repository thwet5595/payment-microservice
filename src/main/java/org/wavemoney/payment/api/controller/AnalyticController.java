package org.wavemoney.payment.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wavemoney.payment.api.dto.WalletSummaryResponseDto;
import org.wavemoney.payment.api.services.WalletCreateServices;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticController {

    private final WalletCreateServices walletCreateServices;

    public AnalyticController(WalletCreateServices walletCreateServices) {
        this.walletCreateServices = walletCreateServices;
    }

    @GetMapping("/{walletId}/summary")
    public ResponseEntity<WalletSummaryResponseDto> getSummary(
            @PathVariable String walletId) {

        return ResponseEntity.ok(walletCreateServices.getWalletSummary(walletId));
    }
}
