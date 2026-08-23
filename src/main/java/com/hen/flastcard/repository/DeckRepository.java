package com.hen.flastcard.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hen.flastcard.entity.Deck;

@Repository
public interface DeckRepository extends JpaRepository<Deck, Long> {
    List<Deck> findAllByUser_Id(Long userId);

    Optional<Deck> findByIdAndUser_Id(Long deckId, Long userId);
}
