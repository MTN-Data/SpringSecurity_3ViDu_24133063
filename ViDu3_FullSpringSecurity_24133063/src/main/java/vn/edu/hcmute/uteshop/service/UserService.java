package vn.edu.hcmute.uteshop.service;

import java.util.Locale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.hcmute.uteshop.dto.UserDTO;
import vn.edu.hcmute.uteshop.dto.UserForm;
import vn.edu.hcmute.uteshop.entity.Role;
import vn.edu.hcmute.uteshop.entity.User;
import vn.edu.hcmute.uteshop.mapper.UserMapper;
import vn.edu.hcmute.uteshop.repository.ProductRepository;
import vn.edu.hcmute.uteshop.repository.RoleRepository;
import vn.edu.hcmute.uteshop.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository users;
    private final RoleRepository roles;
    private final ProductRepository products;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;

    public UserService(
            UserRepository users,
            RoleRepository roles,
            ProductRepository products,
            UserMapper mapper,
            PasswordEncoder encoder
    ) {
        this.users = users;
        this.roles = roles;
        this.products = products;
        this.mapper = mapper;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> search(String keyword, int page) {
        return users.search(
                keyword == null ? "" : keyword.trim(),
                PageRequest.of(Math.max(0, page), 8, Sort.by(Sort.Direction.DESC, "id"))
        ).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public UserForm findForm(Long id) {
        User user = get(id);
        UserForm form = new UserForm();
        form.setId(user.getId());
        form.setUsername(user.getUsername());
        form.setEmail(user.getEmail());
        form.setFullName(user.getFullName());
        form.setImages(user.getImages());
        form.setRoleName(user.getRole().getName());
        form.setEnabled(user.isEnabled());
        return form;
    }

    @Transactional
    public void create(UserForm form) {
        validateUnique(form, null);
        if (form.getPassword() == null || form.getPassword().length() < 8) {
            throw new IllegalArgumentException("Mật khẩu mới phải dài ít nhất 8 ký tự.");
        }
        UserDTO draft = new UserDTO();
        draft.setUsername(form.getUsername().trim());
        draft.setEmail(form.getEmail().trim());
        draft.setFullName(form.getFullName().trim());
        draft.setImages(form.getImages());
        draft.setEnabled(form.isEnabled());
        User user = mapper.toEntity(draft);
        copyFields(user, form);
        user.setPassword(encoder.encode(form.getPassword()));
        users.save(user);
    }

    @Transactional
    public void update(Long id, UserForm form, Long actingUserId) {
        User user = get(id);
        validateUnique(form, id);
        if (id.equals(actingUserId)
                && (!form.isEnabled() || !"ROLE_ADMIN".equals(form.getRoleName()))) {
            throw new IllegalArgumentException("Không thể tự khóa hoặc tự hạ quyền tài khoản đang sử dụng.");
        }
        copyFields(user, form);
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            if (form.getPassword().length() < 8) {
                throw new IllegalArgumentException("Mật khẩu phải dài ít nhất 8 ký tự.");
            }
            user.setPassword(encoder.encode(form.getPassword()));
        }
    }

    @Transactional
    public void delete(Long id, Long actingUserId) {
        if (id.equals(actingUserId)) {
            throw new IllegalArgumentException("Bạn không thể tự xóa tài khoản đang đăng nhập.");
        }
        users.delete(get(id));
    }

    @Transactional(readOnly = true)
    public long count() {
        return users.count();
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = mapper.toDTO(user);
        dto.setProductCount(products.countByUserId(user.getId()));
        return dto;
    }

    private void validateUnique(UserForm form, Long existingId) {
        users.findByUsernameIgnoreCase(form.getUsername().trim())
                .filter(user -> !user.getId().equals(existingId))
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Username đã tồn tại.");
                });
        users.findByEmailIgnoreCase(form.getEmail().trim())
                .filter(user -> !user.getId().equals(existingId))
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Email đã tồn tại.");
                });
    }

    private void copyFields(User user, UserForm form) {
        Role role = roles.findByName(form.getRoleName())
                .orElseThrow(() -> new IllegalArgumentException("Vai trò không hợp lệ."));
        user.setUsername(form.getUsername().trim());
        user.setEmail(form.getEmail().trim().toLowerCase(Locale.ROOT));
        user.setFullName(form.getFullName().trim());
        user.setImages(form.getImages());
        user.setEnabled(form.isEnabled());
        user.setRole(role);
    }

    private User get(Long id) {
        return users.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng."));
    }
}
