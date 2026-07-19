package com.hen.flastcard.controller;

import com.hen.flastcard.dto.response.ApiResponse;
import com.hen.flastcard.dto.response.StudyCardResponse;
import com.hen.flastcard.service.StudyService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudyController {
    StudyService studyService;
    @GetMapping("/study/decks/{deckId}")
    public ApiResponse<List<StudyCardResponse>> startStudying(@PathVariable("deckId") Long id) {
        return ApiResponse.<List<StudyCardResponse>>builder()
                .result(studyService.loadStudyCards(id))
                .build();
    }
}
