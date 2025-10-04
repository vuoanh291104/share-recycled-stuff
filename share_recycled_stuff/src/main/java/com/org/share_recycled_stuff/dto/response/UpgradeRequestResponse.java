package com.org.share_recycled_stuff.dto.response;

import com.org.share_recycled_stuff.entity.enums.RequestStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpgradeRequestResponse {
    private Long requestId;
    private String idCard;
    private String addressDetail;
    private RequestStatus status;
    private String createdAt;
}
