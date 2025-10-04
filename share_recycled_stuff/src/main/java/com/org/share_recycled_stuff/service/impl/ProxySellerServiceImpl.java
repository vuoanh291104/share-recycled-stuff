package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.UpgradeRequestDTO;
import com.org.share_recycled_stuff.dto.response.UpgradeRequestResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.ProxySellerRequests;
import com.org.share_recycled_stuff.entity.enums.RequestStatus;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.repository.AccountRepository;
import com.org.share_recycled_stuff.repository.ProxySellerRequestRepository;
import com.org.share_recycled_stuff.service.ProxySellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProxySellerServiceImpl implements ProxySellerService {
    @Autowired
    private ProxySellerRequestRepository requestRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Override
    public UpgradeRequestResponse createRequest(UpgradeRequestDTO dto,String email){
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        ProxySellerRequests request = ProxySellerRequests.builder()
                .account(account)
                .idCard(dto.getIdCard())
                .idCardFrontImage(dto.getIdCardFrontImage())
                .idCardBackImage(dto.getIdCardBackImage())
                .addressDetail(dto.getAddressDetail())
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        ProxySellerRequests saved = requestRepository.save(request);
        return UpgradeRequestResponse.builder()
                .requestId(saved.getId())
                .idCard(saved.getIdCard())
                .addressDetail(saved.getAddressDetail())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt().toString())
                .build();
    }
}
