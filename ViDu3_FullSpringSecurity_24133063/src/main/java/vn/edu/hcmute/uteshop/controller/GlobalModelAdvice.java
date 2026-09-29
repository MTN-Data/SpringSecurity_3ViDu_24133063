package vn.edu.hcmute.uteshop.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import vn.edu.hcmute.uteshop.dto.UserDTO;
import vn.edu.hcmute.uteshop.mapper.UserMapper;
import vn.edu.hcmute.uteshop.repository.UserRepository;
import vn.edu.hcmute.uteshop.security.CustomUserDetails;

@ControllerAdvice
public class GlobalModelAdvice {
    private final UserRepository users;
    private final UserMapper mapper;

    public GlobalModelAdvice(UserRepository users, UserMapper mapper) {
        this.users = users;
        this.mapper = mapper;
    }

    @ModelAttribute("currentUser")
    public UserDTO currentUser(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        if (!(authentication.getPrincipal() instanceof CustomUserDetails principal)) {
            return null;
        }
        return users.findById(principal.getId())
                .map(mapper::toDTO)
                .orElse(null);
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(Authentication authentication) {
        return authentication != null
                && authentication.getAuthorities().stream()
                        .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
}
