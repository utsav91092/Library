package com.responsedto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ApproveOrCollectBookResponse {
    private Long bookRequestId;
    private String message;
    private BigDecimal amountCreditedToWallet;
}
