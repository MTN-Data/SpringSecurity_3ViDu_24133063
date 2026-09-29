package vn.edu.hcmute.uteshop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.hcmute.uteshop.entity.Role;
import vn.edu.hcmute.uteshop.entity.User;
import vn.edu.hcmute.uteshop.repository.RoleRepository;
import vn.edu.hcmute.uteshop.repository.UserRepository;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seed(
            RoleRepository roles,
            UserRepository users,
            PasswordEncoder passwordEncoder,
            @Value("${app.demo.seed:false}") boolean demoSeed,
            @Value("${BOOTSTRAP_ADMIN_EMAIL:}") String bootstrapEmail,
            @Value("${BOOTSTRAP_ADMIN_PASSWORD:}") String bootstrapPassword,
            @Value("${BOOTSTRAP_ADMIN_USERNAME:admin}") String bootstrapUsername
    ) {
        return args -> {
            Role userRole = roles.findByName("USER")
                    .orElseGet(() -> roles.save(new Role("USER")));
            Role adminRole = roles.findByName("ADMIN")
                    .orElseGet(() -> roles.save(new Role("ADMIN")));
            if (!demoSeed && !bootstrapEmail.isBlank() && !bootstrapPassword.isBlank()) {
                if (bootstrapPassword.length() < 8) {
                    throw new IllegalArgumentException(
                            "Mật khẩu khởi tạo ADMIN phải có ít nhất 8 ký tự."
                    );
                }
                createUser(
                        users,
                        passwordEncoder,
                        adminRole,
                        bootstrapEmail,
                        "System Administrator",
                        bootstrapUsername,
                        bootstrapPassword
                );
            }
            if (!demoSeed) {
                return;
            }
            createUser(
                    users,
                    passwordEncoder,
                    userRole,
                    "user@uteshop.vn",
                    "Nguyễn Minh Tiến",
                    "tien",
                    "Demo@12345"
            );
            createUser(
                    users,
                    passwordEncoder,
                    adminRole,
                    "admin@uteshop.vn",
                    "Quản trị viên",
                    "admin",
                    "Admin@12345"
            );
        };
    }

    private void createUser(
            UserRepository users,
            PasswordEncoder encoder,
            Role role,
            String email,
            String fullName,
            String username,
            String password
    ) {
        if (users.existsByEmailIgnoreCase(email)) {
            return;
        }
        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPassword(encoder.encode(password));
        user.setEnabled(true);
        user.setRole(role);
        user.setUsername(username);
        user.setImages("/images/avatar-default.svg");
        users.save(user);
    }
}
