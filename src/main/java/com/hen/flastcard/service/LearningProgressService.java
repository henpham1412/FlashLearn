package com.hen.flastcard.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.hen.flastcard.entity.FlashCard;
import com.hen.flastcard.entity.LearningProgress;
import com.hen.flastcard.entity.User;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LearningProgressService {

    public LearningProgress initializeProgress(User user, FlashCard flashCard) {
        LearningProgress progress = LearningProgress.builder()
                .user(user)
                .flashCard(flashCard)
                .reviewInterval(1)
                .repetition(0)
                .easeFactor(2.5)
                .lastReviewDate(null)
                .nextReviewDate(LocalDateTime.now())
                .build();
        return progress;
    }
}
