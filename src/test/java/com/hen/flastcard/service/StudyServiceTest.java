package com.hen.flastcard.service;

import com.hen.flastcard.dto.request.ReviewRequest;
import com.hen.flastcard.dto.response.StudyCardResponse;
import com.hen.flastcard.entity.Deck;
import com.hen.flastcard.entity.FlashCard;
import com.hen.flastcard.entity.LearningProgress;
import com.hen.flastcard.entity.User;
import com.hen.flastcard.exception.AppException;
import com.hen.flastcard.exception.ErrorCode;
import com.hen.flastcard.mapper.StudyMapper;
import com.hen.flastcard.repository.DeckRepository;
import com.hen.flastcard.repository.FlashCardRepository;
import com.hen.flastcard.repository.LearningProgressRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @InjectMocks
    private StudyService studyService;

    @Mock
    private FlashCardRepository flashCardRepository;

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private LearningProgressRepository learningProgressRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private LearningProgressService learningProgressService;

    @Mock
    private StudyMapper studyMapper;

    @Mock
    private SpacedRepetitionStrategy spacedRepetitionStrategy;

    private User user;
    private Deck deck;
    private FlashCard flashCard;
    private FlashCard flashCard2;
    private LearningProgress progress;
    private LearningProgress dueProgress;
    private LearningProgress notDueProgress;

    private StudyCardResponse studyCardResponse;

    private ReviewRequest reviewRequest;

    @BeforeEach
    void initData() {
        user = User.builder()
                .id(1L)
                .username("john")
                .email("john@gmail.com")
                .build();

        deck = Deck.builder()
                .id(1L)
                .name("Japanese N5")
                .description("Japanese N5 flashcards")
                .user(user)
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

        progress = LearningProgress.builder()
                .id(1L)
                .user(user)
                .flashCard(flashCard)
                .reviewInterval(1)
                .repetition(1)
                .easeFactor(2.5)
                .lastReviewDate(LocalDateTime.now().minusDays(1))
                .nextReviewDate(LocalDateTime.now().plusDays(1))
                .build();

        dueProgress = LearningProgress.builder()
                .id(1L)
                .user(user)
                .flashCard(flashCard)
                .reviewInterval(1)
                .repetition(1)
                .easeFactor(2.5)
                .lastReviewDate(LocalDateTime.now().minusDays(2))
                .nextReviewDate(LocalDateTime.now().minusMinutes(1))
                .build();

        notDueProgress = LearningProgress.builder()
                .id(2L)
                .user(user)
                .flashCard(flashCard2)
                .reviewInterval(1)
                .repetition(1)
                .easeFactor(2.5)
                .lastReviewDate(LocalDateTime.now().minusDays(1))
                .nextReviewDate(LocalDateTime.now().plusDays(1))
                .build();

        studyCardResponse = StudyCardResponse.builder()
                .cardId(1L)
                .word("食べる")
                .hira_kata("たべる")
                .meaning("ăn")
                .example("私はりんごを食べる。")
                .build();

        reviewRequest = ReviewRequest.builder()
                .cardId(1L)
                .quality(4)
                .build();
    }

    @Test
    void loadStudyCards_deckNotFound_fail() {
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(deckRepository.findByIdAndUser_Id(1L, user.getId()))
                .thenReturn(Optional.empty());

        var exception = assertThrows(
                AppException.class,
                () -> studyService.loadStudyCards(1L)
        );

        Assertions.assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.DECK_NOT_EXISTED);

        verify(currentUserService).getCurrentUser();
        verify(deckRepository)
                .findByIdAndUser_Id(1L, user.getId());

        verifyNoInteractions(
                flashCardRepository,
                learningProgressRepository,
                learningProgressService,
                studyMapper
        );
    }

    @Test
    void loadStudyCards_newCard_success() {
        when(currentUserService.getCurrentUser()).thenReturn(user);

        when(deckRepository.findByIdAndUser_Id(1L, user.getId()))
                .thenReturn(Optional.of(deck));

        when(flashCardRepository.findAllByDeck_Id(1L))
                .thenReturn(List.of(flashCard));

        when(learningProgressRepository
                .findAllByUser_IdAndFlashCard_Deck_Id(user.getId(), 1L))
                .thenReturn(List.of());

        when(learningProgressService.initializeProgress(user, flashCard))
                .thenReturn(progress);

        when(studyMapper.toStudyCardResponse(flashCard))
                .thenReturn(studyCardResponse);

        var response = studyService.loadStudyCards(1L);

        Assertions.assertThat(response)
                .containsExactly(studyCardResponse);

        verify(currentUserService).getCurrentUser();

        verify(deckRepository)
                .findByIdAndUser_Id(1L, user.getId());

        verify(flashCardRepository)
                .findAllByDeck_Id(1L);

        verify(learningProgressRepository)
                .findAllByUser_IdAndFlashCard_Deck_Id(user.getId(), 1L);

        verify(learningProgressService)
                .initializeProgress(user, flashCard);

        verify(studyMapper)
                .toStudyCardResponse(flashCard);

        verify(learningProgressRepository)
                .saveAll(List.of(progress));
    }

    @Test
    void loadStudyCards_dueCard_success() {
        when(currentUserService.getCurrentUser()).thenReturn(user);

        when(deckRepository.findByIdAndUser_Id(1L, user.getId()))
                .thenReturn(Optional.of(deck));

        when(flashCardRepository.findAllByDeck_Id(1L))
                .thenReturn(List.of(flashCard));

        when(learningProgressRepository
                .findAllByUser_IdAndFlashCard_Deck_Id(user.getId(), 1L))
                .thenReturn(List.of(dueProgress));

        when(studyMapper.toStudyCardResponse(flashCard))
                .thenReturn(studyCardResponse);

        var response = studyService.loadStudyCards(1L);

        Assertions.assertThat(response)
                .containsExactly(studyCardResponse);

        verify(studyMapper)
                .toStudyCardResponse(flashCard);

        verify(learningProgressService, never())
                .initializeProgress(any(), any());

        verify(learningProgressRepository)
                .saveAll(List.of());
    }

    @Test
    void loadStudyCards_notDueCard_success() {
        when(currentUserService.getCurrentUser()).thenReturn(user);

        when(deckRepository.findByIdAndUser_Id(1L, user.getId()))
                .thenReturn(Optional.of(deck));

        when(flashCardRepository.findAllByDeck_Id(1L))
                .thenReturn(List.of(flashCard2));

        when(learningProgressRepository
                .findAllByUser_IdAndFlashCard_Deck_Id(user.getId(), 1L))
                .thenReturn(List.of(notDueProgress));

        var response = studyService.loadStudyCards(1L);

        Assertions.assertThat(response)
                .isEmpty();

        verify(studyMapper, never())
                .toStudyCardResponse(any());

        verify(learningProgressService, never())
                .initializeProgress(any(), any());

        verify(learningProgressRepository)
                .saveAll(List.of());
    }

    @Test
    void reviewCard_valid_success() {
        when(currentUserService.getCurrentUser()).thenReturn(user);

        when(learningProgressRepository
                .findByUser_IdAndFlashCard_Id(user.getId(), 1L))
                .thenReturn(Optional.of(progress));

        LocalDateTime nextReviewDate =
                LocalDateTime.now().plusDays(3);

        progress.setNextReviewDate(nextReviewDate);

        var response = studyService.reviewCard(reviewRequest);

        Assertions.assertThat(response.isSuccess())
                .isTrue();

        Assertions.assertThat(response.getNextReviewDate())
                .isEqualTo(nextReviewDate);

        verify(currentUserService).getCurrentUser();

        verify(learningProgressRepository)
                .findByUser_IdAndFlashCard_Id(user.getId(), 1L);

        verify(spacedRepetitionStrategy)
                .updateProgress(progress, 4);

        verify(learningProgressRepository)
                .save(progress);
    }

    @Test
    void reviewCard_progressNotFound_fail() {
        when(currentUserService.getCurrentUser()).thenReturn(user);

        when(learningProgressRepository
                .findByUser_IdAndFlashCard_Id(user.getId(), 1L))
                .thenReturn(Optional.empty());

        var exception = assertThrows(
                AppException.class,
                () -> studyService.reviewCard(reviewRequest)
        );

        Assertions.assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.PROGRESS_NOT_EXISTED);

        verify(currentUserService).getCurrentUser();

        verify(learningProgressRepository)
                .findByUser_IdAndFlashCard_Id(user.getId(), 1L);

        verify(spacedRepetitionStrategy, never())
                .updateProgress(any(), anyInt());

        verify(learningProgressRepository, never())
                .save(any());
    }
}