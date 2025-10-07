package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.UpgradeRequestDTO;
import com.org.share_recycled_stuff.dto.response.UpgradeRequestResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.ProxySellerRequests;
import com.org.share_recycled_stuff.entity.UserRole;
import com.org.share_recycled_stuff.entity.enums.RequestStatus;
import com.org.share_recycled_stuff.entity.enums.Role;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.repository.AccountRepository;
import com.org.share_recycled_stuff.repository.ProxySellerRequestRepository;
import com.org.share_recycled_stuff.service.ProxySellerService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                .createdAt(saved.getCreatedAt())
                .build();
    }
    @Override
    public Page<UpgradeRequestResponse> getAllRequests(Pageable pageable) {
        return requestRepository.findAllRequest(pageable);
    }
    @Override
    public Page<UpgradeRequestResponse> getRequestsByStatus(RequestStatus status, Pageable pageable) {
        return requestRepository.findAllRequest(status, pageable);
    }
    @Override
    public Page<UpgradeRequestResponse> getRequestName(String fullName, Pageable pageable) {
        Page<UpgradeRequestResponse> response = requestRepository.findRequestsByFullName(fullName, pageable);
        if (response == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Không tìm thấy yêu cầu với tên: " + fullName);
        }
        return response;
    }
    @Transactional
    @Override
    public void approveRequest(Long requestId) {
        ProxySellerRequests request = requestRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST, "Không tìm thấy yêu cầu cần duyệt"));
        if(request.getStatus() != RequestStatus.PENDING){
            throw new AppException(ErrorCode.INVALID_REQUEST, "Yêu cầu này không ở trạng thái chờ xử lý");
        }

        request.setStatus(RequestStatus.APPROVED);
        request.setProcessedAt(LocalDateTime.now());
        requestRepository.save(request);

        Account account = request.getAccount();
        UserRole newRole = UserRole.builder()
                .account(account)
                .roleType(Role.PROXY_SELLER)
                .assignedAt(LocalDateTime.now())
                .build();
        account.getRoles().add(newRole);
        accountRepository.save(account);
    }
    @Transactional
    @Override
    public void rejectRequest(Long requestId, String reason) {
        ProxySellerRequests request = requestRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST, "Không tìm thấy yêu cầu cần từ chối"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Yêu cầu này không ở trạng thái chờ xử lý");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(reason);
        request.setProcessedAt(LocalDateTime.now());
        requestRepository.save(request);
    }
}
