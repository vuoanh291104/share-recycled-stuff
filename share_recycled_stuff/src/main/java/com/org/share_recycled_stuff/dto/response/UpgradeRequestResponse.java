package com.org.share_recycled_stuff.dto.response;

import com.org.share_recycled_stuff.entity.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpgradeRequestResponse {
    private Long requestId;
    private String fullName;
    private String email;
    private String idCard;
    private String addressDetail;
    private RequestStatus status;
    private LocalDateTime createdAt;
}
