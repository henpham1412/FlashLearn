package com.hen.flastcard.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.hen.flastcard.dto.request.FlashCardRequest;
import com.hen.flastcard.dto.response.ApiResponse;
import com.hen.flastcard.dto.response.FlashCardResponse;
import com.hen.flastcard.service.FlashCardService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FlashCardController {
    FlashCardService flashCardService;

    @PostMapping("/decks/{deckId}/cards")
    public ApiResponse<FlashCardResponse> createFlashCard(
            @PathVariable("deckId") Long id, @RequestBody @Valid FlashCardRequest request) {
        return ApiResponse.<FlashCardResponse>builder()
                .result(flashCardService.createFlashCard(id, request))
                .build();
    }

    @PutMapping("/cards/{cardId}")
    public ApiResponse<FlashCardResponse> updateFlashCard(
            @PathVariable("cardId") Long id, @RequestBody @Valid FlashCardRequest request) {
        return ApiResponse.<FlashCardResponse>builder()
                .result(flashCardService.updateFlashCard(id, request))
                .build();
    }

    @GetMapping("/decks/{deckId}/cards")
    public ApiResponse<List<FlashCardResponse>> getAllCards(@PathVariable("deckId") Long id) {
        return ApiResponse.<List<FlashCardResponse>>builder()
                .result(flashCardService.getAll(id))
                .build();
    }

    @DeleteMapping("cards/{cardId}")
    public ApiResponse<String> deleteCard(@PathVariable("cardId") Long id) {
        return ApiResponse.<String>builder()
                .result(flashCardService.deleteFlashCard(id))
                .build();
    }
}
