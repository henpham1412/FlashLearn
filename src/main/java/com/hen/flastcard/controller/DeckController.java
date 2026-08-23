package com.hen.flastcard.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.hen.flastcard.dto.request.DeckRequest;
import com.hen.flastcard.dto.response.ApiResponse;
import com.hen.flastcard.dto.response.DeckResponse;
import com.hen.flastcard.service.DeckService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/decks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeckController {
    DeckService deckService;

    @PostMapping
    public ApiResponse<DeckResponse> createDeck(@RequestBody @Valid DeckRequest request) {
        return ApiResponse.<DeckResponse>builder()
                .result(deckService.createDeck(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<DeckResponse>> getAll() {
        return ApiResponse.<List<DeckResponse>>builder()
                .result(deckService.getAll())
                .build();
    }

    @GetMapping("/{deckId}")
    public ApiResponse<DeckResponse> getById(@PathVariable("deckId") Long id) {
        return ApiResponse.<DeckResponse>builder()
                .result(deckService.getById(id))
                .build();
    }

    @PutMapping("/{deckId}")
    public ApiResponse<DeckResponse> updateDeck(
            @PathVariable("deckId") Long id, @RequestBody @Valid DeckRequest request) {
        return ApiResponse.<DeckResponse>builder()
                .result(deckService.updateDeck(id, request))
                .build();
    }

    @DeleteMapping("/{deckId}")
    public ApiResponse<String> delete(@PathVariable("deckId") Long id) {
        return ApiResponse.<String>builder().result(deckService.deleteDeck(id)).build();
    }
}
