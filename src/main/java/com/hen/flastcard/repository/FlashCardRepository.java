package com.hen.flastcard.repository;

import com.hen.flastcard.entity.FlashCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlashCardRepository extends JpaRepository<FlashCard, Long> {
    List<FlashCard> findAllByDeck_Id(Long id);
    Optional<List<FlashCard>> findAllByDeck_IdAndDeck_User_Id(Long deckId, Long userId);
}
