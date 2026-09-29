package vn.edu.hcmute.uteshop.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import vn.edu.hcmute.uteshop.dto.UserDTO;
import vn.edu.hcmute.uteshop.mapper.UserMapper;
import vn.edu.hcmute.uteshop.repository.UserRepository;

@ControllerAdvice
public class GlobalModelAdvice {
    private final UserRepository repository;
    private final UserMapper mapper;

    public GlobalModelAdvice(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @ModelAttribute("currentUser")
    public UserDTO currentUser(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return repository.findByEmailIgnoreCase(authentication.getName())
                .map(mapper::toDTO)
                .orElse(null);
    }
}
