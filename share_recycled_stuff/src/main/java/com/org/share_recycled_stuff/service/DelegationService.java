package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.DelegationRequest;
import com.org.share_recycled_stuff.dto.response.DelegationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DelegationService {
    DelegationResponse createDelegationRequest(DelegationRequest request, Long customerId);

    void approve(Long requestId, Long proxySellerId,String note);
    void reject(Long requestId, Long proxySellerId, String reason);

    Page<DelegationResponse> getDelegationRequests(Long accountId, String role, Pageable pageable);
    DelegationResponse getDelegationRequestDetail(Long id, Long accountId, String role);
}
