package com.org.share_recycled_stuff.dto.request;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DelegationRejectRequest {
    private String reason;
}
