package com.manager;

import com.enums.RequestStatus;
import com.exception.CustomException;
import com.model.Book;
import com.model.BookCategory;
import com.model.BookRequest;
import com.model.Users;
import com.repository.BookCategoryRepository;
import com.repository.BookRepository;
import com.repository.BookRequestRepository;
import com.repository.UserRepository;
import com.requestdto.CreateBookRequest;
import com.requestdto.SearchBookRequest;
import com.requestdto.UpdateBookRequest;
import com.responsedto.*;
import com.utils.BookSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookManagerImpl implements BookManager {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookCategoryRepository bookCategoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRequestRepository bookRequestRepository;


    @Override
    public CreateBookResponse createBook(CreateBookRequest createBookRequest) {
        Optional<BookCategory> bookCategoryOp = Optional.ofNullable(bookCategoryRepository.findOne(createBookRequest.getCategoryId()));
        if (bookCategoryOp.isPresent()) {
            Book book = new Book();
            book.setName(createBookRequest.getName());
            book.setArrivalDate(LocalDate.now());
            book.setAuthor(createBookRequest.getAuthor());
            book.setBookCategory(bookCategoryOp.get());
            book.setLanguage(createBookRequest.getLanguage());
            book.setNoOfCopiesRemaining(1);
            book.setPrice(createBookRequest.getPrice());
            bookRepository.save(book);
            return CreateBookResponse.builder()
                    .message("Book Created")
                    .bookName(createBookRequest.getName())
                    .build();
        } else {
            throw new CustomException("Invalid category id", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Override
    public CreateBookResponse updateBook(UpdateBookRequest updateBookRequest) {
        Optional<Book> bookOp = Optional.ofNullable(bookRepository.findOne(updateBookRequest.getBookId()));
        if (bookOp.isPresent()) {
            Book book = bookOp.get();
            Optional<BookCategory> bookCategoryOp = Optional.ofNullable(bookCategoryRepository.findOne(updateBookRequest.getCategoryId()));
            if (bookCategoryOp.isPresent()) {
                book.setBookCategory(bookCategoryOp.get());
            } else {
                throw new CustomException("Invalid category id", HttpStatus.UNPROCESSABLE_ENTITY);
            }
            book.setName(updateBookRequest.getName());
            book.setArrivalDate(LocalDate.now());
            book.setAuthor(updateBookRequest.getAuthor());
            book.setLanguage(updateBookRequest.getLanguage());
            book.setNoOfCopiesRemaining(1);
            book.setPrice(updateBookRequest.getPrice());
            bookRepository.save(book);
            return CreateBookResponse.builder()
                    .message("Book Created")
                    .bookName(updateBookRequest.getName())
                    .build();

        } else {
            throw new CustomException("Invalid book id", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Override
    public BookResponse getBookById(Long id) {
        Optional<Book> bookOp = Optional.ofNullable(bookRepository.findOne(id));
        if (bookOp.isPresent()) {
            Book book = bookOp.get();
            return buildBookResponse(book);
        } else
            return BookResponse.builder()
                    .message("Book Details not available").build();
    }

    private BookResponse buildBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .name(book.getName())
                .ppr(book.getPrice())
                .author(book.getAuthor())
                .bookCategory(Objects.nonNull(book.getBookCategory()) ? book.getBookCategory().getName() : "")
                .language(book.getLanguage())
                .arrivalDate(book.getArrivalDate())
                .noOfCopies(book.getNoOfCopiesRemaining())
                .build();
    }

    @Override
    public List<BookResponse> getAllBooks(int pageno, int limit) {
        Pageable pageable = new PageRequest(pageno - 1, limit);
        List<Book> books = bookRepository.findAll(pageable).getContent();
        return books.stream()
                .map(book -> buildBookResponse(book)).collect(Collectors.toList());
    }

    @Override
    public Success deleteBook(Long bookId) {
        Optional<Book> bookOp = Optional.ofNullable(bookRepository.findOne(bookId));
        if (bookOp.isPresent()) {
            bookRepository.delete(bookId);
            return Success.builder()
                    .id(bookId)
                    .message("Deleted Successfully")
                    .build();
        } else {
            throw new CustomException("Book not present", HttpStatus.NOT_FOUND);
        }
    }


    @Override
    public List<BookResponse> getFilteredBook(SearchBookRequest searchBookRequest) throws Exception {
        Pageable pageable = new PageRequest(searchBookRequest.getPageNumber() - 1, searchBookRequest.getPageSize());
        // create specification
        Specification spec = BookSpecification.doFilterSearch(searchBookRequest);
        List<Book> books = bookRepository.findAll(spec, pageable).getContent();
        return books.stream()
                .map(book -> buildBookResponse(book)).collect(Collectors.toList());
    }

    @Override
    public void updateBookInventory(Long bookId, int i) {
        Book book = bookRepository.getOne(bookId);
        book.setNoOfCopiesRemaining(book.getNoOfCopiesRemaining() + i);
        bookRepository.save(book);
    }

    @Override
    public List<BookRentHistoryResponse> getBookRentHistory(Long bookId) {
        List<BookRequest> bookRequests = bookRequestRepository.findByBookIdAndRequestStatusIn(bookId, Arrays.asList(RequestStatus.RENT_APPROVED, RequestStatus.ACKNOWLEDGED, RequestStatus.RETURN_REQUEST, RequestStatus.COLLECTED));
        return bookRequests.stream()
                .map(bookRequest ->
                        BookRentHistoryResponse.builder()
                                .rentedDate(bookRequest.getRequestedDate())
                                .rentedBy(bookRequest.getUsers().getUsername())
                                .rentStatus(bookRequest.getRequestStatus())
                                .build()).collect(Collectors.toList());
    }

    @Override
    public List<UserReadingHistoryResponse> fetchUserReadingHistory(Integer userId) {
        Users user = userRepository.getOne(userId);
        List<BookRequest> bookRequests = bookRequestRepository.findByUsersAndRequestStatusIn(user, Arrays.asList(RequestStatus.RENT_APPROVED, RequestStatus.ACKNOWLEDGED, RequestStatus.RETURN_REQUEST, RequestStatus.COLLECTED));
        Map<Long, BookResponse> bookDetailsMap = bookRequests.stream()
                .map(bookRequest -> bookRequest.getBookId())
                .distinct()
                .collect(Collectors.toMap(bookId -> bookId, bookId -> getBookById(bookId)));

        return bookRequests.stream()
                .map(bookRequest ->
                        UserReadingHistoryResponse.builder()
                                .rentReferenceNumber(bookRequest.getId())
                                .rentedDate(bookRequest.getApprovedDate())
                                .bookName(bookDetailsMap.get(bookRequest.getBookId()).getName() != null ? bookDetailsMap.get(bookRequest.getBookId()).getName() : bookDetailsMap.get(bookRequest.getBookId()).getMessage())
                                .bookCategory(bookDetailsMap.get(bookRequest.getBookId()).getBookCategory() != null ? bookDetailsMap.get(bookRequest.getBookId()).getBookCategory() : "")
                                .rentedPrice(bookDetailsMap.get(bookRequest.getBookId()).getPpr() != null ? bookDetailsMap.get(bookRequest.getBookId()).getPpr() : BigDecimal.ZERO)
                                .authorName(bookDetailsMap.get(bookRequest.getBookId()).getAuthor() != null ? bookDetailsMap.get(bookRequest.getBookId()).getAuthor() : "")
                                .build()).collect(Collectors.toList());
    }
}
