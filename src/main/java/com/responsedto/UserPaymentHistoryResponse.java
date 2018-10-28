package com.responsedto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.enums.PaymentMode;
import com.enums.PaymentStatus;
import com.enums.TransactionFor;
import com.enums.TransactionType;
import com.utils.DateUtil;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class UserPaymentHistoryResponse {

    private TransactionType transactionType;

    private TransactionFor transactionFor;

    private PaymentMode paymentMode;

    @JsonFormat(pattern = DateUtil.ISO_DATE)
    private LocalDateTime transactionDate;

    private String referenceId;

    private PaymentStatus paymentStatus;

    private BigDecimal transactionAmount;

}
