package com.repository;

import com.model.BookRequest;
import com.model.Wallet;
import com.model.WalletTransactions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository  extends JpaRepository<WalletTransactions, Long> {

    List<WalletTransactions> findByWallet(Wallet wallet);

    boolean existsByBookRequest(BookRequest bookRequest);
}
