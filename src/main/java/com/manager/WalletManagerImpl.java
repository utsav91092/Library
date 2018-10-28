package com.manager;

import com.enums.PaymentMode;
import com.enums.PaymentStatus;
import com.enums.TransactionFor;
import com.enums.TransactionType;
import com.exception.CustomException;
import com.model.BookRequest;
import com.model.Users;
import com.model.Wallet;
import com.model.WalletTransactions;
import com.repository.UserRepository;
import com.repository.WalletRepository;
import com.repository.WalletTransactionRepository;
import com.requestdto.RechargeWalletRequest;
import com.responsedto.RechargeWalletResponse;
import com.responsedto.UserPaymentHistoryResponse;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class WalletManagerImpl implements WalletManager {


    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private WalletTransactionRepository walletTransactionRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public RechargeWalletResponse rechargeWallet(RechargeWalletRequest rechargeWalletRequest, Users user) {
        BigDecimal amount = rechargeWalletRequest.getAmount();
        Wallet wallet = walletRepository.findWalletByUser(user);
        BigDecimal newAmt = wallet.getAvailableBalance().add(amount);
        wallet.setAvailableBalance(newAmt);
        WalletTransactions transactions = new WalletTransactions();
        transactions.setAvailableBalance(newAmt);
        String refId = RandomString.make();
        transactions.setReferenceId(refId);
        transactions.setPaymentStatus(PaymentStatus.COMPLETED);
        transactions.setTransactionDate(LocalDateTime.now());
        transactions.setTransactionType(TransactionType.CREDIT);
        transactions.setTransactionFor(TransactionFor.RECHARGE);
        transactions.setWallet(wallet);
        transactions.setTransactionAmount(amount);
        transactions.setPaymentMode(PaymentMode.ONLINE);
        walletTransactionRepository.save(transactions);
        walletRepository.save(wallet);
        return RechargeWalletResponse.builder()
                .message("Recharged Successfully")
                .rechargedWithAmount(amount)
                .currentBalance(newAmt)
                .referenceId(refId)
                .build();
    }

    @Override
    public boolean creditWallet(Wallet wallet, Long debitFromWalletId, BigDecimal amount, BookRequest bookRequest) {
        boolean status = true;
        try {
            BigDecimal finalAmount = wallet.getAvailableBalance().add(amount);
            wallet.setAvailableBalance(finalAmount);
            WalletTransactions transactions = new WalletTransactions();
            transactions.setAvailableBalance(finalAmount);
            transactions.setBookRequest(bookRequest);
            transactions.setPaymentMode(PaymentMode.ONLINE);
            transactions.setReferenceId(RandomString.make());
            transactions.setPaymentStatus(PaymentStatus.COMPLETED);
            transactions.setTransactionDate(LocalDateTime.now());
            transactions.setTransactionType(TransactionType.CREDIT);
            transactions.setTransactionFor(TransactionFor.BOOK);
            transactions.setTransactionAmount(amount);
            transactions.setWallet(wallet);
            walletTransactionRepository.save(transactions);
            walletRepository.save(wallet);
        } catch (Exception ex) {
            ex.printStackTrace();
            status = false;
        }
        return status;
    }

    @Override
    public boolean debitWallet(Wallet wallet, Long creditToWalletId, BigDecimal amount, BookRequest bookRequest) {
        boolean status = true;
        try {
            if (wallet.getAvailableBalance().compareTo(amount) >= 0) {
                BigDecimal finalAmount = wallet.getAvailableBalance().subtract(amount);
                wallet.setAvailableBalance(finalAmount);
                WalletTransactions transactions = new WalletTransactions();
                transactions.setAvailableBalance(finalAmount);
                transactions.setBookRequest(bookRequest);
                transactions.setPaymentMode(PaymentMode.ONLINE);
                transactions.setReferenceId(RandomString.make());
                transactions.setPaymentStatus(PaymentStatus.COMPLETED);
                transactions.setTransactionDate(LocalDateTime.now());
                transactions.setTransactionType(TransactionType.DEBIT);
                transactions.setTransactionFor(TransactionFor.BOOK);
                transactions.setTransactionAmount(amount);
                transactions.setWallet(wallet);
                walletTransactionRepository.save(transactions);
                walletRepository.save(wallet);
            } else {
                status = false;
                throw new CustomException("Insufficient amount in User's wallet to deduct", HttpStatus.UNPROCESSABLE_ENTITY);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            status = false;
        }
        return status;
    }

    @Override
    public boolean transactBetweenWallets(Wallet toWallet, Wallet fromWallet, BigDecimal amount, BookRequest bookRequest) {
        boolean status = false;
        boolean debitStatus = debitWallet(fromWallet, toWallet.getId(), amount, bookRequest);
        if (debitStatus) {
            boolean creditStatus = creditWallet(toWallet, fromWallet.getId(), amount, bookRequest);
            if (creditStatus) {
                status = true;
            }
        }
        return status;
    }

    @Override
    public boolean paymentRecievedAlready(BookRequest bookRequest) {
        return walletTransactionRepository.existsByBookRequest(bookRequest);
    }

    @Override
    public List<UserPaymentHistoryResponse> fetchUserPaymentHistory(Integer userId) {
        Users user = userRepository.getOne(userId);
        Wallet wallet = walletRepository.findWalletByUser(user);
        List<WalletTransactions> walletTransactions = walletTransactionRepository.findByWallet(wallet);
        if (!CollectionUtils.isEmpty(walletTransactions)) {
            return walletTransactions.stream()
                    .map(transaction ->
                            UserPaymentHistoryResponse.builder()
                                    .referenceId(transaction.getReferenceId())
                                    .transactionAmount(transaction.getTransactionAmount())
                                    .transactionDate(transaction.getTransactionDate())
                                    .paymentMode(transaction.getPaymentMode())
                                    .paymentStatus(transaction.getPaymentStatus())
                                    .transactionType(transaction.getTransactionType())
                                    .transactionFor(transaction.getTransactionFor())
                                    .build()
                    ).collect(Collectors.toList());
        } else {
            throw new CustomException("Not done any transactions yet !", HttpStatus.UNPROCESSABLE_ENTITY);
        }

    }

}
