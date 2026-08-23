package com.hen.flastcard.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hen.flastcard.dto.request.FlashCardRequest;
import com.hen.flastcard.dto.response.FlashCardResponse;
import com.hen.flastcard.entity.Deck;
import com.hen.flastcard.entity.FlashCard;
import com.hen.flastcard.entity.User;
import com.hen.flastcard.exception.AppException;
import com.hen.flastcard.exception.ErrorCode;
import com.hen.flastcard.mapper.FlashCardMapper;
import com.hen.flastcard.repository.DeckRepository;
import com.hen.flastcard.repository.FlashCardRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FlashCardService {
    FlashCardRepository flashCardRepository;
    DeckRepository deckRepository;
    FlashCardMapper flashCardMapper;
    CurrentUserService currentUserService;

    @Transactional
    public FlashCardResponse createFlashCard(Long id, FlashCardRequest request) {
        FlashCard flashCard = flashCardMapper.toFlashCard(request);
        Deck deck = deckRepository
                .findByIdAndUser_Id(id, currentUserService.getCurrentUser().getId())
                .orElseThrow(() -> new AppException(ErrorCode.DECK_NOT_EXISTED));
        flashCard.setDeck(deck);
        return flashCardMapper.toFlashCardResponse(flashCardRepository.save(flashCard));
    }

    @Transactional
    public FlashCardResponse updateFlashCard(Long id, FlashCardRequest request) {
        FlashCard flashCard = flashCardRepository
                .findByIdAndDeck_User_Id(id, currentUserService.getCurrentUser().getId())
                .orElseThrow(() -> new AppException(ErrorCode.FLASHCARD_NOT_EXISTED));
        flashCardMapper.updateFlashCard(flashCard, request);
        flashCardRepository.save(flashCard);
        return flashCardMapper.toFlashCardResponse(flashCard);
    }

    @Transactional
    public String deleteFlashCard(Long id) {
        FlashCard flashCard = flashCardRepository
                .findByIdAndDeck_User_Id(id, currentUserService.getCurrentUser().getId())
                .orElseThrow(() -> new AppException(ErrorCode.FLASHCARD_NOT_EXISTED));
        flashCardRepository.delete(flashCard);
        return "FlashCard has been deleted";
    }

    public List<FlashCardResponse> getAll(Long deckId) {
        User user = currentUserService.getCurrentUser();
        List<FlashCard> flashCards = flashCardRepository.findAllByDeck_IdAndDeck_User_Id(deckId, user.getId());
        return flashCards.stream().map(flashCardMapper::toFlashCardResponse).toList();
    }
}
