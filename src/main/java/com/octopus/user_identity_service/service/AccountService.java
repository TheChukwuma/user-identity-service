package com.octopus.user_identity_service.service;

import com.octopus.user_identity_service.enums.AccountStatus;
import com.octopus.user_identity_service.enums.AccountType;
import com.octopus.user_identity_service.enums.VerificationStatus;
import com.octopus.user_identity_service.exception.ResourceNotFoundException;
import com.octopus.user_identity_service.model.Account;
import com.octopus.user_identity_service.model.User;
import com.octopus.user_identity_service.repository.AccountRepository;
import com.octopus.user_identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public Account createAccount(Account account, Long userId) {
        log.info("Creating account for user with id: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Generate unique account number if not provided
        if (account.getAccountNumber() == null || account.getAccountNumber().isEmpty()) {
            account.setAccountNumber(generateAccountNumber(account.getAccountType()));
        }

        // Check if account number already exists
        if (accountRepository.findByAccountNumber(account.getAccountNumber()).isPresent()) {
            throw new IllegalArgumentException("Account number already exists: " + account.getAccountNumber());
        }

        account.setUser(user);
        
        // If this is the first account or marked as primary, make it primary
        if (account.getIsPrimary() || accountRepository.countByUserId(userId) == 0) {
            setAsPrimaryAccount(account.getId(), userId);
        }

        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Account> getAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    @Transactional(readOnly = true)
    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<Account> getPrimaryAccountByUserId(Long userId) {
        return accountRepository.findByUserIdAndIsPrimaryTrue(userId);
    }

    @Transactional(readOnly = true)
    public List<Account> getAccountsByUserIdAndStatus(Long userId, AccountStatus status) {
        return accountRepository.findByUserIdAndStatus(userId, status.name());
    }

    @Transactional(readOnly = true)
    public List<Account> getAccountsByAccountType(AccountType accountType) {
        return accountRepository.findByAccountType(accountType.name());
    }

    @Transactional(readOnly = true)
    public List<Account> getAccountsByStatus(AccountStatus status) {
        return accountRepository.findByStatus(status.name());
    }

    @Transactional(readOnly = true)
    public List<Account> getAccountsByVerificationStatus(VerificationStatus verificationStatus) {
        return accountRepository.findByVerificationStatus(verificationStatus.name());
    }

    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Account> getAllAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    public Account updateAccount(Long id, Account accountDetails) {
        log.info("Updating account with id: {}", id);
        
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        account.setAccountType(accountDetails.getAccountType());
        account.setStatus(accountDetails.getStatus());
        account.setCurrency(accountDetails.getCurrency());
        account.setIsPrimary(accountDetails.getIsPrimary());
        account.setVerificationStatus(accountDetails.getVerificationStatus());

        return accountRepository.save(account);
    }

    public Account updateBalance(Long id, BigDecimal newBalance) {
        log.info("Updating balance for account with id: {} to {}", id, newBalance);
        
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        account.setBalance(newBalance);
        account.setLastActivityAt(LocalDateTime.now());
        return accountRepository.save(account);
    }

    public Account deposit(Long id, BigDecimal amount) {
        log.info("Depositing {} to account with id: {}", amount, id);
        
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        account.setBalance(account.getBalance().add(amount));
        account.setLastActivityAt(LocalDateTime.now());
        return accountRepository.save(account);
    }

    public Account withdraw(Long id, BigDecimal amount) {
        log.info("Withdrawing {} from account with id: {}", amount, id);
        
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));
        account.setLastActivityAt(LocalDateTime.now());
        return accountRepository.save(account);
    }

    public Account transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        log.info("Transferring {} from account {} to account {}", amount, fromAccountId, toAccountId);
        
        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Source account not found with id: " + fromAccountId));

        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Destination account not found with id: " + toAccountId));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance in source account");
        }

        // Perform transfer
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
        
        LocalDateTime now = LocalDateTime.now();
        fromAccount.setLastActivityAt(now);
        toAccount.setLastActivityAt(now);

        accountRepository.save(fromAccount);
        return accountRepository.save(toAccount);
    }

    public Account setAsPrimaryAccount(Long accountId, Long userId) {
        log.info("Setting account {} as primary for user {}", accountId, userId);
        
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        if (!account.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Account does not belong to the specified user");
        }

        // First, unset all primary accounts for this user
        List<Account> userAccounts = accountRepository.findByUserId(userId);
        userAccounts.forEach(a -> a.setIsPrimary(false));
        accountRepository.saveAll(userAccounts);

        // Set this account as primary
        account.setIsPrimary(true);
        return accountRepository.save(account);
    }

    public Account changeStatus(Long id, AccountStatus status) {
        log.info("Changing status of account {} to {}", id, status);
        
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        account.setStatus(status);
        return accountRepository.save(account);
    }

    public Account changeVerificationStatus(Long id, VerificationStatus verificationStatus) {
        log.info("Changing verification status of account {} to {}", id, verificationStatus);
        
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        account.setVerificationStatus(verificationStatus);
        return accountRepository.save(account);
    }

    public void deleteAccount(Long id) {
        log.info("Deleting account with id: {}", id);
        
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        // If this was the primary account, set another account as primary
        if (account.getIsPrimary()) {
            List<Account> remainingAccounts = accountRepository.findByUserId(account.getUser().getId());
            remainingAccounts.remove(account);
            if (!remainingAccounts.isEmpty()) {
                remainingAccounts.get(0).setIsPrimary(true);
                accountRepository.save(remainingAccounts.get(0));
            }
        }

        accountRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long countAccountsByUserId(Long userId) {
        return accountRepository.countByUserId(userId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalBalanceByUserId(Long userId) {
        return accountRepository.findByUserId(userId)
                .stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static String generateAccountNumber(AccountType accountType) {
        String prefix = accountType.name().substring(0, 3);
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return prefix + "-" + uuid;
    }
}
