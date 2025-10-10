package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.DelegationRequest;
import com.org.share_recycled_stuff.dto.response.DelegationResponse;

public interface DelegationService {
    DelegationResponse createDelegationRequest(DelegationRequest request, Long customerId);
}
