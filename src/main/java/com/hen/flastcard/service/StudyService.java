package com.hen.flastcard.service;

import com.hen.flastcard.dto.response.StudyCardResponse;
import com.hen.flastcard.entity.FlashCard;
import com.hen.flastcard.entity.LearningProgress;
import com.hen.flastcard.entity.User;
import com.hen.flastcard.mapper.StudyMapper;
import com.hen.flastcard.repository.FlashCardRepository;
import com.hen.flastcard.repository.LearningProgressRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StudyService {
    FlashCardRepository flashCardRepository;
    LearningProgressRepository learningProgressRepository;
    CurrentUserService currentUserService;
    LearningProgressService learningProgressService;
    StudyMapper studyMapper;

    private Map<Long, LearningProgress> loadProgress(User user, Long deckId) {
        List<LearningProgress> learningProgresses = learningProgressRepository.findAllByUser_IdAndFlashCard_Deck_Id(user.getId(), deckId);
        Map<Long, LearningProgress> progressMap = new HashMap<>();
        for (LearningProgress progress: learningProgresses) {
            progressMap.put(progress.getFlashCard().getId(), progress);
        }
        return progressMap;
    }

    private void initializeMissingProgress(User user, FlashCard card) {
        learningProgressService.initializeProgress(user, card);
    }

    private boolean isDue(LearningProgress progress) {
        return !progress.getNextReviewDate().isAfter(LocalDate.now());
    }

    public List<StudyCardResponse> loadStudyCards(Long id) {
        List<StudyCardResponse> studyCardResponses = new ArrayList<>();
        User user = currentUserService.getCurrentUser();
        List<FlashCard> flashCards = flashCardRepository.findAllByDeck_Id(id);
        Map<Long, LearningProgress> progressMap = loadProgress(user, id);
        for (FlashCard card: flashCards) {
            if (!progressMap.containsKey(card.getId())) {
                // optimize: create a list to store progress then user saveAll to avoid multiple insert to db
                initializeMissingProgress(user, card);
                studyCardResponses.add(studyMapper.toStudyCardResponse(card));
            } else {
                LearningProgress progress = progressMap.get(card.getId());
                if (isDue(progress)) {
                    StudyCardResponse response = studyMapper.toStudyCardResponse(card);
                    studyCardResponses.add(response);
                }
            }
        }
        return studyCardResponses;
    }
}
