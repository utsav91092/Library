package com.manager;


import com.enums.AdminBookRequestType;
import com.enums.RequestStatus;
import com.enums.UserStatus;
import com.exception.CustomException;
import com.model.*;
import com.repository.BookRepository;
import com.repository.BookRequestRepository;
import com.repository.UserRepository;
import com.repository.WalletRepository;
import com.requestdto.ApproveOrCollectBookRequest;
import com.requestdto.CreateUserRequest;
import com.responsedto.*;
import com.security.JwtTokenProvider;
import com.utils.ObjectMapperUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
public class UserManagerImpl implements UserManager {


    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookRequestRepository bookRequestRepository;

    @Autowired
    private WalletManager walletManager;

    @Autowired
    private BookManager bookManager;

    @Override
    public Success signup(CreateUserRequest createUserRequest) throws Exception {
        if (userRepository.existsByUsername(createUserRequest.getUsername()))
            throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        if (userRepository.existsByEmail(createUserRequest.getEmail()))
            throw new CustomException("Email is already in use", HttpStatus.UNPROCESSABLE_ENTITY);

        Users user = modelMapper.map(createUserRequest, Users.class);
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        user.setCreatedDate(LocalDateTime.now());
        user.setUserStatus(UserStatus.ACTIVE);
        Integer userId;
        if (createUserRequest.getRole().equals(Role.ROLE_ADMIN)) {
            //Deactivate existing admin, assuming ony one admin exists
            if (userRepository.existsByRole(Role.ROLE_ADMIN)) {
                deactivateAllAdmins(Role.ROLE_ADMIN);
            }
            userId = userRepository.save(user).getId();
        } else {
            user.setMax_books(5);
            userId = userRepository.save(user).getId();
        }
        if (userId > 0) {
            createWallet(user);
        }
        return Success.builder()
                .id(Long.valueOf(userId))
                .message("Successfuly Signed Up")
                .build();
    }

