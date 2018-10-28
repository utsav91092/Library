package com.responsedto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.utils.DateUtil;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class UserReadingHistoryResponse {

    private String bookName;
    private String bookCategory;
    private String authorName;
    private BigDecimal rentedPrice;
    @JsonFormat(pattern = DateUtil.ISO_DATE)
    private LocalDateTime rentedDate;
    private Long rentReferenceNumber;

}
