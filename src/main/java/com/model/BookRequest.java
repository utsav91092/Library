package com.model;

import com.enums.RequestStatus;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity(name = "book_request")
public class BookRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Users users;

    private Long bookId;

    private LocalDateTime requestedDate;

    private LocalDateTime approvedDate;

    private LocalDateTime acknowledgedDate;

    private LocalDateTime returnedRequestDate;

    private LocalDateTime collectedDate;

    private RequestStatus requestStatus;


}
