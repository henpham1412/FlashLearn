package com.hen.flastcard.service;

import com.hen.flastcard.dto.request.DeckRequest;
import com.hen.flastcard.dto.response.DeckResponse;
import com.hen.flastcard.entity.Deck;
import com.hen.flastcard.exception.AppException;
import com.hen.flastcard.exception.ErrorCode;
import com.hen.flastcard.mapper.DeckMapper;
import com.hen.flastcard.repository.DeckRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeckService {
    DeckRepository deckRepository;
    DeckMapper deckMapper;
    CurrentUserService currentUserService;

    public DeckResponse createDeck(DeckRequest request) {
        Deck deck = deckMapper.toDeck(request);
        deck.setUser(currentUserService.getCurrentUser());
        return deckMapper.toDeckResponse(deckRepository.save(deck));
    }

    public List<DeckResponse> getAll() {
        List<Deck> list = deckRepository.findAllByUser_Id(currentUserService.getCurrentUser().getId());
        return list.stream().map(deckMapper::toDeckResponse).toList();
    }

    public DeckResponse getById(Long id) {
        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DECK_NOT_EXISTED));
        return deckMapper.toDeckResponse(deck);
    }

    public DeckResponse updateDeck(Long id, DeckRequest request) {
        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DECK_NOT_EXISTED));
        deckMapper.updateDeck(deck, request);
        deckRepository.save(deck);
        return deckMapper.toDeckResponse(deck);
    }

    public String deleteDeck(Long id) {
        deckRepository.deleteById(id);
        return "Deck has been deleted";
    }
}
