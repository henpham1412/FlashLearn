package com.hen.flastcard.repository;

import com.hen.flastcard.entity.LearningProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LearningProgressRepository extends JpaRepository<LearningProgress, Long> {
    List<LearningProgress> findAllByUser_IdAndFlashCard_Deck_Id(Long userId, Long decKId);
}
