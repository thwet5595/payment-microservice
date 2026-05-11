//package org.wavemoney.payment.api.model;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Document(collection = "transactions")
//public class Transaction {
//
//    @Id
//    private String id;
//
//    private String transactionId;
//
//    private String fromWalletId;
//
//    private String toWalletId;
//
//    private BigDecimal amount;
//
//    private String currency;
//
//    private TransactionType type;
//
//    private TransactionStatus status;
//
//    private String description;
//
//    private LocalDateTime createdAt;
//
//    private LocalDateTime completedAt;
//
//    public enum TransactionType {
//        DEPOSIT,
//        WITHDRAWAL,
//        TRANSFER
//    }
//
//    public enum TransactionStatus {
//        PENDING,
//        COMPLETED,
//        FAILED
//    }
//}

package org.wavemoney.payment.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.wavemoney.payment.api.model.enums.TransactionStatus;
import org.wavemoney.payment.api.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    private String id;

    @Builder.Default
    private String transactionId = UUID.randomUUID().toString() ;

    private String fromWalletId;

    private String toWalletId;

    private BigDecimal amount;

    private String currency;

    private TransactionStatus status;

    private TransactionType type;
    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;
    @CreatedDate
    private LocalDateTime timestamp;
}