package com.responsedto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.utils.DateUtil;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RentRequestsResponse {

    private Long bookRequestId;

    private String requestedByUser;

    private Long bookId;

    @JsonFormat(pattern = DateUtil.ISO_DATE)
    private LocalDateTime requestedDate;
}
