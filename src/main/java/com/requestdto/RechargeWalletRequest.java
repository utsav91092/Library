package com.requestdto;

import lombok.Data;

import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Data
public class RechargeWalletRequest {

    @Min(1)
    private BigDecimal amount;
}
