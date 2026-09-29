package vn.edu.hcmute.uteshop.controller;

import jakarta.validation.Valid;
import java.util.Objects;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.hcmute.uteshop.dto.ForgotPasswordDTO;
import vn.edu.hcmute.uteshop.dto.RegisterDTO;
import vn.edu.hcmute.uteshop.dto.ResetPasswordDTO;
import vn.edu.hcmute.uteshop.dto.VerifyOtpDTO;
import vn.edu.hcmute.uteshop.service.AuthService;

@Controller
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerDTO") RegisterDTO dto,
            BindingResult errors,
            RedirectAttributes redirect
    ) {
        if (!Objects.equals(dto.getPassword(), dto.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "mismatch", "Mật khẩu không khớp.");
        }
        if (errors.hasErrors()) {
            return "auth/register";
        }
        try {
            String demoOtp = auth.register(dto);
            redirect.addAttribute("email", dto.getEmail());
            redirect.addFlashAttribute("success", "Tạo tài khoản thành công. Hãy xác minh email.");
            if (demoOtp != null) {
                redirect.addFlashAttribute("demoOtp", demoOtp);
            }
            return "redirect:/verify-otp";
        } catch (IllegalArgumentException | IllegalStateException exception) {
            errors.reject("register", exception.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/verify-otp")
    public String verifyPage(@RequestParam(defaultValue = "") String email, Model model) {
        if (!model.containsAttribute("verifyOtpDTO")) {
            VerifyOtpDTO dto = new VerifyOtpDTO();
            dto.setEmail(email);
            model.addAttribute("verifyOtpDTO", dto);
        }
        return "auth/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verify(
            @Valid @ModelAttribute("verifyOtpDTO") VerifyOtpDTO dto,
            BindingResult errors,
            RedirectAttributes redirect
    ) {
        if (errors.hasErrors()) {
            return "auth/verify-otp";
        }
        if (!auth.verifyRegister(dto.getEmail(), dto.getOtp())) {
            errors.reject("otp", "OTP không đúng, hết hạn hoặc vượt quá số lần thử.");
            return "auth/verify-otp";
        }
        redirect.addFlashAttribute("success", "Xác thực thành công. Bạn có thể đăng nhập.");
        return "redirect:/login";
    }

    @PostMapping("/register/resend-otp")
    public String resend(@RequestParam String email, RedirectAttributes redirect) {
        redirect.addAttribute("email", email);
        try {
            String demoOtp = auth.resendRegister(email);
            redirect.addFlashAttribute("success", "Mã OTP mới đã được gửi.");
            if (demoOtp != null) {
                redirect.addFlashAttribute("demoOtp", demoOtp);
            }
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirect.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/verify-otp";
    }

    @GetMapping("/forgot-password")
    public String forgotPage(Model model) {
        if (!model.containsAttribute("forgotPasswordDTO")) {
            model.addAttribute("forgotPasswordDTO", new ForgotPasswordDTO());
        }
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgot(
            @Valid @ModelAttribute("forgotPasswordDTO") ForgotPasswordDTO dto,
            BindingResult errors,
            RedirectAttributes redirect
    ) {
        if (errors.hasErrors()) {
            return "auth/forgot-password";
        }
        try {
            String demoOtp = auth.forgotPassword(dto.getEmail());
            if (demoOtp != null) {
                redirect.addFlashAttribute("demoOtp", demoOtp);
            }
            redirect.addFlashAttribute("success", "Nếu email tồn tại, mã OTP đã được gửi.");
            redirect.addAttribute("email", dto.getEmail());
            return "redirect:/reset-password";
        } catch (IllegalArgumentException | IllegalStateException exception) {
            errors.reject("forgot", exception.getMessage());
            return "auth/forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String resetPage(@RequestParam(defaultValue = "") String email, Model model) {
        if (!model.containsAttribute("resetPasswordDTO")) {
            ResetPasswordDTO dto = new ResetPasswordDTO();
            dto.setEmail(email);
            model.addAttribute("resetPasswordDTO", dto);
        }
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String reset(
            @Valid @ModelAttribute("resetPasswordDTO") ResetPasswordDTO dto,
            BindingResult errors,
            RedirectAttributes redirect
    ) {
        if (!Objects.equals(dto.getPassword(), dto.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "mismatch", "Mật khẩu xác nhận không khớp.");
        }
        if (errors.hasErrors()) {
            return "auth/reset-password";
        }
        try {
            if (!auth.resetPassword(dto.getEmail(), dto.getOtp(), dto.getPassword())) {
                errors.reject("otp", "OTP không hợp lệ hoặc tài khoản không tồn tại.");
                return "auth/reset-password";
            }
            redirect.addFlashAttribute("success", "Đổi mật khẩu thành công. Hãy đăng nhập.");
            return "redirect:/login";
        } catch (IllegalArgumentException exception) {
            errors.reject("reset", exception.getMessage());
            return "auth/reset-password";
        }
    }
}
