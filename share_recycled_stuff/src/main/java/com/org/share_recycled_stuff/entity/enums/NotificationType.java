package com.org.share_recycled_stuff.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    COMMENT(1, "Bình luận mới"),
    REPLY(2, "Phản hồi bình luận"),
    POST_APPROVED(3, "Bài viết được duyệt"),
    POST_REJECTED(4, "Bài viết bị từ chối"),
    POST_DELETED(5, "Bài viết bị xóa"),
    ACCOUNT_LOCKED(6, "Tài khoản bị khóa"),
    ACCOUNT_UNLOCKED(7, "Tài khoản được mở khóa"),
    ACCOUNT_WARNING(8, "Cảnh báo tài khoản"),
    PROXY_APPROVED(9, "Yêu cầu nâng cấp được duyệt"),
    PROXY_REJECTED(10, "Yêu cầu nâng cấp bị từ chối"),
    DELEGATION_APPROVED(11, "Yêu cầu ký gửi được chấp nhận"),
    DELEGATION_REJECTED(12, "Yêu cầu ký gửi bị từ chối"),
    REPORT_PROCESSED(13, "Báo cáo được xử lý"),
    ADMIN_MESSAGE(14, "Thông báo từ quản trị viên");

    private final int code;
    private final String displayName;

    public static NotificationType fromCode(int code) {
        return Arrays.stream(values())
                .filter(type -> type.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid notification type code: " + code));
    }
}
