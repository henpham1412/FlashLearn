package com.hen.flastcard.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeckRequest {
    @NotBlank(message = "DECK_NAME_REQUIRED")
    String name;
    @NotBlank(message = "DESCRIPTION_REQUIRED")
    String description;
}
