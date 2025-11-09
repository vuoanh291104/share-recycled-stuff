package com.org.share_recycled_stuff.controller.api;

import com.org.share_recycled_stuff.dto.request.UserSearchRequest;
import com.org.share_recycled_stuff.dto.response.ApiResponse;
import com.org.share_recycled_stuff.dto.response.UserSearchResponse;
import com.org.share_recycled_stuff.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@Tag(name = "Users", description = "User management and search endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Search public users",
            description = "Tìm kiếm và lọc người dùng (bao gồm Proxy Seller) dựa trên tên, địa điểm."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Tìm kiếm thành công. Trả về ApiResponse<Page<UserSearchResponse>>."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Tham số lọc không hợp lệ. Trả về ApiResponse lỗi."
            )
    })
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<UserSearchResponse>>> searchUsers(
            @Parameter(
                    description = "Các tham số lọc. (page, size, keyword, location, isProxySeller)",
                    required = true
            )
            @Valid @ModelAttribute UserSearchRequest filter,
            HttpServletRequest httpRequest
    ) {
        Page<UserSearchResponse> results = userService.searchUsers(filter);

        return ResponseEntity.ok(
                ApiResponse.<Page<UserSearchResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Tìm kiếm người dùng thành công")
                        .path(httpRequest.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(results)
                        .build()
        );
    }

}
