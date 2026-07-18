package com.hen.flastcard.repository;

import com.hen.flastcard.entity.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeckRepository extends JpaRepository<Deck, Long> {
    List<Deck> findAllByUser_Id(Long userId);
}
