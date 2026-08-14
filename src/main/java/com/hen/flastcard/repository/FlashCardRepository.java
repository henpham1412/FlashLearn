package com.hen.flastcard.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hen.flastcard.entity.FlashCard;

@Repository
public interface FlashCardRepository extends JpaRepository<FlashCard, Long> {
    List<FlashCard> findAllByDeck_Id(Long id);

    List<FlashCard> findAllByDeck_IdAndDeck_User_Id(Long deckId, Long userId);

    Optional<FlashCard> findByIdAndDeck_User_Id(Long cardId, Long userId);
}
