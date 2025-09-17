package com.octopus.user_identity_service.repository;

import com.octopus.user_identity_service.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByUserId(Long userId);

    Optional<Account> findByUserIdAndIsPrimaryTrue(Long userId);

    List<Account> findByUserIdAndStatus(Long userId, String status);

    List<Account> findByAccountType(String accountType);

    List<Account> findByStatus(String status);

    List<Account> findByVerificationStatus(String verificationStatus);

    @Query("SELECT a FROM Account a WHERE a.user.id = :userId AND a.accountType = :accountType")
    List<Account> findByUserIdAndAccountType(@Param("userId") Long userId, @Param("accountType") String accountType);

    @Query("SELECT a FROM Account a WHERE a.balance > :minBalance")
    List<Account> findByBalanceGreaterThan(@Param("minBalance") BigDecimal minBalance);

    @Query("SELECT a FROM Account a WHERE a.balance < :maxBalance")
    List<Account> findByBalanceLessThan(@Param("maxBalance") BigDecimal maxBalance);

    @Query("SELECT COUNT(a) FROM Account a WHERE a.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}
