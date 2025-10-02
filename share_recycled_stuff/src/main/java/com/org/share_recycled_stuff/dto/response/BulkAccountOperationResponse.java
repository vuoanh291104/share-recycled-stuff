package com.org.share_recycled_stuff.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkAccountOperationResponse {
    @Builder.Default
    private List<AccountLockResponse> successes = new ArrayList<>();

    @Builder.Default
    private List<AccountOperationError> failures = new ArrayList<>();
}
