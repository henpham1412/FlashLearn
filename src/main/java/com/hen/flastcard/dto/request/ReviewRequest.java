package com.hen.flastcard.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewRequest {
    @NotNull(message = "CARD_ID_REQUIRED")
    Long cardId;

    @NotNull(message = "QUALITY_REQUIRED")
    @Min(value = 0, message = "QUALITY_INVALID")
    @Max(value = 5, message = "QUALITY_INVALID")
    Integer quality;
}
