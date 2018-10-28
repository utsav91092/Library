package com.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.utils.DateUtil;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity(name = "book_category")
public class BookCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @JsonFormat(pattern = DateUtil.ISO_OFFSET_DATE_TIME)
    private LocalDateTime createdDate;


}
