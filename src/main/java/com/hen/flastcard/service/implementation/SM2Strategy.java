package com.hen.flastcard.service.implementation;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.hen.flastcard.entity.LearningProgress;
import com.hen.flastcard.service.SpacedRepetitionStrategy;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SM2Strategy implements SpacedRepetitionStrategy {
    @Override
    public void updateProgress(LearningProgress progress, Integer quality) {
        Integer repetition = progress.getRepetition();
        Integer reviewInterval = progress.getReviewInterval();
        Double easeFactor = progress.getEaseFactor();

        Double newFactor = (easeFactor + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02)));
        newFactor = Math.max(newFactor, 1.3);
        progress.setEaseFactor(newFactor);
        if (quality >= 3) {
            switch (repetition) {
                case 0 -> progress.setReviewInterval(1);
                case 1 -> progress.setReviewInterval(6);
                default -> progress.setReviewInterval((int) Math.round(reviewInterval * newFactor));
            }
            progress.setRepetition(repetition + 1);
        } else {
            progress.setRepetition(0);
            progress.setReviewInterval(1);
        }
        LocalDateTime today = LocalDateTime.now();
        progress.setNextReviewDate(today.plusDays(progress.getReviewInterval()));
        progress.setLastReviewDate(today);
    }
}
