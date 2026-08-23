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

import com.hen.flastcard.dto.request.DeckRequest;
import com.hen.flastcard.dto.response.DeckResponse;
import com.hen.flastcard.entity.Deck;
import com.hen.flastcard.entity.User;
import com.hen.flastcard.exception.AppException;
import com.hen.flastcard.exception.ErrorCode;
import com.hen.flastcard.mapper.DeckMapper;
import com.hen.flastcard.repository.DeckRepository;

@ExtendWith(MockitoExtension.class)
class DeckServiceTest {

    @InjectMocks
    private DeckService deckService;

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private DeckMapper deckMapper;

    @Mock
    private CurrentUserService currentUserService;

    private DeckRequest request;
    private DeckResponse deckResponse;
    private DeckResponse deckResponse2;
    private Deck deck;
    private Deck deck2;
    private User user;

    @BeforeEach
    void initData() {
        user = User.builder().id(1L).username("john").email("john@gmail.com").build();

        request = DeckRequest.builder()
                .name("Java")
                .description("Java flashcards")
                .build();

        deck = Deck.builder()
                .id(1L)
                .name("Java")
                .description("Java flashcards")
                .user(user)
                .build();

        deck2 = Deck.builder()
                .id(2L)
                .name("Spring Boot")
                .description("Spring Boot flashcards")
                .user(user)
                .build();

        deckResponse = DeckResponse.builder()
                .id(1L)
                .name("Java")
                .description("Java flashcards")
                .build();

        deckResponse2 = DeckResponse.builder()
                .id(2L)
                .name("Spring Boot")
                .description("Spring Boot flashcards")
                .build();
    }

    @Test
    void createDeck_valid_success() {
        when(deckMapper.toDeck(request)).thenReturn(deck);
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(deckRepository.save(deck)).thenReturn(deck);
        when(deckMapper.toDeckResponse(deck)).thenReturn(deckResponse);

        var response = deckService.createDeck(request);

        Assertions.assertThat(response).isEqualTo(deckResponse);

        Assertions.assertThat(deck.getUser()).isEqualTo(user);

        verify(deckMapper).toDeck(request);
        verify(currentUserService).getCurrentUser();
        verify(deckRepository).save(deck);
        verify(deckMapper).toDeckResponse(deck);
    }

    @Test
    void getAll_valid_success() {
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(deckRepository.findAllByUser_Id(user.getId())).thenReturn(List.of(deck, deck2));

        when(deckMapper.toDeckResponse(deck)).thenReturn(deckResponse);
        when(deckMapper.toDeckResponse(deck2)).thenReturn(deckResponse2);

        var response = deckService.getAll();

        Assertions.assertThat(response).containsExactly(deckResponse, deckResponse2);

        verify(currentUserService).getCurrentUser();
        verify(deckRepository).findAllByUser_Id(user.getId());
        verify(deckMapper).toDeckResponse(deck);
        verify(deckMapper).toDeckResponse(deck2);
    }

    @Test
    void getAll_empty_success() {
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(deckRepository.findAllByUser_Id(user.getId())).thenReturn(List.of());

        var response = deckService.getAll();

        Assertions.assertThat(response).isEmpty();

        verify(currentUserService).getCurrentUser();
        verify(deckRepository).findAllByUser_Id(user.getId());
        verify(deckMapper, never()).toDeckResponse(any());
    }

    @Test
    void getById_valid_success() {
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(deckRepository.findByIdAndUser_Id(1L, user.getId())).thenReturn(Optional.of(deck));
        when(deckMapper.toDeckResponse(deck)).thenReturn(deckResponse);

        var response = deckService.getById(1L);

        Assertions.assertThat(response).isEqualTo(deckResponse);

        verify(currentUserService).getCurrentUser();
        verify(deckRepository).findByIdAndUser_Id(1L, user.getId());
        verify(deckMapper).toDeckResponse(deck);
    }

    @Test
    void getById_invalid_fail() {
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(deckRepository.findByIdAndUser_Id(1L, user.getId())).thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class, () -> deckService.getById(1L));

        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DECK_NOT_EXISTED);

        verify(currentUserService).getCurrentUser();
        verify(deckRepository).findByIdAndUser_Id(1L, user.getId());
        verify(deckMapper, never()).toDeckResponse(any());
    }

    @Test
    void updateDeck_valid_success() {
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(deckRepository.findByIdAndUser_Id(1L, user.getId())).thenReturn(Optional.of(deck));
        when(deckMapper.toDeckResponse(deck)).thenReturn(deckResponse);

        var response = deckService.updateDeck(1L, request);

        Assertions.assertThat(response).isEqualTo(deckResponse);

        verify(currentUserService).getCurrentUser();
        verify(deckRepository).findByIdAndUser_Id(1L, user.getId());
        verify(deckMapper).updateDeck(deck, request);
        verify(deckRepository).save(deck);
        verify(deckMapper).toDeckResponse(deck);
    }

    @Test
    void updateDeck_invalid_fail() {
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(deckRepository.findByIdAndUser_Id(1L, user.getId())).thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class, () -> deckService.updateDeck(1L, request));

        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DECK_NOT_EXISTED);

        verify(currentUserService).getCurrentUser();
        verify(deckRepository).findByIdAndUser_Id(1L, user.getId());
        verify(deckMapper, never()).updateDeck(any(), any());
        verify(deckRepository, never()).save(any());
        verify(deckMapper, never()).toDeckResponse(any());
    }

    @Test
    void deleteDeck_valid_success() {
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(deckRepository.findByIdAndUser_Id(1L, user.getId())).thenReturn(Optional.of(deck));

        var response = deckService.deleteDeck(1L);

        Assertions.assertThat(response).isEqualTo("Deck has been deleted");

        verify(currentUserService).getCurrentUser();
        verify(deckRepository).findByIdAndUser_Id(1L, user.getId());
        verify(deckRepository).delete(deck);
    }

    @Test
    void deleteDeck_invalid_fail() {
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(deckRepository.findByIdAndUser_Id(1L, user.getId())).thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class, () -> deckService.deleteDeck(1L));

        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DECK_NOT_EXISTED);

        verify(currentUserService).getCurrentUser();
        verify(deckRepository).findByIdAndUser_Id(1L, user.getId());
        verify(deckRepository, never()).delete(any());
    }
}
