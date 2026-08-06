package com.hen.flastcard.mapper;

import com.hen.flastcard.dto.request.PermissionRequest;
import com.hen.flastcard.dto.response.PermissionResponse;
import com.hen.flastcard.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
