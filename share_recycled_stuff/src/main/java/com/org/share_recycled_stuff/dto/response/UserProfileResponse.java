package com.org.share_recycled_stuff.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.org.share_recycled_stuff.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {
    private Long accountId;
    private Long userId;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String ward;
    private String district;
    private String city;
    private String idCard;
    private String avatarUrl;
    private String bio;
    private BigDecimal ratingAverage;
    private Integer totalRatings;
    private Set<Role> roles;
    private List<UserReviewResponse> reviews;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
