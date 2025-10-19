package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(String to, String token) {
        String subject = "Xác thực tài khoản của bạn";
        String verifyUrl = "http://localhost:3000/verify?token=" + token;

        String content = "<p>Chào bạn,</p>"
                + "<p>Vui lòng click link dưới đây để xác thực tài khoản:</p>"
                + "<a href=\"" + verifyUrl + "\">Xác thực ngay</a>"
                + "<p>Link sẽ hết hạn sau 15 phút.</p>";
        sendEmail(to, subject, content);
    }

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Đặt lại mật khẩu tài khoản";
        String resetUrl = "http://localhost:3000/reset-password?token=" + token;

        String content = "<p>Chào bạn,</p>"
                + "<p>Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản của mình.</p>"
                + "<p>Vui lòng click link dưới đây để đặt lại mật khẩu:</p>"
                + "<a href=\"" + resetUrl + "\">Đặt lại mật khẩu</a>"
                + "<p>Link sẽ hết hạn sau 15 phút.</p>"
                + "<p><strong>Lưu ý:</strong> Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>";
        sendEmail(to, subject, content);
    }

    @Override
    public void sendNotificationEmail(String to, String title, String content) {
        String emailContent = "<div style=\"font-family: Arial, sans-serif; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">" + title + "</h2>"
                + "<div style=\"background-color: #f5f5f5; padding: 15px; border-left: 4px solid #4CAF50;\">"
                + "<p style=\"margin: 0; color: #555;\">" + content + "</p>"
                + "</div>"
                + "<hr style=\"margin-top: 20px; border: none; border-top: 1px solid #ddd;\">"
                + "<p style=\"color: #888; font-size: 12px;\">Đăng nhập vào hệ thống để xem chi tiết.</p>"
                + "</div>";
        sendEmail(to, title, emailContent);
    }

    private void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new AppException(ErrorCode.EMAIL_SENDING_FAILED, e);
        }
    }
}
