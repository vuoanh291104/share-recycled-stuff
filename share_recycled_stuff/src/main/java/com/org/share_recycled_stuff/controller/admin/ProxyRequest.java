package com.org.share_recycled_stuff.controller.admin;

import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.UpgradeRequestResponse;
import com.org.share_recycled_stuff.entity.enums.RequestStatus;
import com.org.share_recycled_stuff.service.ProxySellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Tag(name = "Admin - Proxy Seller Requests", description = "Admin endpoints for managing proxy seller upgrade requests")
@RestController
@RequestMapping("/api/admin/request_proxy")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class ProxyRequest {
    private final ProxySellerService proxySellerService;

    @Operation(
            summary = "Get all upgrade requests",
            description = "Retrieve all proxy seller upgrade requests with pagination"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved upgrade requests. Returns ApiResponse<Page<UpgradeRequestResponse>>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required. Returns ApiResponse with error."
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UpgradeRequestResponse>>> getAllRequests(
            Pageable pageable,
            HttpServletRequest httpRequest
    ) {

        Page<UpgradeRequestResponse> requests = proxySellerService.getAllRequests(pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<UpgradeRequestResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy danh sách yêu cầu proxy seller thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(requests)
                        .build()
        );
    }

    @Operation(
            summary = "Get upgrade requests by status",
            description = "Retrieve proxy seller upgrade requests filtered by status (PENDING/APPROVED/REJECTED)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved filtered upgrade requests. Returns ApiResponse<Page<UpgradeRequestResponse>>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required. Returns ApiResponse with error."
            )
    })
    @GetMapping(params = "status")
    public ResponseEntity<ApiResponse<Page<UpgradeRequestResponse>>> getRequestsByStatus(
            @Parameter(
                    description = "Request status (PENDING, APPROVED, REJECTED)",
                    required = true,
                    example = "PENDING"
            )
            @RequestParam RequestStatus status,
            Pageable pageable,
            HttpServletRequest httpRequest
    ) {

        Page<UpgradeRequestResponse> requests = proxySellerService.getRequestsByStatus(status, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<UpgradeRequestResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy danh sách yêu cầu proxy seller theo trạng thái thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(requests)
                        .build()
        );
    }

    @Operation(
            summary = "Search upgrade requests by name",
            description = "Search proxy seller upgrade requests by user's full name"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved matching upgrade requests. Returns ApiResponse<Page<UpgradeRequestResponse>>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required. Returns ApiResponse with error."
            )
    })
    @GetMapping(params = "fullName")
    public ResponseEntity<ApiResponse<Page<UpgradeRequestResponse>>> getRequestName(
            @Parameter(
                    description = "User full name to search for",
                    required = true,
                    example = "Nguyễn Văn A"
            )
            @RequestParam String fullName,
            Pageable pageable,
            HttpServletRequest httpRequest
    ) {
        Page<UpgradeRequestResponse> request = proxySellerService.getRequestName(fullName, pageable);


        return ResponseEntity.ok(
                ApiResponse.<Page<UpgradeRequestResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy chi tiết yêu cầu proxy seller thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(request)
                        .build()
        );
    }

    @Operation(
            summary = "Approve upgrade request",
            description = "Admin approves a pending upgrade request, promoting the user to PROXY_SELLER role"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Upgrade request approved successfully. User is now PROXY_SELLER."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Upgrade request not found."
            )
    })
    @PostMapping("/{id}/approve")
    public ResponseEntity<Void> approveRequest(
            @Parameter(
                    description = "Upgrade request ID",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {
        proxySellerService.approveRequest(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Reject upgrade request",
            description = "Admin rejects a pending upgrade request with a reason"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Upgrade request rejected successfully."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Upgrade request not found."
            )
    })
    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectRequest(
            @Parameter(
                    description = "Upgrade request ID",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,
            @Parameter(
                    description = "Reason for rejection",
                    required = true,
                    example = "Giấy phép kinh doanh không hợp lệ"
            )
            @RequestParam String reason) {
        proxySellerService.rejectRequest(id, reason);
        return ResponseEntity.ok().build();
    }
}
