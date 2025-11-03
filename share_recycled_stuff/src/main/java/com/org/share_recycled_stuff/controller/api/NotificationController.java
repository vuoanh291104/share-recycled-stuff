package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.config.CustomUserDetail;
import com.org.share_recycled_stuff.dto.request.CreateNotificationRequest;
import com.org.share_recycled_stuff.dto.request.MarkAsReadRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.NotificationResponse;
import com.org.share_recycled_stuff.dto.response.UnreadCountResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.repository.AccountRepository;
import com.org.share_recycled_stuff.service.NotificationService;
import com.org.share_recycled_stuff.utils.SseEmitterManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Notifications", description = "Real-time notifications and SSE streaming endpoints")
@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SseEmitterManager sseEmitterManager;
    private final AccountRepository accountRepository;

    @Operation(
            summary = "Stream real-time notifications",
            description = "Establish SSE (Server-Sent Events) connection for real-time notification streaming. Connection timeout: 30 minutes."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "SSE stream established successfully. Returns Server-Sent Events stream (NOT ApiResponse wrapper)."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required."
            )
    })
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@AuthenticationPrincipal CustomUserDetail userDetail) {
        Long accountId = userDetail.getAccountId();
        
        // Check if account is locked before establishing SSE connection
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
            
        if (account.isCurrentlyLocked()) {
            log.warn("Locked account attempting SSE connection: {}", accountId);
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }
        
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

    @Operation(
            summary = "Create notification (Admin)",
            description = "Admin creates and broadcasts a notification to specific user or all users"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Notification created successfully. Returns ApiResponse<NotificationResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required. Returns ApiResponse with error."
            )
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Notification details including recipient and message",
                    required = true
            )
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

    @Operation(
            summary = "Get all notifications",
            description = "Retrieve paginated list of all notifications for current user with sorting"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved notifications. Returns ApiResponse<Page<NotificationResponse>>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            )
    })
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

    @Operation(
            summary = "Get unread notifications",
            description = "Retrieve all unread notifications for current user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved unread notifications. Returns ApiResponse<List<NotificationResponse>>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            )
    })
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

    @Operation(
            summary = "Get unread notification count",
            description = "Get the count of unread notifications for current user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved unread count. Returns ApiResponse<UnreadCountResponse>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            )
    })
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

    @Operation(
            summary = "Mark notifications as read",
            description = "Mark specific notifications as read by providing their IDs"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully marked as read. Returns ApiResponse<Integer> with count of updated notifications."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            )
    })
    @PutMapping("/mark-read")
    public ResponseEntity<ApiResponse<Integer>> markAsRead(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "List of notification IDs to mark as read",
                    required = true
            )
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

    @Operation(
            summary = "Mark all notifications as read",
            description = "Mark all unread notifications as read for current user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully marked all as read. Returns ApiResponse<Integer> with count of updated notifications."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            )
    })
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

    @Operation(
            summary = "Delete notification",
            description = "Delete a specific notification by ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully deleted notification. Returns ApiResponse<Void>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Notification not found. Returns ApiResponse with error."
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @io.swagger.v3.oas.annotations.Parameter(
                    description = "Notification ID to delete",
                    required = true,
                    example = "1"
            )
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

    @Operation(
            summary = "Get notifications by type",
            description = "Retrieve paginated notifications filtered by type for current user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved notifications by type. Returns ApiResponse<Page<NotificationResponse>>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required. Returns ApiResponse with error."
            )
    })
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotificationsByType(
            @io.swagger.v3.oas.annotations.Parameter(
                    description = "Notification type code (1=SYSTEM, 2=POST_STATUS, 3=COMMENT, etc.)",
                    required = true,
                    example = "2"
            )
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

