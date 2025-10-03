package com.org.share_recycled_stuff.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkUnlockRequest {
    @NotEmpty(message = "Account IDs are required")
    private List<Long> accountIds;
}
