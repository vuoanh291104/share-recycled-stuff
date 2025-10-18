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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Delegation Requests", description = "Delegation request management for proxy sellers")
@RestController
@RequestMapping("/api/delegation-requests")
@RequiredArgsConstructor
public class DelegationController {

    private final DelegationService delegationService;

    @Operation(
            summary = "Create delegation request",
            description = "Customer creates a delegation request to ask proxy seller to sell their item"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Delegation request created successfully. Returns ApiResponse wrapper with DelegationResponse in result field."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data. Returns ApiResponse with error message."
            )
    })
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<ApiResponse<DelegationResponse>> createDelegationRequest(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Delegation request details including post ID and message",
                    required = true
            )
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

    @Operation(
            summary = "Approve delegation request",
            description = "Proxy seller approves a delegation request from customer"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Request approved successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Delegation request not found"
            )
    })
    @PreAuthorize("hasRole('PROXY_SELLER')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveDelegationRequest(
            @Parameter(description = "Delegation request ID", required = true, example = "1")
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

    @Operation(
            summary = "Reject delegation request",
            description = "Proxy seller rejects a delegation request with reason"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Request rejected successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Delegation request not found"
            )
    })
    @PreAuthorize("hasRole('PROXY_SELLER')")
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectDelegationRequest(
            @Parameter(description = "Delegation request ID", required = true, example = "1")
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

    @Operation(
            summary = "Get all delegation requests for current user",
            description = "Customer or Proxy Seller retrieves their own delegation requests with pagination and sorting"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved delegation requests. Returns ApiResponse<Page<DelegationResponse>>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            )
    })
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PROXY_SELLER')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<DelegationResponse>>> getDelegationRequests(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field and direction (field,direction)", example = "createdAt,desc")
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

    @Operation(
            summary = "Get delegation request detail",
            description = "Retrieve detailed information about a specific delegation request by ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved delegation request detail. Returns ApiResponse<DelegationResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Delegation request not found. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            )
    })
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PROXY_SELLER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DelegationResponse>> getDelegationRequestDetail(
            @Parameter(description = "Delegation request ID", required = true, example = "1")
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
