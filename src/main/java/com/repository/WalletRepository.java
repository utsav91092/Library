package com.repository;

import com.model.Users;
import com.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Wallet findWalletByUser(Users user);
}
