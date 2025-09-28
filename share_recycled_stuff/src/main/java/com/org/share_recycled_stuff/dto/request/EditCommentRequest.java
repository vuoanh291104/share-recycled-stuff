package com.org.share_recycled_stuff.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditCommentRequest {
    @NotBlank(message = "Nội dung mới không được để trống")
    private String content;
}
