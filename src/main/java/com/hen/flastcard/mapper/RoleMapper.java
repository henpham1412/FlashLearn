package com.hen.flastcard.mapper;

import com.hen.flastcard.dto.request.RoleRequest;
import com.hen.flastcard.dto.response.RoleResponse;
import com.hen.flastcard.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);
    RoleResponse toRoleResponse(Role role);
}