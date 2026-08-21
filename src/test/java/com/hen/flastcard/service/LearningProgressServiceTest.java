package com.hen.flastcard.service;

import com.hen.flastcard.entity.FlashCard;
import com.hen.flastcard.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class LearningProgressServiceTest {

    @InjectMocks
    private LearningProgressService learningProgressService;

    private User user;
    private FlashCard flashCard;

    @BeforeEach
    void initData() {
        user = User.builder()
                .id(1L)
                .username("john")
                .email("john@gmail.com")
                .build();

        flashCard = FlashCard.builder()
                .id(1L)
                .word("食べる")
                .hira_kata("たべる")
                .meaning("ăn")
                .example("私はりんごを食べる。")
                .build();
    }

    @Test
    void initializeProgress_valid_success() {
        var before = LocalDateTime.now();

        var progress = learningProgressService.initializeProgress(user, flashCard);

        var after = LocalDateTime.now();

        Assertions.assertThat(progress.getUser())
                .isEqualTo(user);
        Assertions.assertThat(progress.getFlashCard())
                .isEqualTo(flashCard);
        Assertions.assertThat(progress.getReviewInterval())
                .isEqualTo(1);
        Assertions.assertThat(progress.getRepetition())
                .isZero();
        Assertions.assertThat(progress.getEaseFactor())
                .isEqualTo(2.5);
        Assertions.assertThat(progress.getLastReviewDate())
                .isNull();

        Assertions.assertThat(progress.getNextReviewDate())
                .isBetween(before, after);
    }
}