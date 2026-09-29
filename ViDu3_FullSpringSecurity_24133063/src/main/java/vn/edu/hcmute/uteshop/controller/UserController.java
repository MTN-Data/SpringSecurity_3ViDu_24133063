package vn.edu.hcmute.uteshop.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.hcmute.uteshop.dto.UserDTO;
import vn.edu.hcmute.uteshop.dto.UserForm;
import vn.edu.hcmute.uteshop.security.CustomUserDetails;
import vn.edu.hcmute.uteshop.service.UserService;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Page<UserDTO> result = users.search(keyword, page);
        model.addAttribute("users", result);
        model.addAttribute("keyword", keyword);
        return "users/list";
    }

    @GetMapping("/create")
    public String createPage(Model model) {
        model.addAttribute("userForm", new UserForm());
        model.addAttribute("editing", false);
        return "users/form";
    }

    @PostMapping("/create")
    public String create(
            @Valid @ModelAttribute("userForm") UserForm form,
            BindingResult errors,
            RedirectAttributes redirect,
            Model model
    ) {
        if (errors.hasErrors()) {
            model.addAttribute("editing", false);
            return "users/form";
        }
        try {
            users.create(form);
            redirect.addFlashAttribute("success", "Đã tạo người dùng mới.");
            return "redirect:/users";
        } catch (IllegalArgumentException exception) {
            errors.reject("user", exception.getMessage());
            model.addAttribute("editing", false);
            return "users/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        model.addAttribute("userForm", users.findForm(id));
        model.addAttribute("editing", true);
        return "users/form";
    }

    @PostMapping("/edit/{id}")
    public String edit(
            @PathVariable Long id,
            @Valid @ModelAttribute("userForm") UserForm form,
            BindingResult errors,
            @AuthenticationPrincipal CustomUserDetails current,
            RedirectAttributes redirect,
            Model model
    ) {
        form.setId(id);
        if (errors.hasErrors()) {
            model.addAttribute("editing", true);
            return "users/form";
        }
        try {
            users.update(id, form, current.getId());
            redirect.addFlashAttribute("success", "Đã cập nhật người dùng.");
            return "redirect:/users";
        } catch (IllegalArgumentException exception) {
            errors.reject("user", exception.getMessage());
            model.addAttribute("editing", true);
            return "users/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails current,
            RedirectAttributes redirect
    ) {
        try {
            users.delete(id, current.getId());
            redirect.addFlashAttribute("success", "Đã xóa người dùng.");
        } catch (IllegalArgumentException exception) {
            redirect.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/users";
    }
}
