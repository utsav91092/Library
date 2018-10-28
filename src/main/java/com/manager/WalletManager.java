package com.manager;

import com.model.BookRequest;
import com.model.Users;
import com.model.Wallet;
import com.requestdto.RechargeWalletRequest;
import com.responsedto.RechargeWalletResponse;
import com.responsedto.UserPaymentHistoryResponse;

import java.math.BigDecimal;
import java.util.List;

public interface WalletManager {

    List<UserPaymentHistoryResponse> fetchUserPaymentHistory(Integer userId);

    RechargeWalletResponse rechargeWallet(RechargeWalletRequest rechargeWalletRequest, Users user) throws Exception;

    boolean creditWallet(Wallet wallet, Long transactionWith, BigDecimal amount, BookRequest bookRequest);

    boolean debitWallet(Wallet wallet, Long transactionWith, BigDecimal amount, BookRequest bookReques);

    boolean transactBetweenWallets(Wallet fromWallet, Wallet toWallet, BigDecimal amount, BookRequest bookRequest);

    boolean paymentRecievedAlready(BookRequest bookRequest);

}
