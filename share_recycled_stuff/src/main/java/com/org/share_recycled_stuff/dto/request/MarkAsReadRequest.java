package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to mark notifications as read")
public class MarkAsReadRequest {

    @Schema(
            description = "List of notification IDs to mark as read",
            example = "[1, 2, 3]",
            required = true
    )
    @NotEmpty(message = "Danh sách notification IDs không được để trống")
    private List<Long> notificationIds;
}

