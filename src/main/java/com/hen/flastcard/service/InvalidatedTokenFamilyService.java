package com.hen.flastcard.service;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hen.flastcard.repository.InvalidatedTokenFamilyRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvalidatedTokenFamilyService {
    InvalidatedTokenFamilyRepository invalidatedTokenFamilyRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeFamily(String familyId, Date expiryTime) {
        invalidatedTokenFamilyRepository.revokeFamily(familyId, expiryTime);
    }
}
