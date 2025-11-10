package com.org.share_recycled_stuff.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Recent chat user information")
public class RecentChatUserResponse {
    
    @Schema(description = "User account ID", example = "1")
    private Long id;

    @Schema(description = "User full name", example = "Nguyễn Văn A")
    private String fullName;

    @Schema(description = "User avatar URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;
}

