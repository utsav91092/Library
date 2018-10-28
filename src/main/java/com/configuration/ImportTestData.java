package com.configuration;

import com.enums.Languages;
import com.enums.UserStatus;
import com.model.*;
import com.repository.BookCategoryRepository;
import com.repository.BookRepository;
import com.repository.UserRepository;
import com.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Configuration
public class ImportTestData {

    @Autowired
    private BookCategoryRepository bookCategoryRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void importData() {
        /**
         * Random Data For Testing
         * */

        Random rand = new Random();
        List<Languages> languages = Arrays.asList(Languages.values());
        List<String> catgeories = Arrays.asList("Fiction", "Drama", "Romance", "History", "Science", "Journals");
        LocalDate now = LocalDate.now();
        List<LocalDate> dates = Arrays.asList(now, now.minusDays(2), now.minusDays(3), now.minusDays(4), now.minusDays(5), now.minusDays(6));
        List<BigDecimal> prices = Arrays.asList(BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), BigDecimal.valueOf(1800), BigDecimal.valueOf(600), BigDecimal.valueOf(100), BigDecimal.valueOf(700));
        for (int i = 0; i < catgeories.size(); i++) {
            BookCategory category = new BookCategory();
            int id = i;
            category.setId(Long.valueOf(++id));
            category.setName(catgeories.get(i));
            category.setCreatedDate(LocalDateTime.now());
            bookCategoryRepository.save(category);

            for (int j = 0; j < 10; j++) {
                Book book = new Book();
                book.setNoOfCopiesRemaining(1);
                book.setLanguage(languages.get(rand.nextInt(languages.size())));
                book.setBookCategory(category);
                book.setAuthor("author" + j);
                book.setArrivalDate(dates.get(rand.nextInt(dates.size())));
                book.setPrice(prices.get(rand.nextInt(prices.size())));
                book.setName("book" + category.getName() + j);
                bookRepository.save(book);
            }
        }

        Users user = new Users();
        user.setId(1);
        user.setUsername("utsav");
        user.setEmail("utsav@gmail.com");
        user.setRole(Role.ROLE_USER);
        user.setMax_books(5);
        user.setCreatedDate(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode("utsav"));
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setAvailableBalance(BigDecimal.ZERO);
        wallet.setCreatedDate(LocalDateTime.now());
        walletRepository.save(wallet);
    }
}
