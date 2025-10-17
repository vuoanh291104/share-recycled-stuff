package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.CreateNotificationRequest;
import com.org.share_recycled_stuff.dto.request.MarkAsReadRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.NotificationResponse;
import com.org.share_recycled_stuff.dto.response.UnreadCountResponse;
import com.org.share_recycled_stuff.service.NotificationService;
import com.org.share_recycled_stuff.utils.SseEmitterManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SseEmitterManager sseEmitterManager;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@AuthenticationPrincipal CustomUserDetail userDetail) {
        Long accountId = userDetail.getAccountId();
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        
        sseEmitterManager.addEmitter(accountId, emitter);
        log.info("SSE connection established for account ID: {}", accountId);

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("Connected to notification stream"));
        } catch (IOException e) {
            log.error("Failed to send initial SSE event for account ID: {}", accountId, e);
        }

        emitter.onCompletion(() -> {
            sseEmitterManager.removeEmitter(accountId, emitter);
            log.info("SSE connection completed for account ID: {}", accountId);
        });

        emitter.onTimeout(() -> {
            sseEmitterManager.removeEmitter(accountId, emitter);
            log.info("SSE connection timeout for account ID: {}", accountId);
        });

        emitter.onError((ex) -> {
            sseEmitterManager.removeEmitter(accountId, emitter);
            log.error("SSE connection error for account ID: {}", accountId, ex);
        });

        return emitter;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(
            @Valid @RequestBody CreateNotificationRequest request,
            HttpServletRequest httpRequest) {
        
        NotificationResponse response = notificationService.createNotification(request);
        
        return ResponseEntity.ok(
                ApiResponse.<NotificationResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Tạo thông báo thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(response)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<NotificationResponse> notifications = notificationService
                .getUserNotifications(userDetail.getAccountId(), pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<NotificationResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy danh sách thông báo thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(notifications)
                        .build()
        );
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest) {

        List<NotificationResponse> notifications = notificationService
                .getUnreadNotifications(userDetail.getAccountId());

        return ResponseEntity.ok(
                ApiResponse.<List<NotificationResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy danh sách thông báo chưa đọc thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(notifications)
                        .build()
        );
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<UnreadCountResponse>> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest) {

        Long count = notificationService.getUnreadCount(userDetail.getAccountId());
        UnreadCountResponse response = UnreadCountResponse.builder().count(count).build();

        return ResponseEntity.ok(
                ApiResponse.<UnreadCountResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy số thông báo chưa đọc thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(response)
                        .build()
        );
    }

    @PutMapping("/mark-read")
    public ResponseEntity<ApiResponse<Integer>> markAsRead(
            @Valid @RequestBody MarkAsReadRequest request,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest) {

        int count = notificationService.markAsRead(
                request.getNotificationIds(),
                userDetail.getAccountId()
        );

        return ResponseEntity.ok(
                ApiResponse.<Integer>builder()
                        .code(HttpStatus.OK.value())
                        .message("Đánh dấu đã đọc thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(count)
                        .build()
        );
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<ApiResponse<Integer>> markAllAsRead(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest) {

        int count = notificationService.markAllAsRead(userDetail.getAccountId());

        return ResponseEntity.ok(
                ApiResponse.<Integer>builder()
                        .code(HttpStatus.OK.value())
                        .message("Đánh dấu tất cả đã đọc thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(count)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest) {

        notificationService.deleteNotification(id, userDetail.getAccountId());

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(HttpStatus.OK.value())
                        .message("Xóa thông báo thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotificationsByType(
            @PathVariable Integer type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetail userDetail,
            HttpServletRequest httpRequest) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<NotificationResponse> notifications = notificationService
                .getNotificationsByType(userDetail.getAccountId(), type, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<NotificationResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy danh sách thông báo theo loại thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(notifications)
                        .build()
        );
    }
}

