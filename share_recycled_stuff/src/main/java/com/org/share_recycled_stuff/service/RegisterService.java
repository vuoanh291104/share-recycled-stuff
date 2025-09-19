package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void register(Account account) {
        // Check email trùng
        if (accountRepository.existsByEmail(account.getEmail())) {
            throw new RuntimeException("Email đã tồn tại!");
        }

        // Gán giá trị mặc định
        account.setVerificationToken(UUID.randomUUID().toString());
        account.setCreatedAt(LocalDateTime.now());
        accountRepository.save(account);

    }
}
