package com.hen.flastcard.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class FlashCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 100)
    String word;

    @Column(nullable = false, length = 100)
    String hira_kata;

    @Column(nullable = false, length = 100)
    String meaning;

    @Column(nullable = false, length = 200)
    String example;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "deck_id")
    Deck deck;
    @OneToMany(mappedBy = "flashCard")
    List<LearningProgress> learningProgresses = new ArrayList<>();
}
