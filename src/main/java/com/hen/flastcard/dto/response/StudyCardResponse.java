package com.hen.flastcard.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudyCardResponse {
    Long cardId;
    String word;
    String hira_kata;
    String meaning;
    String example;
    /*
    String pronunciation (romaji or mp3)
    String imageUrl
    will design later
    * */
}
