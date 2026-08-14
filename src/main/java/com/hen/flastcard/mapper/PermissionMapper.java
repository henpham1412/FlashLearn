package com.hen.flastcard.mapper;

import org.mapstruct.Mapper;

import com.hen.flastcard.dto.request.PermissionRequest;
import com.hen.flastcard.dto.response.PermissionResponse;
import com.hen.flastcard.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
