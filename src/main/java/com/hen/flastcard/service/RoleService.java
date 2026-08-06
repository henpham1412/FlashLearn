package com.hen.flastcard.service;

import com.hen.flastcard.dto.request.RoleRequest;
import com.hen.flastcard.dto.response.RoleResponse;
import com.hen.flastcard.entity.Permission;
import com.hen.flastcard.entity.Role;
import com.hen.flastcard.exception.AppException;
import com.hen.flastcard.exception.ErrorCode;
import com.hen.flastcard.mapper.RoleMapper;
import com.hen.flastcard.repository.PermissionRepository;
import com.hen.flastcard.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;
    PermissionRepository permissionRepository;
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    @Transactional
    public RoleResponse create(RoleRequest request) {
        Role role = roleMapper.toRole(request);
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }
    @PreAuthorize("hasAuthority('ROLE_GET_ALL')")
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    @Transactional
    public void delete(String role) {
        roleRepository.deleteById(role);
    }
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    @Transactional
    public RoleResponse update(String roleName, RoleRequest request) {
        var role = roleRepository.findById(roleName).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
        roleMapper.updateRole(role, request);
        role.setPermissions(new HashSet<>(permissions));
        roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }
}
