package org.wavemoney.payment.api.repository;

import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.wavemoney.payment.api.dto.WalletResponseDto;
import org.wavemoney.payment.api.model.Wallet;

import java.util.Optional;

@Repository
public interface WalletCreateRepository extends MongoRepository<Wallet, String> {

    Optional<Wallet> findByWalletId(String walletId);

//    Optional<Wallet> findByPhoneNumber(String phoneNumber);

}