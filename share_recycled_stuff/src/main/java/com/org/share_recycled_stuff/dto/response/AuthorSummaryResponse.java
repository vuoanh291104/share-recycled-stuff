package com.org.share_recycled_stuff.dto.response;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Thông tin tóm tắt về người đăng bài")
public class AuthorSummaryResponse {

    @Schema(description = "ID của người đăng", example = "12")
    private Long id;

    @Schema(description = "Tên hiển thị", example = "Nguyễn Văn A")
    private String displayName;

    @Schema(description = "URL ảnh đại diện", example = "https://image.com/avatar.jpg")
    private String avatarUrl;
}
