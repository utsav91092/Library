package com.responsedto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Error {
    private String id;
    private String message;
    private List<String> errors = new ArrayList<>();
}
