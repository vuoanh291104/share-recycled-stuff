package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.UpgradeRequestDTO;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.UpgradeRequestResponse;
import com.org.share_recycled_stuff.service.ProxySellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Upgrade Requests", description = "Customer to Proxy Seller upgrade request endpoints")
@RestController
@RequestMapping("/api")
public class UpgradeRequestController {
    @Autowired
    private ProxySellerService proxySellerService;

    @Operation(
            summary = "Request to upgrade to Proxy Seller",
            description = "Customer submits a request to upgrade their account to Proxy Seller role. " +
                    "Requires business license, address, and phone number. Admin will review the request."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Upgrade request created successfully. Returns ApiResponse<UpgradeRequestResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or missing required fields. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Only CUSTOMER role can request upgrade. Returns ApiResponse with error."
            )
    })
    @PostMapping("/upgrade-request")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<UpgradeRequestResponse>> upgradeRequest(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Upgrade request details including business license, address, and phone number",
                    required = true
            )
            @Valid @RequestBody UpgradeRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest request
    ) {
        UpgradeRequestResponse response = proxySellerService.createRequest(dto, userDetail.getEmail());


        return ResponseEntity.ok(
                ApiResponse.<UpgradeRequestResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Gửi yêu cầu nâng cấp thành công")
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now().toString())
                        .result(response)
                        .build()
        );
    }
    @Operation(summary = "Get my upgrade requests", description = "Customer retrieves a list of their own upgrade requests to check the status.")
    @GetMapping("/upgrade-request/my-requests")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<UpgradeRequestResponse>>> getMyUpgradeRequests(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest request
    ) {
        List<UpgradeRequestResponse> response = proxySellerService.getMyRequests(userDetail.getEmail());

        return ResponseEntity.ok(
                ApiResponse.<List<UpgradeRequestResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy danh sách yêu cầu thành công")
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now().toString())
                        .result(response)
                        .build()
        );
    }

}
