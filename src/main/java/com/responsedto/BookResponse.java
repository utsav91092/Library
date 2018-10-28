package com.responsedto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.enums.Languages;
import com.utils.DateUtil;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
public class BookResponse {

    private Long id;
    private String name;
    private String author;
    private String  bookCategory;
    private Integer noOfCopies;
    private BigDecimal ppr;
    private Languages language;
    @JsonFormat(pattern = DateUtil.ISO_DATE)
    private LocalDate arrivalDate;

}
