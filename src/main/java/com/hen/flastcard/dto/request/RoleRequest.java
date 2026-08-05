package com.hen.flastcard.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {
    @NotBlank(message = "ROLE_NAME_REQUIRED")
    String name;
    @NotBlank(message = "ROLE_DESC_REQUIRED")
    String description;
    @NotNull(message = "PERMISSIONS_REQUIRED")
    Set<String> permissions;
}
