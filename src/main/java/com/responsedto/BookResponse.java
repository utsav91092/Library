package com.responsedto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.enums.Languages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.utils.DateUtil;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
public class BookResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String author;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String  bookCategory;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer noOfCopies;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal ppr;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Languages language;
    @JsonFormat(pattern = DateUtil.ISO_DATE)
    private LocalDate arrivalDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

}
