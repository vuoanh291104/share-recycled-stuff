package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.DelegationRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.DelegationResponse;
import com.org.share_recycled_stuff.service.DelegationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/delegation-requests")
@RequiredArgsConstructor
public class DelegationController {

    private final DelegationService delegationService;

    @PreAuthorize("hasAnyRole('CUSTOMER', 'PROXY_SELLER')")
    @PostMapping
    public ResponseEntity<ApiResponse<DelegationResponse>> createDelegationRequest(
            @Valid @RequestBody DelegationRequest request,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest
    ) {
        DelegationResponse response = delegationService.createDelegationRequest(request, userDetail.getAccountId());
        return ResponseEntity.ok(
                ApiResponse.<DelegationResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Gửi yêu cầu ủy thác thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(response)
                        .build()
        );
    }
}
