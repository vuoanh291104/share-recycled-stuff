package com.org.share_recycled_stuff.controller;

import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
public class RegisterController {

    private final AccountService accountService;

    public RegisterController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Account account) {
        accountService.register(account);
        return ResponseEntity.ok("Đăng ký thành công! Vui lòng kiểm tra email để xác nhận.");
    }
}
