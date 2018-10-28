package com.utils;

import com.model.Book;
import com.model.BookCategory;
import com.requestdto.SearchBookRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;


public class BookSpecification {

    public static Specification<Book> doFilterSearch(SearchBookRequest searchCriteria) {
        return (Specification<Book>) (Root<Book> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {

            List<Predicate> predicates = new LinkedList<>();

            if (searchCriteria.getCategoryIds() != null) {
                if (!CollectionUtils.isEmpty(searchCriteria.getCategoryIds())) {
                    Join<Book, BookCategory> categoryJoin = root.join("bookCategory");
                    /*Join<User, Role> roleJoin = userRoot.join("role");
                    return cb.equal(cb.lower(roleJoin.<String> get("name")), searchCriteria);*/
                    Expression<BookCategory> exp = categoryJoin.get("id");
                    predicates.add(exp.in(searchCriteria.getCategoryIds()));
                }
            }

            if (searchCriteria.getPriceFrom() != null && searchCriteria.getPriceTo() != null) {
                Expression<BigDecimal> exp = root.get("price");
                predicates.add(cb
                        .and(cb
                                .between(exp, searchCriteria.getPriceFrom(), searchCriteria.getPriceTo())));
            }

            if (searchCriteria.getDays() != null) {
                LocalDate arrivalDateFrom = LocalDate.now().minusDays(searchCriteria.getDays());
                LocalDate arrivalDateTo = LocalDate.now();
                Expression<LocalDate> exp = root.get("arrivalDate");
                predicates.add(cb
                        .and(cb
                                .between(exp, arrivalDateFrom, arrivalDateTo)));
            }

            // return the create query
            return cb.and(predicates.toArray(new Predicate[]{}));
        };
    }
}
