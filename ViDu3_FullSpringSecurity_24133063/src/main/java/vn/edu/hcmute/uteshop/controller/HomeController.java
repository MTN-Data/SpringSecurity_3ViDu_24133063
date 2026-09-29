package vn.edu.hcmute.uteshop.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.edu.hcmute.uteshop.security.CustomUserDetails;
import vn.edu.hcmute.uteshop.service.ProductService;
import vn.edu.hcmute.uteshop.service.UserService;

@Controller
public class HomeController {
    private final ProductService products;
    private final UserService users;

    public HomeController(ProductService products, UserService users) {
        this.products = products;
        this.users = users;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("userCount", users.count());
        model.addAttribute("productCount", products.countAll());
        model.addAttribute("recentProducts", products.recent());
        return "home";
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Model model,
            @AuthenticationPrincipal CustomUserDetails current
    ) {
        boolean admin = current.getAuthorities().stream()
                .anyMatch(role -> "ROLE_ADMIN".equals(role.getAuthority()));
        model.addAttribute("myProductCount", products.countFor(current.getId()));
        model.addAttribute("userCount", users.count());
        model.addAttribute("productCount", products.countAll());
        model.addAttribute("admin", admin);
        return "dashboard";
    }

    @GetMapping("/access-denied")
    public String denied() {
        return "access-denied";
    }
}
