package com.requestdto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
public class SearchBookRequest {
    private List<Long> categoryIds;
    private BigDecimal priceFrom;
    private BigDecimal priceTo;
    private Integer days;
    private Integer pageNumber;
    private Integer pageSize;
}
