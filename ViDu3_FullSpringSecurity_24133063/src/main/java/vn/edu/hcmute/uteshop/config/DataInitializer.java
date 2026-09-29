package vn.edu.hcmute.uteshop.config;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.hcmute.uteshop.entity.Product;
import vn.edu.hcmute.uteshop.entity.Role;
import vn.edu.hcmute.uteshop.entity.User;
import vn.edu.hcmute.uteshop.repository.ProductRepository;
import vn.edu.hcmute.uteshop.repository.RoleRepository;
import vn.edu.hcmute.uteshop.repository.UserRepository;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initialize(
            RoleRepository roles,
            UserRepository users,
            ProductRepository products,
            PasswordEncoder encoder,
            @Value("${app.demo.seed:false}") boolean seedDemo,
            @Value("${BOOTSTRAP_ADMIN_EMAIL:}") String bootstrapEmail,
            @Value("${BOOTSTRAP_ADMIN_PASSWORD:}") String bootstrapPassword,
            @Value("${BOOTSTRAP_ADMIN_USERNAME:admin}") String bootstrapUsername
    ) {
        return args -> {
            Role userRole = roles.findByName("ROLE_USER")
                    .orElseGet(() -> roles.save(new Role("ROLE_USER")));
            Role adminRole = roles.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roles.save(new Role("ROLE_ADMIN")));
            if (!seedDemo && !bootstrapEmail.isBlank() && !bootstrapPassword.isBlank()) {
                if (bootstrapPassword.length() < 8) {
                    throw new IllegalArgumentException("Mật khẩu khởi tạo ADMIN phải có ít nhất 8 ký tự.");
                }
                addUser(
                        users,
                        encoder,
                        adminRole,
                        bootstrapUsername,
                        bootstrapEmail,
                        "System Administrator",
                        bootstrapPassword
                );
            }
            if (!seedDemo) {
                return;
            }
            User admin = addUser(users, encoder, adminRole,
                    "admin", "admin@uteshop.vn", "Quản trị viên", "Admin@12345");
            User student = addUser(users, encoder, userRole,
                    "tien", "user@uteshop.vn", "Nguyễn Minh Tiến", "Demo@12345");
            if (products.count() == 0) {
                addProduct(products, student, "Tai nghe Aurora", "Âm thanh rõ nét, phong cách tối giản.",
                        "1290000", "/images/product-headphone.svg");
                addProduct(products, student, "Đèn bàn Halo", "Chiếu sáng dịu nhẹ, phù hợp bàn làm việc.",
                        "890000", "/images/product-lamp.svg");
                addProduct(products, admin, "Bàn phím Nova", "Bố cục gọn gàng, cảm giác gõ êm ái.",
                        "1590000", "/images/product-keyboard.svg");
            }
        };
    }

    private User addUser(
            UserRepository users,
            PasswordEncoder encoder,
            Role role,
            String username,
            String email,
            String fullName,
            String password
    ) {
        return users.findByEmailIgnoreCase(email).orElseGet(() -> {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setFullName(fullName);
            user.setRole(role);
            user.setPassword(encoder.encode(password));
            user.setImages("/images/avatar-default.svg");
            user.setEnabled(true);
            return users.save(user);
        });
    }

    private void addProduct(
            ProductRepository products,
            User owner,
            String name,
            String description,
            String price,
            String imageUrl
    ) {
        Product product = new Product();
        product.setUser(owner);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(new BigDecimal(price));
        product.setImageUrl(imageUrl);
        products.save(product);
    }
}
