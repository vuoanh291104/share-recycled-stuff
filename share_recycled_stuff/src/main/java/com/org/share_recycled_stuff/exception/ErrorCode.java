package com.org.share_recycled_stuff.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Authentication & Authorization
    UNAUTHORIZED("AUTH_001", "Truy cập không được phép", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("AUTH_002", "Truy cập bị từ chối", HttpStatus.FORBIDDEN),
    INVALID_TOKEN("AUTH_003", "Token không hợp lệ", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("AUTH_004", "Token đã hết hạn", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS("AUTH_005", "Tên đăng nhập hoặc mật khẩu không hợp lệ", HttpStatus.UNAUTHORIZED),
    ACCOUNT_DISABLED("AUTH_006", "Tài khoản đã bị vô hiệu hóa", HttpStatus.UNAUTHORIZED),
    ACCOUNT_LOCKED("AUTH_007", "Tài khoản đã bị khóa", HttpStatus.UNAUTHORIZED),
    ACCOUNT_ALREADY_VERIFIED("AUTH_008", "Tài khoản đã được xác minh", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH("AUTH_009", "Mật khẩu không khớp", HttpStatus.BAD_REQUEST),
    INVALID_RESET_TOKEN("AUTH_010", "Token đặt lại mật khẩu không hợp lệ hoặc đã hết hạn", HttpStatus.BAD_REQUEST),
    RESET_TOKEN_EXPIRED("AUTH_011", "Token đặt lại mật khẩu đã hết hạn", HttpStatus.BAD_REQUEST),
    PASSWORD_CANNOT_BE_SAME("AUTH_012", "Mật khẩu mới không được trùng với mật khẩu hiện tại", HttpStatus.BAD_REQUEST),
    INVALID_CURRENT_PASSWORD("AUTH_013", "Mật khẩu hiện tại không chính xác", HttpStatus.BAD_REQUEST),
    // User related
    USER_NOT_FOUND("USER_001", "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("USER_002", "Người dùng đã tồn tại", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS("USER_003", "Email đã tồn tại", HttpStatus.CONFLICT),
    USERNAME_ALREADY_EXISTS("USER_004", "Tên đăng nhập đã tồn tại", HttpStatus.CONFLICT),
    ACCOUNT_NOT_FOUND("USER_005", "Không tìm thấy tài khoản", HttpStatus.NOT_FOUND),
    USER_ALREADY_HAS_ROLE("USER_006", "Người dùng đã có vai trò này", HttpStatus.CONFLICT),
    USER_DOES_NOT_HAVE_ROLE("USER_007", "Người dùng không có vai trò này", HttpStatus.BAD_REQUEST),
    CANNOT_REMOVE_LAST_ADMIN("USER_008", "Không thể xóa vai trò quản trị viên cuối cùng", HttpStatus.BAD_REQUEST),
    USER_MUST_HAVE_AT_LEAST_ONE_ROLE("USER_009", "Người dùng phải có ít nhất một vai trò", HttpStatus.BAD_REQUEST),

    // Item/Product related
    ITEM_NOT_FOUND("ITEM_001", "Không tìm thấy vật phẩm", HttpStatus.NOT_FOUND),
    ITEM_NOT_AVAILABLE("ITEM_002", "Vật phẩm không có sẵn", HttpStatus.BAD_REQUEST),
    ITEM_ALREADY_SHARED("ITEM_003", "Vật phẩm đã được chia sẻ", HttpStatus.CONFLICT),
    //Category
    CATEGORY_NOT_FOUND("CATEGORY_001", "Không tìm thấy danh mục", HttpStatus.NOT_FOUND),

    //Post
    POST_NOT_FOUND("POST_001", "Không tìm thấy bài đăng", HttpStatus.NOT_FOUND),
    POST_ALREADY_DELETED("POST_002", "Bài đăng đã bị xóa", HttpStatus.GONE),
    POST_NOT_DELETED("POST_003", "Bài đăng chưa bị xóa", HttpStatus.BAD_REQUEST),
    INVALID_STATUS_TRANSITION("POST_004", "Chuyển đổi trạng thái không hợp lệ", HttpStatus.BAD_REQUEST),

    //Image
    IMAGE_NOT_FOUND("IMAGE_001", "Không tìm thấy hình ảnh", HttpStatus.NOT_FOUND),

    //Review
    REVIEW_NOT_FOUND("REVIEW_001", "Không tìm thấy đánh giá", HttpStatus.NOT_FOUND),
    CANNOT_DELETE_REVIEW("REVIEW_002", "Bạn không có quyền xóa đánh giá này", HttpStatus.FORBIDDEN),
    REVIEW_ALREADY_EXISTS("REVIEW_003", "Bạn đã gửi đánh giá cho đối tượng này", HttpStatus.CONFLICT),

    //Report
    REPORT_NOT_FOUND("REPORT_001", "Không tìm thấy báo cáo", HttpStatus.NOT_FOUND),
    ALREADY_REPORTED_POST("REPORT_002", "Bạn đã báo cáo bài đăng này", HttpStatus.CONFLICT),
    ALREADY_REPORTED_USER("REPORT_003", "Bạn đã báo cáo người dùng này", HttpStatus.CONFLICT),
    INVALID_REPORT_TYPE("REPORT_004", "Loại báo cáo không hợp lệ", HttpStatus.BAD_REQUEST),
    CANNOT_REPORT_OWN_POST("REPORT_005", "Không thể báo cáo bài đăng của chính mình", HttpStatus.BAD_REQUEST),
    CANNOT_REPORT_YOURSELF("REPORT_006", "Không thể báo cáo chính mình", HttpStatus.BAD_REQUEST),
    INVALID_REPORT_TARGET("REPORT_007", "Đối tượng báo cáo không hợp lệ - phải chỉ định bài đăng hoặc tài khoản", HttpStatus.BAD_REQUEST),

    // Payment
    VNPAY_URL_CREATION_FAILED("PAY_001", "Lỗi tạo URL thanh toán VNPay", HttpStatus.INTERNAL_SERVER_ERROR),
    VNPAY_INVALID_SIGNATURE("PAY_002", "Chữ ký VNPay không hợp lệ", HttpStatus.BAD_REQUEST),
    VNPAY_VALIDATION_FAILED("PAY_003", "Lỗi xác thực callback từ VNPay", HttpStatus.INTERNAL_SERVER_ERROR),
    VNPAY_INVALID_TCODE("PAY_004", "Mã TmnCode (website) không hợp lệ", HttpStatus.BAD_REQUEST),

    //Notification
    NOTIFICATION_NOT_FOUND("NOTIFICATION_001", "Không tìm thấy thông báo", HttpStatus.NOT_FOUND),

    // Validation
    INVALID_INPUT("VAL_001", "Đầu vào không hợp lệ: %s", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_FIELD("VAL_002", "Thiếu trường bắt buộc: %s", HttpStatus.BAD_REQUEST),
    INVALID_FILE_FORMAT("VAL_003", "Định dạng tệp tin không hợp lệ", HttpStatus.BAD_REQUEST),
    FILE_SIZE_EXCEEDED("VAL_004", "Kích thước tệp tin vượt quá giới hạn", HttpStatus.BAD_REQUEST),
    TYPE_MISMATCH("VAL_005", "Không đúng kiểu dữ liệu", HttpStatus.BAD_REQUEST),

    // Business logic
    RESOURCE_NOT_FOUND("BUS_001", "Không tìm thấy tài nguyên: %s", HttpStatus.NOT_FOUND),
    OPERATION_NOT_ALLOWED("BUS_002", "Thao tác không được phép: %s", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST("BUS_003", "Yêu cầu không hợp lệ", HttpStatus.BAD_REQUEST),

    // System
    INTERNAL_ERROR("SYS_001", "Lỗi máy chủ nội bộ", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE("SYS_002", "Dịch vụ tạm thời không khả dụng", HttpStatus.SERVICE_UNAVAILABLE),
    DATABASE_ERROR("SYS_003", "Đã xảy ra lỗi cơ sở dữ liệu", HttpStatus.INTERNAL_SERVER_ERROR),
    METHOD_NOT_SUPPORTED("SYS_004", "Phương thức HTTP không được hỗ trợ", HttpStatus.METHOD_NOT_ALLOWED),
    SYSTEM_RESOURCE_NOT_FOUND("SYS_005", "Không tìm thấy tài nguyên được yêu cầu", HttpStatus.NOT_FOUND),
    EMAIL_SENDING_FAILED("SYS_006", "Gửi email thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    DATA_INTEGRITY_ERROR("SYS_007", "Lỗi toàn vẹn dữ liệu", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