    private void createWallet(Users user) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setAvailableBalance(BigDecimal.ZERO);
        wallet.setCreatedDate(LocalDateTime.now());
        walletRepository.save(wallet);
    }

    private void deactivateAllAdmins(Role roleAdmin) {
        List<Users> admins = userRepository.findByRoleAndUserStatus(roleAdmin, UserStatus.ACTIVE);
        admins.stream().forEach(users -> {
            users.setUserStatus(UserStatus.INACTIVE);
            userRepository.save(users);
        });
    }

    @Override
    public String signin(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return jwtTokenProvider.createToken(username, userRepository.findByUsername(username).getRole());
        } catch (AuthenticationException e) {
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Override
    public List<GetUserResponse> getAllUsers() throws Exception {
        return ObjectMapperUtils.mapAll(userRepository.findAll(), GetUserResponse.class);
    }

    @Override
    public GetUserResponse fetchUserById(Integer id) {
        Optional<Users> userOp = userRepository.findById(id);
        if (userOp.isPresent())
            return modelMapper.map(userOp.get(), GetUserResponse.class);
        else
            throw new CustomException("User not available", HttpStatus.NOT_FOUND);
    }


    @Override
    public BookRequestResponse bookRentRequest(Long bookId, Users user) {
        BookRequest br = new BookRequest();
        if (bookRepository.existsById(bookId)) {
                if (preChecksForRentRequest(user, bookId)) {
                    BookRequest bookRequest = new BookRequest();
                    bookRequest.setUsers(user);
                    bookRequest.setBookId(bookId);
                    bookRequest.setRequestedDate(LocalDateTime.now());
                    bookRequest.setRequestStatus(RequestStatus.RENT_REQUEST);
                    br = bookRequestRepository.save(bookRequest);
                }
            }
        return BookRequestResponse.builder()
                .bookRequestId(br.getId())
                .message("Requested successfully")
                .build();
    }

    @Override
    public BookRequestResponse bookReturnRequest(Long bookId, Users user) {
        BookRequest br = new BookRequest();
        if (bookRepository.existsById(bookId)) {
                Optional<BookRequest> bookRequestOp = Optional.ofNullable(bookRequestRepository.findByUsersAndBookIdAndRequestStatus(user, bookId, RequestStatus.ACKNOWLEDGED));
                if (bookRequestOp.isPresent()) {
                    BookRequest bookRequest = bookRequestOp.get();
                    bookRequest.setRequestStatus(RequestStatus.RETURN_REQUEST);
                    bookRequest.setReturnedRequestDate(LocalDateTime.now());
                    br = bookRequestRepository.save(bookRequest);
                } else {
                    throw new CustomException("Book not acknowleged to admin yet, acknowledge then send return request / or already in return status", HttpStatus.UNPROCESSABLE_ENTITY);
                }
            } else {
            throw new CustomException("Invalid book id", HttpStatus.NOT_FOUND);
        }
        return BookRequestResponse.builder()
                .bookRequestId(br.getId())
                .message("Requested successfully")
                .build();
    }

    @Override
    public BookRequestResponse bookAcknowledgeRequest(Long bookId, Users user) {
        BookRequest br = new BookRequest();
        if (bookRepository.existsById(bookId)) {
            Optional<BookRequest> bookRequestOp = Optional.ofNullable(bookRequestRepository.findByUsersAndBookIdAndRequestStatus(user, bookId, RequestStatus.RENT_APPROVED));
            if (bookRequestOp.isPresent()) {
                BookRequest bookRequest = bookRequestOp.get();
                bookRequest.setRequestStatus(RequestStatus.ACKNOWLEDGED);
                bookRequest.setAcknowledgedDate(LocalDateTime.now());
                br = bookRequestRepository.save(bookRequest);
            } else {
                throw new CustomException("Book not approved yet by admin / Already acknowledged", HttpStatus.UNPROCESSABLE_ENTITY);
            }
        } else {
            throw new CustomException("Invalid book id", HttpStatus.NOT_FOUND);
        }
        return BookRequestResponse.builder()
                .bookRequestId(br.getId())
                .message("Requested successfully")
                .build();
    }


    @Override
    public List<RentRequestsResponse> getBookRequest(AdminBookRequestType adminBookRequestType) {
        List<BookRequest> bookRequests = new ArrayList<>();
        List<RentRequestsResponse> rentRequestsResponses = new ArrayList<>();
        switch (adminBookRequestType) {
            case RENT:
                bookRequests = bookRequestRepository.findByRequestStatus(RequestStatus.RENT_REQUEST);
                rentRequestsResponses = bookRequests.stream()
                        .map(bookRequest -> RentRequestsResponse.builder()
                                .bookRequestId(bookRequest.getId())
                                .requestedByUser(bookRequest.getUsers().getUsername())
                                .bookId(bookRequest.getBookId())
                                .requestedDate(bookRequest.getRequestedDate())
                                .build()).collect(Collectors.toList());
                break;
            case COLLECT:
                bookRequests = bookRequestRepository.findByRequestStatus(RequestStatus.RETURN_REQUEST);
                rentRequestsResponses = bookRequests.stream()
                        .map(bookRequest -> RentRequestsResponse.builder()
                                .bookRequestId(bookRequest.getId())
                                .requestedByUser(bookRequest.getUsers().getUsername())
                                .bookId(bookRequest.getBookId())
                                .requestedDate(bookRequest.getCollectedDate())
                                .build()).collect(Collectors.toList());
                break;
        }
        return rentRequestsResponses;
    }

    @Override
    public ApproveOrCollectBookResponse approveBookRequest(ApproveOrCollectBookRequest request) {
        Optional<BookRequest> bookRequestOp = Optional.ofNullable(bookRequestRepository.getOne(request.getBookRequestId()));
        if (bookRequestOp.isPresent()) {
            BookRequest bookRequest = bookRequestOp.get();
            if (bookRequest.getRequestStatus().equals(RequestStatus.RENT_APPROVED)) {
                throw new CustomException("Already Approved", HttpStatus.UNPROCESSABLE_ENTITY);
            } else {
                bookRequest.setRequestStatus(RequestStatus.RENT_APPROVED);
                bookRequest.setApprovedDate(LocalDateTime.now());
                BigDecimal ppr = calculateTotalAmountToBeCollectedForBook(bookRequest);
                //Assuming only one active admin
                Users admin = userRepository.findByRoleAndUserStatus(Role.ROLE_ADMIN, UserStatus.ACTIVE).get(0);
                Users user = bookRequest.getUsers();
                Wallet adminWallet = walletRepository.findWalletByUser(admin);
                Wallet userWallet = walletRepository.findWalletByUser(user);
                if (debitPprFromUserWalletAndCreditToAdminWallet(adminWallet, userWallet, ppr, bookRequest)) {
                    assignBookToUser(bookRequest);
                    bookManager.updateBookInventory(bookRequest.getBookId(), -1);
                    return ApproveOrCollectBookResponse.builder()
                            .bookRequestId(bookRequest.getId())
                            .amountCreditedToWallet(ppr)
                            .message("Rent Book Request Approved, Book assigned to user")
                            .build();
                }
            }

        } else {
            throw new CustomException("Invalid bookRequestId", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return null;
    }


    @Override
    public ApproveOrCollectBookResponse collectBookRequest(ApproveOrCollectBookRequest request) {
        Optional<BookRequest> bookRequestOp = Optional.ofNullable(bookRequestRepository.getOne(request.getBookRequestId()));
        if (bookRequestOp.isPresent()) {
            BookRequest bookRequest = bookRequestOp.get();
            if (bookRequest.getRequestStatus().equals(RequestStatus.COLLECTED)) {
                throw new CustomException("Already Collected", HttpStatus.UNPROCESSABLE_ENTITY);
            } else {
                bookRequest.setRequestStatus(RequestStatus.COLLECTED);
                bookRequest.setCollectedDate(LocalDateTime.now());
                BigDecimal ppr = BigDecimal.ZERO;
                bookRequestRepository.save(bookRequest);
                bookManager.updateBookInventory(bookRequest.getBookId(), 1);
                return ApproveOrCollectBookResponse.builder()
                        .bookRequestId(bookRequest.getId())
                        .amountCreditedToWallet(ppr)
                        .message("Collect Book Request Approved.")
                        .build();
            }

        } else {
            throw new CustomException("Invalid bookRequestId", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private BigDecimal calculateTotalAmountToBeCollectedForBook(BookRequest bookRequest) {
        //Assuming price as book price (Pay Per Rent)
        return bookRepository.findById(bookRequest.getBookId()).getPrice();
    }

    private void assignBookToUser(BookRequest bookRequest) {
        bookRequest.setRequestStatus(RequestStatus.RENT_APPROVED);
        bookRequest.setApprovedDate(LocalDateTime.now());
        bookRequestRepository.save(bookRequest);
    }

    private boolean debitPprFromUserWalletAndCreditToAdminWallet(Wallet admin, Wallet user, BigDecimal ppr, BookRequest bookRequest) {
        return walletManager.transactBetweenWallets(admin, user, ppr, bookRequest);
    }

    private boolean preChecksForRentRequest(Users user, Long bookId) {
        boolean pass = true;
        //Check For Wallet balance
        BigDecimal price = bookRepository.findOne(bookId).getPrice();
        BigDecimal walletbalance = walletRepository.findWalletByUser(user).getAvailableBalance();
        if (!(walletbalance.compareTo(price) >= 0)) {
            pass = false;
            throw new CustomException("Insufficient Fund in wallet, Please recharge it with minimum Rs." + String.valueOf(price) + " to rent book", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        //Check For Max no of books
        int noOfRentedBooksByUser = bookRequestRepository.findByUsersAndRequestStatusIn(user, Arrays.asList(RequestStatus.RENT_APPROVED, RequestStatus.ACKNOWLEDGED, RequestStatus.RETURN_REQUEST)).size();
        if (noOfRentedBooksByUser >= user.getMax_books()) {
            pass = false;
            throw new CustomException("Already reached max number of book rented, return old books to put rent request", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        //Check For book availability
        if (bookRepository.getOne(bookId).getNoOfCopiesRemaining().equals(0)) {
            pass = false;
            throw new CustomException("Requested book is already rented", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        //Check Admin Exists
        if (!userRepository.existsByRole(Role.ROLE_ADMIN)) {
            pass = false;
            throw new CustomException("Please create admin before, renting book", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (bookRequestRepository.findByUsersAndBookIdAndRequestStatus(user, bookId, RequestStatus.RENT_REQUEST) != null) {
            pass = false;
            throw new CustomException("Book Already requested", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return pass;
    }

    public Users whoami(HttpServletRequest req) {
        return userRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
    }

    public Users getUserByToken(String token) {
        return userRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveTokenForUser(token)));
    }

}
