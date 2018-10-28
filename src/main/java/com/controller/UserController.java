package com.controller;

import com.exception.CustomException;
import com.model.Role;
import io.swagger.annotations.*;
import com.enums.AdminBookRequestType;
import com.manager.BookManager;
import com.manager.UserManager;
import com.manager.WalletManager;
import com.model.BookRequest;
import com.model.Users;
import com.requestdto.ApproveOrCollectBookRequest;
import com.requestdto.UserBookRequest;
import com.requestdto.CreateUserRequest;
import com.responsedto.*;
import com.utils.CommonUtils;
import javafx.beans.property.ReadOnlyListProperty;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/users")
@Api(tags = "users")
public class UserController {

    @Autowired
    private UserManager userManager;

    @Autowired
    private WalletManager walletManager;

    @Autowired
    private BookManager bookManager;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/signin")
    @ApiOperation(value = "${UserController.signin}", response = LoginResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 422, message = "Invalid username/password supplied")})
    public LoginResponse login(
            @ApiParam("Username") @RequestParam String username,
            @ApiParam("Password") @RequestParam String password) {
        return LoginResponse.builder()
                .jwt(userManager.signin(username, password))
                .build();

    }

    @PostMapping("/signup")
    @ApiOperation(value = "${UserController.signup}", response = Success.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 422, message = "Username is already in use")})
    public ResponseEntity<?> signup(@ApiParam("Create User/Admin") @RequestBody CreateUserRequest user) {
        try {
            Success response = userManager.signup(user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return CommonUtils.getResponseEntity("Creation Failed", "", Arrays.asList(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping(value = "/all")
    @ApiOperation(value = "${UserController.getallusers}", response = GetUserResponse.class,
            responseContainer = "List")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 401, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String jwt) {
        try {
            List<GetUserResponse> users = userManager.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return CommonUtils.getResponseEntity("Failed", "", Arrays.asList(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping(value = "/user")
    @ApiOperation(value = "${UserController.getuser}", response = GetUserResponse.class)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "User Not Available"),
            @ApiResponse(code = 401, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String jwt) {
        try {
            GetUserResponse user = modelMapper.map(userManager.getUserByToken(jwt), GetUserResponse.class);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return CommonUtils.getResponseEntity("Failed", "", Arrays.asList(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    @GetMapping(value = "/bookRequests/{userBookRequestType}")
    @ApiOperation(value = "${UserController.getbookrequest}", response = BookRequest.class,
            responseContainer = "List")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 401, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> getBookRequests(@PathVariable AdminBookRequestType userBookRequestType, @RequestHeader("Authorization") String jwt) {
        try {
            List<RentRequestsResponse> bookRequests = userManager.getBookRequest(userBookRequestType);
            return new ResponseEntity<>(bookRequests, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return CommonUtils.getResponseEntity("Failed", "", Arrays.asList(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/bookRequest/rentRequest")
    @ApiOperation(value = "${UserController.rentrequest}", response = BookRequestResponse.class)
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 422, message = "Insufficient Fund")})
    public ResponseEntity<?> rentRequest(@RequestBody UserBookRequest userBookRequest, @RequestHeader("Authorization") String jwt) {
        try {
            Users user = userManager.getUserByToken(jwt);
            return new ResponseEntity<>(userManager.bookRentRequest(userBookRequest.getBookId(), user), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return CommonUtils.getResponseEntity("Failed", "", Arrays.asList(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/bookRequest/returnRequest")
    @ApiOperation(value = "${UserController.returnrequest}", response = BookRequestResponse.class)
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 422, message = "Insufficient Fund")})
    public ResponseEntity<?> returnRequest(@RequestBody UserBookRequest userBookRequest, @RequestHeader("Authorization") String jwt) {
        try {
            Users user = userManager.getUserByToken(jwt);
            return new ResponseEntity<>(userManager.bookReturnRequest(userBookRequest.getBookId(), user), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return CommonUtils.getResponseEntity("Failed", "", Arrays.asList(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/bookRequest/ackRequest")
    @ApiOperation(value = "${UserController.ackrequest}", response = BookRequestResponse.class)
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 422, message = "Insufficient Fund")})
    public ResponseEntity<?> ackRequest(@RequestBody UserBookRequest userBookRequest, @RequestHeader("Authorization") String jwt) {
        try {
            Users user = userManager.getUserByToken(jwt);
            return new ResponseEntity<>(userManager.bookAcknowledgeRequest(userBookRequest.getBookId(), user), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return CommonUtils.getResponseEntity("Failed", "", Arrays.asList(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/bookRequest/approve")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "${UserController.approve}", response = ApproveOrCollectBookResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied")})
    public ResponseEntity<?> approve(@RequestBody ApproveOrCollectBookRequest request, @RequestHeader("Authorization") String jwt) {
        try {
            ApproveOrCollectBookResponse response = userManager.approveBookRequest(request);
            if (response != null) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return CommonUtils.getResponseEntity("Failed", "", Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return CommonUtils.getResponseEntity("Failed", "", Arrays.asList(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/bookRequest/collect")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "${UserController.collect}", response = ApproveOrCollectBookResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied")})
    public ResponseEntity<?> collect(@RequestBody ApproveOrCollectBookRequest request, @RequestHeader("Authorization") String jwt) {
        try {
            return new ResponseEntity<>(userManager.collectBookRequest(request), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return CommonUtils.getResponseEntity("Failed", "", Arrays.asList(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping(value = "/readingHistory/{userId}")
    @ApiOperation(value = "${UserController.readinghistory}", responseContainer = "List", response = UserReadingHistoryResponse.class)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 401, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> getUsersReadingHistory(@PathVariable Integer userId, @RequestHeader("Authorization") String jwt) {
        try {
            List<UserReadingHistoryResponse> history = bookManager.fetchUserReadingHistory(userId);
            return new ResponseEntity<>(history, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return CommonUtils.getResponseEntity("Failed", "", Arrays.asList(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/paymentHistory/{userId}")
    @ApiOperation(value = "${UserController.paymenthistory}", responseContainer = "List", response = UserPaymentHistoryResponse.class)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 422, message = "No Transactions Available"),
            @ApiResponse(code = 401, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> getUsersPaymentHistory(@PathVariable Integer userId, @RequestHeader("Authorization") String jwt) {
        try {
            Users user = userManager.getUserByToken(jwt);
            List<UserPaymentHistoryResponse> history = new ArrayList<>();
            if (user.getRole().equals(Role.ROLE_ADMIN)) {
                history = walletManager.fetchUserPaymentHistory(userId);
            }
            if ((user.getRole().equals(Role.ROLE_USER)) && (user.getId().equals(userId))) {
                history = walletManager.fetchUserPaymentHistory(userId);

            } else {
                new CustomException("Cannot see other users history", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(history, HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return CommonUtils.getResponseEntity("Failed", "", Arrays.asList(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping(value = "/me")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 401, message = "Expired or invalid JWT token")})
    public GetUserResponse whoami(HttpServletRequest req, @RequestHeader("Authorization") String jwt) {
        return modelMapper.map(userManager.whoami(req), GetUserResponse.class);
    }

}
