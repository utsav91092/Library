package com.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.utils.DateUtil;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Entity(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    private BigDecimal availableBalance;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.ISO_OFFSET_DATE_TIME)
    private LocalDateTime createdDate;

 /*   @OneToMany(fetch = FetchType.LAZY, mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WalletTransactions> walletTransactions;*/

}
