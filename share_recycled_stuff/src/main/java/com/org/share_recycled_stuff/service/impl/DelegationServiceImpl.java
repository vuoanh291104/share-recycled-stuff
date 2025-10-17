package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.DelegationRequest;
import com.org.share_recycled_stuff.dto.response.DelegationResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.ApprovedDelegationRequests;
import com.org.share_recycled_stuff.entity.DelegationImages;
import com.org.share_recycled_stuff.entity.DelegationRequests;
import com.org.share_recycled_stuff.entity.enums.DelegationRequestsStatus;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.repository.AccountRepository;
import com.org.share_recycled_stuff.repository.ApprovedDelegationRequestsRepository;
import com.org.share_recycled_stuff.repository.DelegationImagesRepository;
import com.org.share_recycled_stuff.repository.DelegationRequestsRepository;
import com.org.share_recycled_stuff.mapper.DelegationMapper;
import com.org.share_recycled_stuff.service.DelegationService;
import com.org.share_recycled_stuff.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class DelegationServiceImpl implements DelegationService {

    private final DelegationRequestsRepository delegationRequestsRepository;
    private final AccountRepository accountRepository;
    private final DelegationImagesRepository delegationImagesRepository;
    private final ApprovedDelegationRequestsRepository approvedDelegationRequestsRepository;
    private final NotificationService notificationService;
    private final DelegationMapper delegationMapper;
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

        if (!imageUrls.isEmpty()) {
            List<DelegationImages> imagesToSave = new ArrayList<>();
            int order = 1;
            for (String url : imageUrls) {
                DelegationImages img = new DelegationImages();
                img.setDelegationRequest(saved);
                img.setImageUrl(url);
                img.setDisplayOrder(order++);
                imagesToSave.add(img);
            }
            delegationImagesRepository.saveAll(imagesToSave);
        }

        DelegationRequests savedWithImages = delegationRequestsRepository.findById(saved.getId())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST));

        return delegationMapper.toResponse(savedWithImages);
    }
    @Transactional
    @Override
    public void approve(Long delegationId, Long proxySellerId, String note) {
        DelegationRequests request = delegationRequestsRepository.findById(delegationId)
                .orElseThrow(() -> new AppException(ErrorCode. INVALID_REQUEST));

        if (request.getStatus() != DelegationRequestsStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Yêu cầu không ở trạng thái chờ duyệt");
        }

        if (!request.getProxySeller().getId().equals(proxySellerId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "Bạn không có quyền duyệt yêu cầu này");
        }

        request.setStatus(DelegationRequestsStatus.APPROVED);
        delegationRequestsRepository.save(request);

        ApprovedDelegationRequests approved = ApprovedDelegationRequests.builder()
                .delegationRequest(request)
                .approvedBy(request.getProxySeller())
                .note(note)
                .build();

        approvedDelegationRequestsRepository.save(approved);

        String approvalMessage = note != null && !note.trim().isEmpty()
                ? String.format("Yêu cầu ký gửi sản phẩm \"%s\" của bạn đã được chấp nhận. Ghi chú: %s", 
                    request.getProductDescription(), note)
                : String.format("Yêu cầu ký gửi sản phẩm \"%s\" của bạn đã được chấp nhận.", 
                    request.getProductDescription());
        
        notificationService.createNotification(
                request.getCustomer().getId(),
                "Yêu cầu ký gửi được chấp nhận",
                approvalMessage,
                11,
                3,
                "DelegationRequest",
                delegationId
        );
    }

    @Transactional
    @Override
    public void reject(Long requestId, Long proxySellerId, String reason) {
        DelegationRequests request = delegationRequestsRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST));
        if (!request.getProxySeller().getId().equals(proxySellerId)) {
            throw new AppException(ErrorCode. USER_NOT_FOUND);
        }
        if (request.getStatus() != DelegationRequestsStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        request.setStatus(DelegationRequestsStatus.REJECTED);
        request.setRejectionReason(reason);
        delegationRequestsRepository.save(request);

        String rejectionMessage = reason != null && !reason.trim().isEmpty()
                ? String.format("Yêu cầu ký gửi sản phẩm \"%s\" của bạn đã bị từ chối. Lý do: %s", 
                    request.getProductDescription(), reason)
                : String.format("Yêu cầu ký gửi sản phẩm \"%s\" của bạn đã bị từ chối.", 
                    request.getProductDescription());
        
        notificationService.createNotification(
                request.getCustomer().getId(),
                "Yêu cầu ký gửi bị từ chối",
                rejectionMessage,
                12,
                3,
                "DelegationRequest",
                requestId
        );
    }
    @Transactional
    @Override
    public Page<DelegationResponse> getDelegationRequests(Long accountId, String role, Pageable pageable) {
        Page<DelegationRequests> page;

        if ("PROXY_SELLER".equalsIgnoreCase(role)) {
            page = delegationRequestsRepository.findByProxySellerId(accountId, pageable);
        } else if ("CUSTOMER".equalsIgnoreCase(role)) {
            page = delegationRequestsRepository.findByCustomerId(accountId, pageable);
        } else {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        return page.map(delegationMapper::toResponse);
    }
    @Transactional
    @Override
    public DelegationResponse getDelegationRequestDetail(Long id, Long accountId, String role) {
        DelegationRequests request = delegationRequestsRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST, "Không tìm thấy yêu cầu ủy thác"));

        if ("CUSTOMER".equalsIgnoreCase(role) && !request.getCustomer().getId().equals(accountId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        if ("PROXY_SELLER".equalsIgnoreCase(role) && !request.getProxySeller().getId().equals(accountId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        return delegationMapper.toResponse(request);
    }
}
