package com.responsedto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookRequestResponse {
    private Long bookRequestId;
    private String message;
}
