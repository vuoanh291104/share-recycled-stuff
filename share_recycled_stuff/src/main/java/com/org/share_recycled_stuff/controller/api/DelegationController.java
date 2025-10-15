package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.DelegationApproveRequest;
import com.org.share_recycled_stuff.dto.request.DelegationRejectRequest;
import com.org.share_recycled_stuff.dto.request.DelegationRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.DelegationResponse;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.service.DelegationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/delegation-requests")
@RequiredArgsConstructor
public class DelegationController {

    private final DelegationService delegationService;

    @PreAuthorize("hasAnyRole('CUSTOMER')")
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
    @PreAuthorize("hasRole('PROXY_SELLER')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveDelegationRequest(
            @PathVariable Long id,
            @RequestBody(required = false) DelegationApproveRequest request,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest
    ) {
        delegationService.approve(
                id,
                userDetail.getAccountId(),
                request != null ? request.getNote() : null
        );
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(HttpStatus.OK.value())
                        .message("Phê duyệt yêu cầu ủy thác thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }
    @PreAuthorize("hasRole('PROXY_SELLER')")
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectDelegationRequest(
            @PathVariable Long id,
            @RequestBody DelegationRejectRequest request,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest
    ) {
        delegationService.reject(
                id,
                userDetail.getAccountId(),
                request.getReason()
        );
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(HttpStatus.OK.value())
                        .message("Từ chối yêu cầu ủy thác thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PROXY_SELLER')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<DelegationResponse>>> getDelegationRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest
    ) {
        String[] sortParams = sort.split(",");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]));
        String role = extractRole(userDetail);
        Page<DelegationResponse> responses = delegationService.getDelegationRequests(
                userDetail.getAccountId(),
                role,
                pageable
        );

        return ResponseEntity.ok(
                ApiResponse.<Page<DelegationResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy danh sách yêu cầu ủy thác thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(responses)
                        .build()
        );
    }
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PROXY_SELLER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DelegationResponse>> getDelegationRequestDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest
    ) {
        String role = extractRole(userDetail);

        DelegationResponse response = delegationService.getDelegationRequestDetail(
                id, userDetail.getAccountId(), role
        );

        return ResponseEntity.ok(
                ApiResponse.<DelegationResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Xem chi tiết yêu cầu ủy thác thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(response)
                        .build()
        );
    }
    private String extractRole(CustomUserDetail userDetail) {
        List<String> roles = userDetail.getAuthorities().stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .toList();

        if (roles.contains("PROXY_SELLER")) {
            return "PROXY_SELLER";
        }

        if (roles.contains("CUSTOMER")) {
            return "CUSTOMER";
        }

        throw new AppException(ErrorCode.ACCESS_DENIED, "Không tìm thấy vai trò hợp lệ của người dùng");
    }
}
