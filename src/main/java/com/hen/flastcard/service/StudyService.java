package com.hen.flastcard.service;

import com.hen.flastcard.dto.request.ReviewRequest;
import com.hen.flastcard.dto.response.ReviewResponse;
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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StudyService {
    FlashCardRepository flashCardRepository;
    DeckRepository deckRepository;
    LearningProgressRepository learningProgressRepository;
    CurrentUserService currentUserService;
    LearningProgressService learningProgressService;
    StudyMapper studyMapper;
    SpacedRepetitionStrategy spacedRepetitionStrategy;
    private Map<Long, LearningProgress> loadProgress(User user, Long deckId) {
        List<LearningProgress> learningProgresses = learningProgressRepository.findAllByUser_IdAndFlashCard_Deck_Id(user.getId(), deckId);
        Map<Long, LearningProgress> progressMap = new HashMap<>();
        for (LearningProgress progress: learningProgresses) {
            progressMap.put(progress.getFlashCard().getId(), progress);
        }
        return progressMap;
    }

    private boolean isDue(LearningProgress progress) {
        return !progress.getNextReviewDate().isAfter(LocalDateTime.now());
    }
    @Transactional
    public List<StudyCardResponse> loadStudyCards(Long id) {
        List<StudyCardResponse> studyCardResponses = new ArrayList<>();
        List<LearningProgress> progresses = new ArrayList<>();
        User user = currentUserService.getCurrentUser();
        Deck deck = deckRepository.findByIdAndUser_Id(id ,user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.DECK_NOT_EXISTED));
        List<FlashCard> flashCards = flashCardRepository.findAllByDeck_Id(id);
        Map<Long, LearningProgress> progressMap = loadProgress(user, id);
        for (FlashCard card: flashCards) {
            if (!progressMap.containsKey(card.getId())) {
                // optimize: create a list to store progress then user saveAll to avoid multiple insert to db
                progresses.add(learningProgressService.initializeProgress(user, card));
                studyCardResponses.add(studyMapper.toStudyCardResponse(card));
            } else {
                LearningProgress progress = progressMap.get(card.getId());
                if (isDue(progress)) {
                    StudyCardResponse response = studyMapper.toStudyCardResponse(card);
                    studyCardResponses.add(response);
                }
            }
        }
        learningProgressRepository.saveAll(progresses);
        return studyCardResponses;
    }
    @Transactional
    public ReviewResponse reviewCard(ReviewRequest request) {
        User user = currentUserService.getCurrentUser();
        LearningProgress progress = learningProgressRepository
                .findByUser_IdAndFlashCard_Id(user.getId(), request.getCardId())
                .orElseThrow(() -> new AppException(ErrorCode.PROGRESS_NOT_EXISTED));;
        spacedRepetitionStrategy.updateProgress(progress, request.getQuality());
        learningProgressRepository.save(progress);
        return ReviewResponse.builder()
                .success(true)
                .nextReviewDate(progress.getNextReviewDate())
                .build();
    }
}
