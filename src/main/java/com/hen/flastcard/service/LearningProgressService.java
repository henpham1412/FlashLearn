package com.hen.flastcard.service;

import com.hen.flastcard.entity.FlashCard;
import com.hen.flastcard.entity.LearningProgress;
import com.hen.flastcard.entity.User;
import com.hen.flastcard.repository.LearningProgressRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LearningProgressService {
    LearningProgressRepository learningProgressRepository;
    public LearningProgress initializeProgress(User user, FlashCard flashCard) {
        LearningProgress progress = LearningProgress.builder()
                .user(user)
                .flashCard(flashCard)
                .reviewInterval(1)
                .repetition(0)
                .easeFactor(2.5)
                .lastReviewDate(null)
                .nextReviewDate(LocalDate.now())
                .build();
        return learningProgressRepository.save(progress);
    }
}
