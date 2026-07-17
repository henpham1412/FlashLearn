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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true, length = 50)
    String username;
    @Column(nullable = false, unique = true, length = 100)
    String email;
    @Column(nullable = false)
    String password;
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDate createdAt;
    @OneToMany(mappedBy = "user")
    List<Deck> decks = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    List<LearningProgress> learningProgresses = new ArrayList<>();
}
