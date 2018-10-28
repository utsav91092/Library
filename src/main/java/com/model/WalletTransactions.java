package com.model;

import com.enums.PaymentMode;
import com.enums.PaymentStatus;
import com.enums.TransactionFor;
import com.enums.TransactionType;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity(name = "wallet_transactions")
public class WalletTransactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    private TransactionFor transactionFor;

    private TransactionType transactionType;

    private PaymentMode paymentMode;

    private LocalDateTime transactionDate;

    private String referenceId;

    private PaymentStatus paymentStatus;

    private BigDecimal transactionAmount;

    private BigDecimal availableBalance;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "book_request_id", nullable = true)
    private BookRequest bookRequest;


}
