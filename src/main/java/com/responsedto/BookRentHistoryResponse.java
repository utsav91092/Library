package com.responsedto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.enums.RequestStatus;
import com.utils.DateUtil;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookRentHistoryResponse {

    @JsonFormat(pattern = DateUtil.ISO_DATE)
    private LocalDateTime rentedDate;
    private String rentedBy;
    private RequestStatus rentStatus;

}
