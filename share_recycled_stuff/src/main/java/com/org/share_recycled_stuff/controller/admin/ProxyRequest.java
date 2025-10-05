package com.org.share_recycled_stuff.controller.admin;

import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.UpgradeRequestResponse;
import com.org.share_recycled_stuff.entity.enums.RequestStatus;
import com.org.share_recycled_stuff.service.ProxySellerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/admin/request_proxy")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class ProxyRequest{
    private final ProxySellerService proxySellerService;
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UpgradeRequestResponse>>> getAllRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            HttpServletRequest httpRequest
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

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
    @GetMapping(params = "status")
    public ResponseEntity<ApiResponse<Page<UpgradeRequestResponse>>> getRequestsByStatus(
            @RequestParam RequestStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            HttpServletRequest httpRequest
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

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
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UpgradeRequestResponse>> getRequestById(
            @PathVariable Long id,
            HttpServletRequest httpRequest
    ) {
        UpgradeRequestResponse request = proxySellerService.getRequestDetail(id);

        return ResponseEntity.ok(
                ApiResponse.<UpgradeRequestResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy chi tiết yêu cầu proxy seller thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(request)
                        .build()
        );
    }
}
