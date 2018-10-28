package com.responsedto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateBookResponse {
    private String message;
    private String bookName;
}
