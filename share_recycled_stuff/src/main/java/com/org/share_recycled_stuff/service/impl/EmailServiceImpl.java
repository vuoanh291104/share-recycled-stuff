package com.org.share_recycled_stuff.service.impl;

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
        String verificationUrl = "http://localhost:8080/api/auth/verify?token=" + token;

        String content = "<p>Chào bạn,</p>"
                + "<p>Vui lòng click link dưới đây để xác thực tài khoản:</p>"
                + "<a href=\"" + verificationUrl + "\">Xác thực ngay</a>"
                + "<p>Link sẽ hết hạn sau 15 phút.</p>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true = gửi HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Gửi email thất bại", e);
        }
    }
}
