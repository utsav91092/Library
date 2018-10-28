package com.repository;

import com.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface BookRepository extends PagingAndSortingRepository<Book, Long>, JpaSpecificationExecutor<Book>, JpaRepository<Book, Long> {

    boolean existsById(Long id);
    Book findById(Long id);

}
