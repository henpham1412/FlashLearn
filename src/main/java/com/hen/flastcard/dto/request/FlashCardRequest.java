package com.hen.flastcard.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FlashCardRequest {
    @NotBlank(message = "WORD_REQUIRED")
    String word;

    @NotBlank(message = "HIRA_KATA_REQUIRED")
    String hira_kata;

    @NotBlank(message = "MEANING_REQUIRED")
    String meaning;

    String example;
}
