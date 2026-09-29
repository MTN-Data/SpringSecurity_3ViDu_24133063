package vn.edu.hcmute.uteshop.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import vn.edu.hcmute.uteshop.dto.UserDTO;
import vn.edu.hcmute.uteshop.entity.Role;
import vn.edu.hcmute.uteshop.entity.User;

class UserMapperTest {
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void mapsUserAndRoleToDto() {
        User user = new User();
        user.setEmail("student@example.com");
        user.setFullName("Nguyễn Minh Tiến");
        user.setRole(new Role("USER"));
        user.setUsername("tien");
        user.setImages("/images/avatar-default.svg");
        UserDTO dto = mapper.toDTO(user);
        assertEquals("student@example.com", dto.getEmail());
        assertEquals("Nguyễn Minh Tiến", dto.getFullName());
        assertEquals("USER", dto.getRoleName());
        assertEquals("tien", dto.getUsername());
        assertEquals("/images/avatar-default.svg", dto.getImages());
    }
}
