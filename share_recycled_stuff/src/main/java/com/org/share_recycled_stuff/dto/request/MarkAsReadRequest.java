package com.org.share_recycled_stuff.dto.request;

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
public class MarkAsReadRequest {

    @NotEmpty(message = "Danh sách notification IDs không được để trống")
    private List<Long> notificationIds;
}

