package com.repository;

import com.model.WalletTransactions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTransactionsRepository extends JpaRepository<WalletTransactions,Long> {
}
