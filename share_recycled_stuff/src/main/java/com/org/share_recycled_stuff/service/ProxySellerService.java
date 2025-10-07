package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.UpgradeRequestDTO;
import com.org.share_recycled_stuff.dto.response.UpgradeRequestResponse;
import com.org.share_recycled_stuff.entity.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProxySellerService {
    UpgradeRequestResponse createRequest(UpgradeRequestDTO upgradeRequestDTO, String email);
    Page<UpgradeRequestResponse> getAllRequests(Pageable pageable);

    Page<UpgradeRequestResponse> getRequestsByStatus(RequestStatus status, Pageable pageable);

    Page<UpgradeRequestResponse> getRequestName(String fullName, Pageable pageable);
    void approveRequest(Long requestId);
    void rejectRequest(Long requestId, String reason);
}
