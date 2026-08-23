package com.hen.flastcard.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(nullable = false, length = 100)
    String password;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;

    @OneToMany(mappedBy = "user")
    List<Deck> decks = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    List<LearningProgress> learningProgresses = new ArrayList<>();

    @ManyToMany
    Set<Role> roles;
}
