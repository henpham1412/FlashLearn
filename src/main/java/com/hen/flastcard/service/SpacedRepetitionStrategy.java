package com.hen.flastcard.service;

import org.springframework.stereotype.Service;

import com.hen.flastcard.entity.LearningProgress;

@Service
public interface SpacedRepetitionStrategy {
    public void updateProgress(LearningProgress progress, Integer quality);
}
