package com.repository;

import com.enums.RequestStatus;
import com.model.BookRequest;
import com.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface BookRequestRepository extends JpaRepository<BookRequest, Long> {

    List<BookRequest> findByUsers(Users user);

    List<BookRequest> findByBookIdAndRequestStatusIn(Long bookId,List<RequestStatus> requestStatuses);


    List<BookRequest> findByRequestStatus(RequestStatus bookRequestType);

    BookRequest findByUsersAndBookIdAndRequestStatus(Users user,Long bookId,RequestStatus status);

    List<BookRequest> findByUsersAndRequestStatusIn(Users user, List<RequestStatus> requestStatuses);


}
