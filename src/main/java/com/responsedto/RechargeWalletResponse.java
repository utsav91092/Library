package com.responsedto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class RechargeWalletResponse {

    private String message;
    private BigDecimal rechargedWithAmount;
    private BigDecimal currentBalance;
    private String referenceId;
}
