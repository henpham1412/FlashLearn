package com.hen.flastcard.controller;

import com.hen.flastcard.dto.request.PermissionRequest;
import com.hen.flastcard.dto.response.ApiResponse;
import com.hen.flastcard.dto.response.PermissionResponse;
import com.hen.flastcard.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;
    @PostMapping
    public ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<PermissionResponse>> create() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getALl())
                .build();
    }

    @DeleteMapping("/{permission}")
    public ApiResponse<Void> create(@PathVariable("permission") String permission) {
        permissionService.delete(permission);
        return ApiResponse.<Void>builder()
                .build();
    }
}
