package com.hen.flastcard.service;

import com.hen.flastcard.entity.LearningProgress;
import org.springframework.stereotype.Service;

@Service
public interface SpacedRepetitionStrategy {
    public void updateProgress(LearningProgress progress, Integer quality);
}
