package com.hen.flastcard.service;

import com.hen.flastcard.entity.InvalidatedTokenFamily;
import com.hen.flastcard.repository.InvalidatedTokenFamilyRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvalidatedTokenFamilyService {
    InvalidatedTokenFamilyRepository invalidatedTokenFamilyRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeFamily(String familyId, Date expiryTime) {
        if (!invalidatedTokenFamilyRepository.existsById(familyId)) {
            invalidatedTokenFamilyRepository.save(
                    InvalidatedTokenFamily.builder()
                            .familyId(familyId)
                            .expiryTime(expiryTime)
                            .build()
            );
        }
    }
}
