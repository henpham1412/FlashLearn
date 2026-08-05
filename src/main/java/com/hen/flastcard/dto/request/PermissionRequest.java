package com.hen.flastcard.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionRequest {
    @NotBlank(message = "PERMISSION_NAME_REQUIRED")
    String name;
    @NotBlank(message = "PERMISSION_DESC_REQUIRED")
    String description;
}
