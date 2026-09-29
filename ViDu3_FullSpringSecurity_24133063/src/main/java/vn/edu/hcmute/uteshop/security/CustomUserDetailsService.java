package vn.edu.hcmute.uteshop.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.edu.hcmute.uteshop.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository users;

    public CustomUserDetailsService(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
        return users.findByUsernameIgnoreCaseOrEmailIgnoreCase(login, login)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không tồn tại"));
    }
}
