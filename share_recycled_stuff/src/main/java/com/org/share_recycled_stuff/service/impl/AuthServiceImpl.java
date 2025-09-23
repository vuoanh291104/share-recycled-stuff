package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.constants.AuthConstants;
import com.org.share_recycled_stuff.dto.request.LoginEmailRequest;
import com.org.share_recycled_stuff.dto.request.RegisterRequest;
import com.org.share_recycled_stuff.dto.response.LoginResponse;
import com.org.share_recycled_stuff.dto.response.VerificationResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.User;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.repository.AccountRepository;
import com.org.share_recycled_stuff.repository.UserRepository;
import com.org.share_recycled_stuff.security.jwt.JwtToken;
import com.org.share_recycled_stuff.service.AuthService;
import com.org.share_recycled_stuff.service.EmailService;
import com.org.share_recycled_stuff.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;



@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private EmailService emailService;
    @Value("${app.auth.login.max-attempts:" + AuthConstants.DEFAULT_MAX_LOGIN_ATTEMPTS + "}")
    private int maxLoginAttempts;
    
    @Value("${app.auth.login.lock-duration-minutes:" + AuthConstants.DEFAULT_ACCOUNT_LOCK_DURATION_MINUTES + "}")
    private int accountLockDurationMinutes;
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
                .verificationExpiry(expiresAt)
                .build();

        accountRepository.save(account);

        User user = new User();
        user.setAccount(account);
        user.setFullName(request.getName());
        user.setPhone(request.getPhoneNumber());
        user.setWard(request.getWard());
        user.setCity(request.getCity());

        userRepository.save(user);
        emailService.sendVerificationEmail(account.getEmail(), token);
        return new VerificationResponse(account.getEmail(),expiresAt,token);
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

    @Override
    public LoginResponse loginWithEmailAndPassword(LoginEmailRequest request) {
        log.info("Processing login request for email: {} from IP: {}", request.getEmail(), request.getClientIp());
        
        // Input validation
        if (!StringUtils.hasText(request.getEmail()) || !StringUtils.hasText(request.getPassword())) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        
        String normalizedEmail = request.getEmail().toLowerCase().trim();
        Account account = accountRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> {
                    log.warn("Login attempt with non-existent email: {}", normalizedEmail);
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });
        
        // Check if account is locked
        if (isAccountLocked(account)) {
            LocalDateTime lockUntil = account.getLockedUntil();
            log.warn("Login attempt on locked account: {} (locked until: {})", normalizedEmail, lockUntil);
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            // Increment failed attempts
            incrementFailedAttempts(account);
            log.warn("Invalid password attempt for email: {} from IP: {}", normalizedEmail, request.getClientIp());
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
        
        // Reset login attempts and update login info
        resetLoginAttemptsAndUpdateInfo(account, request.getClientIp());
        
        try {
            // Generate JWT tokens
            CustomUserDetail userDetail = new CustomUserDetail(account, account.getRoles());
            JwtToken accessToken = jwtService.generateTokens(userDetail);
            
            log.info("Login successful for email: {}", normalizedEmail);
            
            return LoginResponse.builder()
                    .tokenType(accessToken.getTokenType())
                    .accessToken(accessToken.getAccessToken())
                    .refreshToken(accessToken.getRefreshToken())
                    .expiresIn(accessToken.getExpiresIn())
                    .userInfo(buildUserInfo(account))
                    .build();
        } catch (Exception e) {
            log.error("Error generating tokens for email: {}", normalizedEmail, e);
            throw new AppException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private boolean isAccountLocked(Account account) {
        if (!account.isLocked()) {
            return false;
        }
        
        // Auto-unlock if lock period has expired
        if (account.getLockedUntil() == null || account.getLockedUntil().isBefore(LocalDateTime.now())) {
            account.setLocked(false);
            account.setLockedUntil(null);
            account.setLockedReason(null);
            account.setLoginAttempts(0);
            accountRepository.save(account);
            log.info("Account auto-unlocked for email: {}", account.getEmail());
            return false;
        }
        
        return true;
    }

    private void incrementFailedAttempts(Account account) {
        int currentAttempts = account.getLoginAttempts() != null ? account.getLoginAttempts() : 0;
        account.setLoginAttempts(currentAttempts + 1);
        
        if (account.getLoginAttempts() >= maxLoginAttempts) {
            LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(accountLockDurationMinutes);
            account.setLocked(true);
            account.setLockedReason(AuthConstants.LOCK_REASON_TOO_MANY_ATTEMPTS);
            account.setLockedAt(LocalDateTime.now());
            account.setLockedUntil(lockUntil);
            log.warn("Account temporarily locked for {} minutes due to too many failed attempts: {}", 
                    accountLockDurationMinutes, account.getEmail());
        }
        
        accountRepository.save(account);
    }

    private void resetLoginAttemptsAndUpdateInfo(Account account, String clientIp) {
        account.setLoginAttempts(0);
        account.setLastLoginAt(LocalDateTime.now());
        account.setLastLoginIp(clientIp);
        
        // Clear lock information on successful login
        if (account.isLocked()) {
            account.setLocked(false);
            account.setLockedUntil(null);
            account.setLockedReason(null);
            account.setLockedAt(null);
            log.info("Account unlocked after successful login: {}", account.getEmail());
        }
        
        accountRepository.save(account);
    }

    private LoginResponse.UserInfo buildUserInfo(Account account) {
        User user = account.getUser();
        String role = account.getRoles().isEmpty() ? "CUSTOMER" :
                account.getRoles().iterator().next().getRoleType().name();

        return LoginResponse.UserInfo.builder()
                .accountId(account.getId())
                .email(account.getEmail())
                .fullName(user != null ? user.getFullName() : null)
                .avatarUrl(user != null ? user.getAvatarUrl() : null)
                .role(role)
                .build();
    }
}
