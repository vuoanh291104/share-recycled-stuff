package com.org.share_recycled_stuff.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRespone {
    private Long id;
    private String name;
    private String email;
    private String numberPhone;
    private Integer gender;
    private boolean isVerified;
    private LocalDateTime createdAt;
}
