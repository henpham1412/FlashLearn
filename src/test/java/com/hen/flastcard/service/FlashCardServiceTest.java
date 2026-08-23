package com.hen.flastcard.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class FlashCardServiceTest {

    @InjectMocks
    private FlashCardService flashCardService;

    @Mock
    private FlashCardRepository flashCardRepository;

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private FlashCardMapper flashCardMapper;

    @Mock
    private CurrentUserService currentUserService;

    private FlashCardRequest request;
    private FlashCardResponse flashCardResponse;
    private FlashCardResponse flashCardResponse2;
    private FlashCard flashCard;
    private FlashCard flashCard2;
    private Deck deck;
    private User user;

    @BeforeEach
    void initData() {
        user = User.builder().id(1L).username("john").email("john@gmail.com").build();

        deck = Deck.builder()
                .id(1L)
                .name("Japanese N5")
                .description("Japanese N5 flashcards")
                .user(user)
                .build();

        request = FlashCardRequest.builder()
                .word("食べる")
                .hira_kata("たべる")
                .meaning("ăn")
                .example("私はりんごを食べる。")
                .build();

        flashCard = FlashCard.builder()
                .id(1L)
                .word("食べる")
                .hira_kata("たべる")
                .meaning("ăn")
                .example("私はりんごを食べる。")
                .deck(deck)
                .build();

        flashCard2 = FlashCard.builder()
                .id(2L)
                .word("飲む")
                .hira_kata("のむ")
                .meaning("uống")
                .example("水を飲む。")
                .deck(deck)
                .build();

        flashCardResponse = FlashCardResponse.builder()
                .id(1L)
                .word("食べる")
                .hira_kata("たべる")
                .meaning("ăn")
                .example("私はりんごを食べる。")
                .build();

        flashCardResponse2 = FlashCardResponse.builder()
                .id(2L)
                .word("飲む")
                .hira_kata("のむ")
                .meaning("uống")
                .example("水を飲む。")
                .build();
    }

    @Test
    void createFlashCard_valid_success() {
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(flashCardMapper.toFlashCard(request)).thenReturn(flashCard);
        when(deckRepository.findByIdAndUser_Id(1L, user.getId())).thenReturn(Optional.of(deck));
        when(flashCardRepository.save(flashCard)).thenReturn(flashCard);
        when(flashCardMapper.toFlashCardResponse(flashCard)).thenReturn(flashCardResponse);

        var response = flashCardService.createFlashCard(1L, request);

        Assertions.assertThat(response).isEqualTo(flashCardResponse);

        Assertions.assertThat(flashCard.getDeck()).isEqualTo(deck);

        verify(currentUserService).getCurrentUser();
        verify(flashCardMapper).toFlashCard(request);
        verify(deckRepository).findByIdAndUser_Id(1L, user.getId());
        verify(flashCardRepository).save(flashCard);
        verify(flashCardMapper).toFlashCardResponse(flashCard);
    }

    @Test
    void createFlashCard_deckNotFound_fail() {
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(flashCardMapper.toFlashCard(request)).thenReturn(flashCard);
        when(deckRepository.findByIdAndUser_Id(1L, user.getId())).thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class, () -> flashCardService.createFlashCard(1L, request));

        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DECK_NOT_EXISTED);

        verify(currentUserService).getCurrentUser();
        verify(flashCardMapper).toFlashCard(request);
        verify(deckRepository).findByIdAndUser_Id(1L, user.getId());
        verify(flashCardRepository, never()).save(any());
        verify(flashCardMapper, never()).toFlashCardResponse(any());
    }

    @Test
    void updateFlashCard_valid_success() {
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(flashCardRepository.findByIdAndDeck_User_Id(1L, user.getId())).thenReturn(Optional.of(flashCard));
        when(flashCardMapper.toFlashCardResponse(flashCard)).thenReturn(flashCardResponse);

        var response = flashCardService.updateFlashCard(1L, request);

        Assertions.assertThat(response).isEqualTo(flashCardResponse);

        verify(currentUserService).getCurrentUser();
        verify(flashCardRepository).findByIdAndDeck_User_Id(1L, user.getId());
        verify(flashCardMapper).updateFlashCard(flashCard, request);
        verify(flashCardRepository).save(flashCard);
        verify(flashCardMapper).toFlashCardResponse(flashCard);
    }

    @Test
    void updateFlashCard_notFound_fail() {
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(flashCardRepository.findByIdAndDeck_User_Id(1L, user.getId())).thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class, () -> flashCardService.updateFlashCard(1L, request));

        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FLASHCARD_NOT_EXISTED);

        verify(currentUserService).getCurrentUser();
        verify(flashCardRepository).findByIdAndDeck_User_Id(1L, user.getId());
        verify(flashCardMapper, never()).updateFlashCard(any(), any());
        verify(flashCardRepository, never()).save(any());
        verify(flashCardMapper, never()).toFlashCardResponse(any());
    }

    @Test
    void deleteFlashCard_valid_success() {
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(flashCardRepository.findByIdAndDeck_User_Id(1L, user.getId())).thenReturn(Optional.of(flashCard));

        var response = flashCardService.deleteFlashCard(1L);

        Assertions.assertThat(response).isEqualTo("FlashCard has been deleted");

        verify(currentUserService).getCurrentUser();
        verify(flashCardRepository).findByIdAndDeck_User_Id(1L, user.getId());
        verify(flashCardRepository).delete(flashCard);
    }

    @Test
    void deleteFlashCard_notFound_fail() {
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(flashCardRepository.findByIdAndDeck_User_Id(1L, user.getId())).thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class, () -> flashCardService.deleteFlashCard(1L));

        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FLASHCARD_NOT_EXISTED);

        verify(currentUserService).getCurrentUser();
        verify(flashCardRepository).findByIdAndDeck_User_Id(1L, user.getId());
        verify(flashCardRepository, never()).delete(any());
    }

    @Test
    void getAll_valid_success() {
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(flashCardRepository.findAllByDeck_IdAndDeck_User_Id(1L, user.getId()))
                .thenReturn(List.of(flashCard, flashCard2));

        when(flashCardMapper.toFlashCardResponse(flashCard)).thenReturn(flashCardResponse);
        when(flashCardMapper.toFlashCardResponse(flashCard2)).thenReturn(flashCardResponse2);

        var response = flashCardService.getAll(1L);

        Assertions.assertThat(response).containsExactly(flashCardResponse, flashCardResponse2);

        verify(currentUserService).getCurrentUser();
        verify(flashCardRepository).findAllByDeck_IdAndDeck_User_Id(1L, user.getId());
        verify(flashCardMapper).toFlashCardResponse(flashCard);
        verify(flashCardMapper).toFlashCardResponse(flashCard2);
    }

    @Test
    void getAll_empty_success() {
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(flashCardRepository.findAllByDeck_IdAndDeck_User_Id(1L, user.getId()))
                .thenReturn(List.of());

        var response = flashCardService.getAll(1L);

        Assertions.assertThat(response).isEmpty();

        verify(currentUserService).getCurrentUser();
        verify(flashCardRepository).findAllByDeck_IdAndDeck_User_Id(1L, user.getId());
        verify(flashCardMapper, never()).toFlashCardResponse(any());
    }
}
