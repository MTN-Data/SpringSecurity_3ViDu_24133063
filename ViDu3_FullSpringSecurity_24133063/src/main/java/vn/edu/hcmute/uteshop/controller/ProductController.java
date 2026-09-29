package vn.edu.hcmute.uteshop.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.hcmute.uteshop.dto.ProductDTO;
import vn.edu.hcmute.uteshop.security.CustomUserDetails;
import vn.edu.hcmute.uteshop.service.ProductService;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService products;

    public ProductController(ProductService products) {
        this.products = products;
    }

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal CustomUserDetails current,
            Model model
    ) {
        Page<ProductDTO> result = products.search(keyword, page, current.getId(), isAdmin(current));
        model.addAttribute("products", result);
        model.addAttribute("keyword", keyword);
        return "products/list";
    }

    @GetMapping("/create")
    public String createPage(Model model) {
        model.addAttribute("productDTO", new ProductDTO());
        model.addAttribute("editing", false);
        return "products/form";
    }

    @PostMapping("/create")
    public String create(
            @Valid @ModelAttribute("productDTO") ProductDTO dto,
            BindingResult errors,
            @RequestParam(name = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails current,
            Model model,
            RedirectAttributes redirect
    ) {
        if (errors.hasErrors()) {
            model.addAttribute("editing", false);
            return "products/form";
        }
        try {
            products.create(dto, image, current.getId());
            redirect.addFlashAttribute("success", "Đã tạo sản phẩm.");
            return "redirect:/products";
        } catch (IllegalArgumentException exception) {
            errors.reject("product", exception.getMessage());
            model.addAttribute("editing", false);
            return "products/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editPage(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails current,
            Model model
    ) {
        model.addAttribute("productDTO", products.findForEditing(id, current.getId(), isAdmin(current)));
        model.addAttribute("editing", true);
        return "products/form";
    }

    @PostMapping("/edit/{id}")
    public String edit(
            @PathVariable Long id,
            @Valid @ModelAttribute("productDTO") ProductDTO dto,
            BindingResult errors,
            @RequestParam(name = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails current,
            Model model,
            RedirectAttributes redirect
    ) {
        dto.setId(id);
        if (errors.hasErrors()) {
            model.addAttribute("editing", true);
            return "products/form";
        }
        try {
            products.update(id, dto, image, current.getId(), isAdmin(current));
            redirect.addFlashAttribute("success", "Đã cập nhật sản phẩm.");
            return "redirect:/products";
        } catch (IllegalArgumentException exception) {
            errors.reject("product", exception.getMessage());
            model.addAttribute("editing", true);
            return "products/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails current,
            RedirectAttributes redirect
    ) {
        try {
            products.delete(id, current.getId(), isAdmin(current));
            redirect.addFlashAttribute("success", "Đã xóa sản phẩm.");
        } catch (IllegalArgumentException exception) {
            redirect.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/products";
    }

    private boolean isAdmin(CustomUserDetails user) {
        return user.getAuthorities().stream()
                .anyMatch(role -> "ROLE_ADMIN".equals(role.getAuthority()));
    }
}
