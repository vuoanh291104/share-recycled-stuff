package com.org.share_recycled_stuff.service.impl;


import com.org.share_recycled_stuff.dto.request.RegisterRequest;
import com.org.share_recycled_stuff.dto.response.VerificationResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.User;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.repository.AccountRepository;
import com.org.share_recycled_stuff.repository.UserRepository;
import com.org.share_recycled_stuff.service.AuthService;
import com.org.share_recycled_stuff.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;



@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository  userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    @Override
    public VerificationResponse register(RegisterRequest request) {

        Optional<Account> existingAccountOpt = accountRepository.findByEmail(request.getEmail());
        Optional<User> existingUserOpt = userRepository.findByPhone(request.getPhoneNumber());


        if (existingUserOpt.isPresent() || existingAccountOpt.isPresent()) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15); // token hết hạn sau 15p

        Account account = Account.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isVerified(false)
                .verificationToken(token)
//                .verificationExpiry(expiresAt)
                .build();

        accountRepository.save(account);

        User user = new User();
        user.setAccount(account);
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhoneNumber());
        user.setWard(request.getWard());
        user.setCity(request.getCity());

        userRepository.save(user);

        emailService.sendVerificationEmail(account.getEmail(), token);
        return new VerificationResponse(account.getEmail(), token,expiresAt);

    }
    @Override
    public String verifyAccount(String token) {
        Account account = accountRepository.findByVerificationToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));

        account.setVerified(true);
        account.setVerificationToken(null);
        accountRepository.save(account);

        return "Xác thực thành công";
    }


}
