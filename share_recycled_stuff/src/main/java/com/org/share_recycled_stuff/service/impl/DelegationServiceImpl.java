package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.DelegationRequest;
import com.org.share_recycled_stuff.dto.response.DelegationResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.DelegationImages;
import com.org.share_recycled_stuff.entity.DelegationRequests;
import com.org.share_recycled_stuff.entity.enums.DelegationRequestsStatus;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.repository.AccountRepository;
import com.org.share_recycled_stuff.repository.DelegationImagesRepository;
import com.org.share_recycled_stuff.repository.DelegationRequestsRepository;
import com.org.share_recycled_stuff.service.DelegationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DelegationServiceImpl implements DelegationService {

    private final DelegationRequestsRepository delegationRequestsRepository;
    private final AccountRepository accountRepository;
    private final DelegationImagesRepository delegationImagesRepository;

    @Override
    public DelegationResponse createDelegationRequest(DelegationRequest request, Long customerId) {
        Account customer = accountRepository.findById(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        Account proxySeller = accountRepository.findById(request.getProxySellerId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        DelegationRequests entity = DelegationRequests.builder()
                .customer(customer)
                .proxySeller(proxySeller)
                .productDescription(request.getProductDescription())
                .expectPrice(request.getExpectPrice())
                .bankAccountNumber(request.getBankAccountNumber())
                .bankName(request.getBankName())
                .accountHolderName(request.getAccountHolderName())
                .status(DelegationRequestsStatus.PENDING)
                .build();

        DelegationRequests saved = delegationRequestsRepository.save(entity);

        Set<String> imageUrls = request.getImageUrls() != null ? request.getImageUrls() : Collections.emptySet();

        int order = 1;
        for (String url : imageUrls) {
            DelegationImages img = new DelegationImages();
            img.setDelegationRequest(saved);
            img.setImageUrl(url);
            img.setDisplayOrder(order++);
            delegationImagesRepository.save(img);
        }
        Set<String> imageUrlSet = delegationImagesRepository.findByDelegationRequestId(saved.getId())
                .stream()
                .map(DelegationImages::getImageUrl)
                .collect(Collectors.toSet());

        return DelegationResponse.builder()
                .id(saved.getId())
                .customerId(saved.getCustomer().getId())
                .proxySellerId(saved.getProxySeller().getId())
                .productDescription(saved.getProductDescription())
                .expectPrice(saved.getExpectPrice())
                .status(saved.getStatus().name())
                .bankAccountNumber(saved.getBankAccountNumber())
                .bankName(saved.getBankName())
                .accountHolderName(saved.getAccountHolderName())
                .imageUrls(imageUrlSet)
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }
}
