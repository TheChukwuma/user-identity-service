package com.octopus.user_identity_service.controller;

import com.octopus.user_identity_service.enums.AccountStatus;
import com.octopus.user_identity_service.enums.AccountType;
import com.octopus.user_identity_service.enums.VerificationStatus;
import com.octopus.user_identity_service.exception.ResourceNotFoundException;
import com.octopus.user_identity_service.model.Account;
import com.octopus.user_identity_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurityService.canAccessUser(authentication.name, #userId)")
    public ResponseEntity<Account> createAccountForUser(@PathVariable Long userId, @RequestBody Account account) {
        log.info("Creating account for user: {}", userId);
        Account createdAccount = accountService.createAccount(account, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @accountSecurityService.canAccessAccount(authentication.name, #id)")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Account account = accountService.getAccountById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        return ResponseEntity.ok(account);
    }

    @GetMapping("/number/{accountNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Account> getAccountByAccountNumber(@PathVariable String accountNumber) {
        Account account = accountService.getAccountByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with number: " + accountNumber));
        return ResponseEntity.ok(account);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurityService.canAccessUser(authentication.name, #userId)")
    public ResponseEntity<List<Account>> getAccountsByUserId(@PathVariable Long userId) {
        List<Account> accounts = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/user/{userId}/primary")
    @PreAuthorize("hasRole('ADMIN') or @userSecurityService.canAccessUser(authentication.name, #userId)")
    public ResponseEntity<Account> getPrimaryAccountByUserId(@PathVariable Long userId) {
        Account account = accountService.getPrimaryAccountByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No primary account found for user: " + userId));
        return ResponseEntity.ok(account);
    }

    @GetMapping("/user/{userId}/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurityService.canAccessUser(authentication.name, #userId)")
    public ResponseEntity<List<Account>> getAccountsByUserIdAndStatus(@PathVariable Long userId, @PathVariable AccountStatus status) {
        List<Account> accounts = accountService.getAccountsByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/type/{accountType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Account>> getAccountsByAccountType(@PathVariable AccountType accountType) {
        List<Account> accounts = accountService.getAccountsByAccountType(accountType);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Account>> getAccountsByStatus(@PathVariable AccountStatus status) {
        List<Account> accounts = accountService.getAccountsByStatus(status);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/verification/{verificationStatus}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Account>> getAccountsByVerificationStatus(@PathVariable VerificationStatus verificationStatus) {
        List<Account> accounts = accountService.getAccountsByVerificationStatus(verificationStatus);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Account>> getAllAccounts(Pageable pageable) {
        Page<Account> accounts = accountService.getAllAccounts(pageable);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Account>> getAllAccountsList() {
        List<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @accountSecurityService.canAccessAccount(authentication.name, #id)")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody Account accountDetails) {
        Account updatedAccount = accountService.updateAccount(id, accountDetails);
        return ResponseEntity.ok(updatedAccount);
    }

    @PutMapping("/{id}/balance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Account> updateBalance(@PathVariable Long id, @RequestBody BigDecimal newBalance) {
        Account account = accountService.updateBalance(id, newBalance);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/{id}/deposit")
    @PreAuthorize("hasRole('ADMIN') or @accountSecurityService.canAccessAccount(authentication.name, #id)")
    public ResponseEntity<Account> deposit(@PathVariable Long id, @RequestBody BigDecimal amount) {
        Account account = accountService.deposit(id, amount);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/{id}/withdraw")
    @PreAuthorize("hasRole('ADMIN') or @accountSecurityService.canAccessAccount(authentication.name, #id)")
    public ResponseEntity<Account> withdraw(@PathVariable Long id, @RequestBody BigDecimal amount) {
        Account account = accountService.withdraw(id, amount);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/{fromAccountId}/transfer/{toAccountId}")
    @PreAuthorize("hasRole('ADMIN') or @accountSecurityService.canAccessAccount(authentication.name, #fromAccountId)")
    public ResponseEntity<Account> transfer(@PathVariable Long fromAccountId, @PathVariable Long toAccountId, @RequestBody BigDecimal amount) {
        Account account = accountService.transfer(fromAccountId, toAccountId, amount);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{id}/set-primary/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurityService.canAccessUser(authentication.name, #userId)")
    public ResponseEntity<Account> setAsPrimaryAccount(@PathVariable Long id, @PathVariable Long userId) {
        Account account = accountService.setAsPrimaryAccount(id, userId);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{id}/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Account> changeStatus(@PathVariable Long id, @PathVariable AccountStatus status) {
        Account account = accountService.changeStatus(id, status);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{id}/verification/{verificationStatus}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Account> changeVerificationStatus(@PathVariable Long id, @PathVariable VerificationStatus verificationStatus) {
        Account account = accountService.changeVerificationStatus(id, verificationStatus);
        return ResponseEntity.ok(account);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @accountSecurityService.canAccessAccount(authentication.name, #id)")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurityService.canAccessUser(authentication.name, #userId)")
    public ResponseEntity<Long> countAccountsByUserId(@PathVariable Long userId) {
        long count = accountService.countAccountsByUserId(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/total-balance/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurityService.canAccessUser(authentication.name, #userId)")
    public ResponseEntity<BigDecimal> getTotalBalanceByUserId(@PathVariable Long userId) {
        BigDecimal totalBalance = accountService.getTotalBalanceByUserId(userId);
        return ResponseEntity.ok(totalBalance);
    }
}
