package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.UpgradeRequestDTO;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.UpgradeRequestResponse;
import com.org.share_recycled_stuff.service.ProxySellerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class UpgradeRequestController {
    @Autowired
    private ProxySellerService proxySellerService;

    @PostMapping("/upgrade-request")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<UpgradeRequestResponse>> upgradeRequest(
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
}
