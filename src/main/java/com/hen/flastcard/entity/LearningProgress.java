package com.hen.flastcard.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class LearningProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "review_interval", nullable = false)
    Integer reviewInterval;

    @Column(name = "ease_factor", nullable = false)
    Double easeFactor;

    @Column(nullable = false)
    Integer repetition;

    @Column(nullable = false, name = "last_review_date")
    LocalDate lastReviewDate;
    @Column(nullable = false, name = "next_review_date")
    LocalDate nextReviewDate;
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDate createdAt;
    // config auditing later
//    @CreatedBy
//    @Column(name = "created_by", nullable = false, updatable = false)
//    String createBy;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    FlashCard flashCard;
}
