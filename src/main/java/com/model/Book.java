package com.model;

import com.enums.Languages;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String author;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private BookCategory bookCategory;
    private Integer noOfCopiesRemaining = 1;
    private BigDecimal price;
    private Languages language;
    private LocalDate arrivalDate;
}
