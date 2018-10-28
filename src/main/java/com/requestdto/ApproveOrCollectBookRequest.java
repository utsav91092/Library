package com.requestdto;

import lombok.Data;


import javax.validation.constraints.NotNull;

@Data
public class ApproveOrCollectBookRequest {

    @NotNull
    private Long bookRequestId;


}
