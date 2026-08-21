package com.hen.flastcard.service;

import com.hen.flastcard.entity.LearningProgress;
import com.hen.flastcard.service.implementation.SM2Strategy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.within;

@ExtendWith(MockitoExtension.class)
class SM2StrategyTest {

    @InjectMocks
    private SM2Strategy sm2Strategy;

    private LearningProgress progress;

    @BeforeEach
    void initData() {
        progress = LearningProgress.builder()
                .repetition(0)
                .reviewInterval(1)
                .easeFactor(2.5)
                .build();
    }

    @Test
    void updateProgress_firstSuccessfulReview_success() {
        var before = LocalDateTime.now();

        sm2Strategy.updateProgress(progress, 4);

        var after = LocalDateTime.now();

        Assertions.assertThat(progress.getEaseFactor())
                .isEqualTo(2.5);

        Assertions.assertThat(progress.getReviewInterval())
                .isEqualTo(1);

        Assertions.assertThat(progress.getRepetition())
                .isEqualTo(1);

        Assertions.assertThat(progress.getLastReviewDate())
                .isBetween(before, after);

        Assertions.assertThat(progress.getNextReviewDate())
                .isBetween(
                        before.plusDays(1),
                        after.plusDays(1)
                );
    }

    @Test
    void updateProgress_secondSuccessfulReview_success() {
        progress.setRepetition(1);
        progress.setReviewInterval(1);
        progress.setEaseFactor(2.5);

        var before = LocalDateTime.now();

        sm2Strategy.updateProgress(progress, 4);

        var after = LocalDateTime.now();

        Assertions.assertThat(progress.getEaseFactor())
                .isEqualTo(2.5);

        Assertions.assertThat(progress.getReviewInterval())
                .isEqualTo(6);

        Assertions.assertThat(progress.getRepetition())
                .isEqualTo(2);

        Assertions.assertThat(progress.getLastReviewDate())
                .isBetween(before, after);

        Assertions.assertThat(progress.getNextReviewDate())
                .isBetween(
                        before.plusDays(6),
                        after.plusDays(6)
                );
    }

    @Test
    void updateProgress_repeatedSuccessfulReview_success() {
        progress.setRepetition(2);
        progress.setReviewInterval(6);
        progress.setEaseFactor(2.5);

        var before = LocalDateTime.now();

        sm2Strategy.updateProgress(progress, 4);

        var after = LocalDateTime.now();

        Assertions.assertThat(progress.getEaseFactor())
                .isEqualTo(2.5);

        Assertions.assertThat(progress.getReviewInterval())
                .isEqualTo(15);

        Assertions.assertThat(progress.getRepetition())
                .isEqualTo(3);

        Assertions.assertThat(progress.getLastReviewDate())
                .isBetween(before, after);

        Assertions.assertThat(progress.getNextReviewDate())
                .isBetween(
                        before.plusDays(15),
                        after.plusDays(15)
                );
    }

    @Test
    void updateProgress_failedReview_resetProgress() {
        progress.setRepetition(3);
        progress.setReviewInterval(15);
        progress.setEaseFactor(2.5);

        var before = LocalDateTime.now();

        sm2Strategy.updateProgress(progress, 2);

        var after = LocalDateTime.now();

        Assertions.assertThat(progress.getRepetition())
                .isZero();

        Assertions.assertThat(progress.getReviewInterval())
                .isEqualTo(1);

        Assertions.assertThat(progress.getEaseFactor())
                .isCloseTo(2.18, within(0.000001));

        Assertions.assertThat(progress.getLastReviewDate())
                .isBetween(before, after);

        Assertions.assertThat(progress.getNextReviewDate())
                .isBetween(
                        before.plusDays(1),
                        after.plusDays(1)
                );
    }

    @Test
    void updateProgress_easeFactorBelowMinimum_shouldBe1_3() {
        progress.setRepetition(3);
        progress.setReviewInterval(10);
        progress.setEaseFactor(1.3);

        sm2Strategy.updateProgress(progress, 0);

        Assertions.assertThat(progress.getEaseFactor())
                .isEqualTo(1.3);

        Assertions.assertThat(progress.getRepetition())
                .isZero();

        Assertions.assertThat(progress.getReviewInterval())
                .isEqualTo(1);
    }
}
