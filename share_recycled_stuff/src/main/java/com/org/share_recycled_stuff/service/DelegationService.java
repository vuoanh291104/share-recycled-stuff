package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.DelegationRequest;
import com.org.share_recycled_stuff.dto.response.DelegationResponse;
import com.org.share_recycled_stuff.dto.response.ProxySellerInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DelegationService {
    DelegationResponse createDelegationRequest(DelegationRequest request, Long customerId);

    void approve(Long requestId, Long proxySellerId, String note);

    void reject(Long requestId, Long proxySellerId, String reason);

    void markAsInTransit(Long delegationId, Long customerId);

    void markAsProductReceived(Long delegationId, Long proxySellerId);

    void markAsSelling(Long delegationId, Long proxySellerId);

    void markAsSold(Long delegationId, Long proxySellerId);

    void markAsPaymentCompleted(Long delegationId, Long proxySellerId);

    Page<DelegationResponse> getDelegationRequests(Long accountId, String role, Pageable pageable);

    DelegationResponse getDelegationRequestDetail(Long id, Long accountId, String role);

    Page<ProxySellerInfoResponse> getAvailableProxySellers(Pageable pageable);
}
