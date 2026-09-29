package vn.edu.hcmute.uteshop.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.hcmute.uteshop.entity.OtpToken;
import vn.edu.hcmute.uteshop.repository.OtpTokenRepository;

@Service
public class OtpService {
    public static final String REGISTER = "REGISTER";
    public static final String RESET = "RESET";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final OtpTokenRepository tokens;
    private final PasswordEncoder encoder;
    private final MailService mail;

    public OtpService(
            OtpTokenRepository tokens,
            PasswordEncoder encoder,
            MailService mail
    ) {
        this.tokens = tokens;
        this.encoder = encoder;
        this.mail = mail;
    }

    @Transactional
    public String issue(String email, String type) {
        String normalized = email.trim().toLowerCase(java.util.Locale.ROOT);
        LocalDateTime now = LocalDateTime.now();
        List<OtpToken> active = tokens.findByEmailAndTypeAndUsedFalse(normalized, type);
        boolean tooSoon = active.stream()
                .anyMatch(token -> token.getCreatedAt().plusSeconds(60).isAfter(now));
        if (tooSoon) {
            throw new IllegalArgumentException("Vui lòng chờ 60 giây trước khi gửi lại OTP.");
        }
        for (OtpToken token : active) {
            token.setUsed(true);
        }
        tokens.saveAll(active);

        String otp = String.format("%06d", RANDOM.nextInt(1_000_000));
        OtpToken token = new OtpToken();
        token.setEmail(normalized);
        token.setType(type);
        token.setOtpHash(encoder.encode(otp));
        token.setExpiresAt(now.plusMinutes(5));
        token.setCreatedAt(now);
        tokens.save(token);
        return mail.sendOtp(
                normalized,
                otp,
                REGISTER.equals(type) ? "kích hoạt tài khoản" : "đặt lại mật khẩu"
        );
    }

    @Transactional
    public boolean consume(String email, String type, String input) {
        if (email == null || input == null) {
            return false;
        }
        return tokens.findTopByEmailAndTypeOrderByCreatedAtDesc(
                        email.trim().toLowerCase(java.util.Locale.ROOT),
                        type
                )
                .map(token -> {
                    if (token.isUsed() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
                        return false;
                    }
                    if (token.getAttempts() >= 5) {
                        return false;
                    }
                    if (!encoder.matches(input, token.getOtpHash())) {
                        token.setAttempts(token.getAttempts() + 1);
                        tokens.save(token);
                        return false;
                    }
                    token.setUsed(true);
                    tokens.save(token);
                    return true;
                })
                .orElse(false);
    }
}
