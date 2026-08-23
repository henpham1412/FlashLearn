package com.hen.flastcard.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.hen.flastcard.dto.request.ReviewRequest;
import com.hen.flastcard.dto.response.ApiResponse;
import com.hen.flastcard.dto.response.ReviewResponse;
import com.hen.flastcard.dto.response.StudyCardResponse;
import com.hen.flastcard.service.StudyService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

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

    @PostMapping("/study/review")
    public ApiResponse<ReviewResponse> reviewCard(@RequestBody @Valid ReviewRequest request) {
        return ApiResponse.<ReviewResponse>builder()
                .result(studyService.reviewCard(request))
                .build();
    }
}
