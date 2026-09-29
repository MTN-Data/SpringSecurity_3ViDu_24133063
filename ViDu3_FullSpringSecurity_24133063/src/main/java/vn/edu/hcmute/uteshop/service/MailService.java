package vn.edu.hcmute.uteshop.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final ObjectProvider<JavaMailSender> senderProvider;
    private final boolean enabled;
    private final boolean showDemoOtp;

    public MailService(
            ObjectProvider<JavaMailSender> senderProvider,
            @Value("${app.mail.enabled:false}") boolean enabled,
            @Value("${app.demo.show-otp:false}") boolean showDemoOtp
    ) {
        this.senderProvider = senderProvider;
        this.enabled = enabled;
        this.showDemoOtp = showDemoOtp;
    }

    public String sendOtp(String email, String otp, String purpose) {
        if (!enabled && showDemoOtp) {
            // Chỉ trong chế độ demo: OTP được trả về để giao diện hiển thị tại máy cục bộ.
            return otp;
        }
        if (!enabled) {
            throw new IllegalStateException("SMTP chưa được cấu hình.");
        }
        JavaMailSender sender = senderProvider.getIfAvailable();
        if (sender == null) {
            throw new IllegalStateException("Không tìm thấy cấu hình gửi email.");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("UTEShop - Mã OTP " + purpose);
        message.setText(
                "Mã OTP của bạn là: " + otp
                        + "\nMã có hiệu lực 5 phút. Nếu không yêu cầu, vui lòng bỏ qua email."
        );
        sender.send(message);
        return null;
    }
}
