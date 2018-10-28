package com.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.enums.UserStatus;
import com.utils.DateUtil;
import lombok.Data;


import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(min = 4, max = 255, message = "Minimum username length: 4 characters")
    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Size(min = 4, message = "Minimum password length: 8 characters")
    private String password;

    @Enumerated(EnumType.STRING)
    Role role;

    private Integer max_books;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.ISO_OFFSET_DATE_TIME)
    private LocalDateTime createdDate;

    private UserStatus userStatus;

}
