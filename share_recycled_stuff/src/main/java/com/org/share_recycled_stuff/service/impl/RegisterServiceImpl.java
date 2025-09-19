package com.org.share_recycled_stuff.service.impl;


import com.org.share_recycled_stuff.dto.request.RegisterRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.repository.AccountRepository;
import com.org.share_recycled_stuff.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final AccountRepository accountRepository;

    @Override
    public ApiResponse<Void> register(RegisterRequest request) {
        Account account = new Account();

        account.setEmail(request.getEmail());
        account.setPassword(request.getPassword());
        accountRepository.save(account);
        return ApiResponse.noti(200, "Đăng ký thành công", "/api/auth/register");
    }
}
