package com.manager;

import com.requestdto.CreateBookRequest;
import com.requestdto.SearchBookRequest;
import com.requestdto.UpdateBookRequest;
import com.responsedto.*;

import java.util.List;


public interface BookManager {

    CreateBookResponse createBook(CreateBookRequest createBookRequest);
    CreateBookResponse updateBook(UpdateBookRequest updateBookRequest);
    BookResponse getBookById(Long id);
    List<BookResponse> getAllBooks(int pageno, int limit);
    Success deleteBook(Long bookId);
    List<BookResponse> getFilteredBook(SearchBookRequest SearchBookRequest) throws Exception;
    List<BookRentHistoryResponse> getBookRentHistory(Long bookId);
    void updateBookInventory(Long bookId, int i);
    List<UserReadingHistoryResponse> fetchUserReadingHistory(Integer user);
}
