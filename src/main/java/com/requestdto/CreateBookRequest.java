package com.requestdto;

import com.enums.Languages;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


@Data
public class CreateBookRequest {

    @NotNull
    private String name;
    @NotNull
    private String author;
    @NotNull
    private Long categoryId;
    @Min(1)
    private BigDecimal price;
    @NotNull
    private Languages language;

}
