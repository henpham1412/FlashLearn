package com.hen.flastcard.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class FlashCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 100)
    String word;

    @Column(name = "hira_kata", nullable = false, length = 100)
    String hira_kata;

    @Column(nullable = false, length = 100)
    String meaning;

    @Column(nullable = false, length = 500)
    String example;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id")
    Deck deck;

    @OneToMany(mappedBy = "flashCard", cascade = CascadeType.ALL, orphanRemoval = true)
    List<LearningProgress> learningProgresses = new ArrayList<>();
}
