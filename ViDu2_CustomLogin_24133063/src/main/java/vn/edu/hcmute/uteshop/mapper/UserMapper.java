package vn.edu.hcmute.uteshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.edu.hcmute.uteshop.dto.UserDTO;
import vn.edu.hcmute.uteshop.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roleName", source = "role.name")
    UserDTO toDTO(User user);
}
