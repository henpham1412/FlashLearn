package com.hen.flastcard.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "card_id"})})
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

    @Column(name = "last_review_date", nullable = true)
    LocalDateTime lastReviewDate;

    @Column(nullable = false, name = "next_review_date")
    LocalDateTime nextReviewDate;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;
    // config auditing later
    //    @CreatedBy
    //    @Column(name = "created_by", nullable = false, updatable = false)
    //    String createBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    FlashCard flashCard;
}
