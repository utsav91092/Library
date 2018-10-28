package com.controller;

import io.swagger.annotations.*;
import com.manager.BookManager;
import com.model.BookCategory;
import com.repository.BookCategoryRepository;
import com.requestdto.CreateBookRequest;
import com.requestdto.SearchBookRequest;
import com.requestdto.UpdateBookRequest;
import com.responsedto.BookRentHistoryResponse;
import com.responsedto.BookResponse;
import com.responsedto.CreateBookResponse;
import com.responsedto.Success;
import com.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/book")
@Api(tags = "book")
public class BookController {

    @Autowired
    private BookManager bookManager;
    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    @PostMapping("/create")
    @ApiOperation(value = "${BookController.create}",response = CreateBookResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied")})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createBook(@ApiParam("Create Book") @RequestBody CreateBookRequest book, @RequestHeader("Authorization") String jwt) {
        try {
            CreateBookResponse response = bookManager.createBook(book);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return CommonUtils.getResponseEntity("Creation Failed", "", Arrays.asList(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/update")
    @ApiOperation(value = "${BookController.update}",response = CreateBookResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied")})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateBook(@ApiParam("Create Book") @RequestBody UpdateBookRequest book, @RequestHeader("Authorization") String jwt) {
        try {
            CreateBookResponse response = bookManager.updateBook(book);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return CommonUtils.getResponseEntity("Updation Failed", "", Arrays.asList(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/delete{bookId}")
    @ApiOperation(value = "${BookController.delete}",response = Success.class)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 401, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> delete(
            @PathVariable Long bookId,
            @RequestHeader("Authorization") String jwt) {
        try {
            return new ResponseEntity<>(bookManager.deleteBook(bookId), HttpStatus.OK);
        } catch (Exception e) {
            return CommonUtils.getResponseEntity("Delete Failed", "", Arrays.asList(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/{bookId}")
    @ApiOperation(value = "${BookController.getbyid}",response = BookResponse.class)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 401, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> getBookById(
            @PathVariable Long bookId,
            @RequestHeader("Authorization") String jwt) {
        try {
            return new ResponseEntity<>(bookManager.getBookById(bookId), HttpStatus.OK);
        } catch (Exception e) {
            return CommonUtils.getResponseEntity("Fetch Failed", "", Arrays.asList(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/rentHistory/{bookId}")
    @ApiOperation(value = "${BookController.renthistory}",response = BookRentHistoryResponse.class,responseContainer = "List")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 401, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> getRentHistory(
            @PathVariable Long bookId,
            @RequestHeader("Authorization") String jwt) {
        try {
            return new ResponseEntity<>(bookManager.getBookRentHistory(bookId), HttpStatus.OK);
        } catch (Exception e) {
            return CommonUtils.getResponseEntity("Fetch Failed", "", Arrays.asList(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    @ApiOperation(value = "${BookController.getallbooks}",response = BookResponse.class,responseContainer = "List")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 401, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> getAllBooks(
            @RequestParam(value = "page_size", defaultValue = "5", required = false) int pazeSize,
            @RequestParam(value = "page_number", defaultValue = "1", required = false) int pageNumber,
            @RequestHeader("Authorization") String jwt) {
        try {
            return new ResponseEntity<>(bookManager.getAllBooks(pageNumber, pazeSize), HttpStatus.OK);
        } catch (Exception e) {
            return CommonUtils.getResponseEntity("Fetch Failed", "", Arrays.asList(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    @ApiOperation(value = "${BookController.search}",responseContainer = "List",response = BookResponse.class)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 401, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> searchBooks(
            @RequestParam(value = "category_ids", required = false) List<Long> categoryIds,
            @RequestParam(value = "price_from", required = false) BigDecimal priceFrom,
            @RequestParam(value = "price_to", required = false) BigDecimal priceTo,
            @RequestParam(value = "arrival_in_last_days", required = false) int days,
            @RequestParam(value = "page_size", defaultValue = "5", required = false) int pazeSize,
            @RequestParam(value = "page_number", defaultValue = "1", required = false) int pageNumber,
            @RequestHeader("Authorization") String jwt) {
        SearchBookRequest searchBookRequest = SearchBookRequest.builder()
                .categoryIds(categoryIds)
                .priceFrom(priceFrom)
                .priceTo(priceTo)
                .days(days)
                .pageNumber(pageNumber)
                .pageSize(pazeSize)
                .build();
        try {
            return new ResponseEntity<>(bookManager.getFilteredBook(searchBookRequest), HttpStatus.OK);
        } catch (Exception e) {
            return CommonUtils.getResponseEntity("Fetch Failed", "", Arrays.asList(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAllCategories")
    @ApiOperation(value = "${BookController.getallcategories}",response = BookCategory.class,responseContainer = "List")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 401, message = "Expired or invalid JWT token")})
    public ResponseEntity<?> categoreies(
            @RequestHeader("Authorization") String jwt) {
        try {
            return new ResponseEntity<>(bookCategoryRepository.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            return CommonUtils.getResponseEntity("Fetch Failed", "", Arrays.asList(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
