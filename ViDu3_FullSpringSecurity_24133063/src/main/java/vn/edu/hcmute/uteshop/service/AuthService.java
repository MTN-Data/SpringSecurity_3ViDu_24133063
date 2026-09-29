package vn.edu.hcmute.uteshop.service;

import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.hcmute.uteshop.dto.RegisterDTO;
import vn.edu.hcmute.uteshop.entity.Role;
import vn.edu.hcmute.uteshop.entity.User;
import vn.edu.hcmute.uteshop.repository.RoleRepository;
import vn.edu.hcmute.uteshop.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final OtpService otp;

    public AuthService(
            UserRepository users,
            RoleRepository roles,
            PasswordEncoder encoder,
            OtpService otp
    ) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
        this.otp = otp;
    }

    @Transactional
    public String register(RegisterDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp.");
        }
        String username = dto.getUsername().trim();
        String email = dto.getEmail().trim().toLowerCase(Locale.ROOT);
        if (users.existsByUsernameIgnoreCase(username) || users.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Username hoặc email đã được sử dụng.");
        }
        Role role = roles.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Chưa có ROLE_USER."));
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullName(dto.getFullName().trim());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setImages("/images/avatar-default.svg");
        user.setEnabled(false);
        user.setRole(role);
        users.save(user);
        return otp.issue(email, OtpService.REGISTER);
    }

    @Transactional
    public boolean verifyRegister(String email, String code) {
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        if (!otp.consume(normalized, OtpService.REGISTER, code)) {
            return false;
        }
        User user = users.findByEmailIgnoreCase(normalized)
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại."));
        user.setEnabled(true);
        return true;
    }

    @Transactional
    public String resendRegister(String email) {
        User user = users.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản chưa được đăng ký."));
        if (user.isEnabled()) {
            throw new IllegalArgumentException("Tài khoản đã được kích hoạt.");
        }
        return otp.issue(user.getEmail(), OtpService.REGISTER);
    }

    @Transactional
    public String forgotPassword(String email) {
        return users.findByEmailIgnoreCase(email.trim())
                .filter(User::isEnabled)
                .map(user -> otp.issue(user.getEmail(), OtpService.RESET))
                .orElse(null);
    }

    @Transactional
    public boolean resetPassword(String email, String code, String newPassword) {
        if (newPassword.length() < 8 || newPassword.length() > 72) {
            throw new IllegalArgumentException("Mật khẩu mới phải có 8–72 ký tự.");
        }
        User user = users.findByEmailIgnoreCase(email.trim())
                .filter(User::isEnabled)
                .orElse(null);
        if (user == null || !otp.consume(user.getEmail(), OtpService.RESET, code)) {
            return false;
        }
        user.setPassword(encoder.encode(newPassword));
        return true;
    }
}
