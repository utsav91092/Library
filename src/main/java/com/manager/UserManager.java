package com.manager;

import com.enums.AdminBookRequestType;
import com.model.Users;
import com.requestdto.ApproveOrCollectBookRequest;
import com.requestdto.CreateUserRequest;
import com.responsedto.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserManager {

    Success signup(CreateUserRequest user) throws Exception;

    String signin(String username, String password);

    List<GetUserResponse> getAllUsers() throws Exception;

    GetUserResponse fetchUserById(Integer id);

    BookRequestResponse bookRentRequest(Long bookId, Users user);
    BookRequestResponse bookReturnRequest(Long bookId, Users user);
    BookRequestResponse bookAcknowledgeRequest(Long bookId, Users user);

    List<RentRequestsResponse> getBookRequest(AdminBookRequestType userBookRequestType);

    ApproveOrCollectBookResponse approveBookRequest(ApproveOrCollectBookRequest request);

    ApproveOrCollectBookResponse collectBookRequest(ApproveOrCollectBookRequest request);

    Users whoami(HttpServletRequest req);

    Users getUserByToken(String token);

}
