package vn.edu.hcmute.uteshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import vn.edu.hcmute.uteshop.dto.UserDTO;
import vn.edu.hcmute.uteshop.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(target = "roleName", source = "role.name")
    @Mapping(target = "productCount", ignore = true)
    UserDTO toDTO(User user);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "products", ignore = true)
    User toEntity(UserDTO dto);
}
