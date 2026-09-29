package vn.edu.hcmute.uteshop.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.edu.hcmute.uteshop.repository.UserRepository;

@Controller
public class PageController {
    private final UserRepository repository;

    public PageController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        model.addAttribute("signedInAs", authentication.getName());
        return "dashboard";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("userCount", repository.count());
        return "admin";
    }
}
