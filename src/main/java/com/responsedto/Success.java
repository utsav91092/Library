package com.responsedto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Success {
    private Long id;
    private String message;
}
