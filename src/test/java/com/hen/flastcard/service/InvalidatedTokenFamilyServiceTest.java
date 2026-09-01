package com.hen.flastcard.service;

import com.hen.flastcard.repository.InvalidatedTokenFamilyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InvalidatedTokenFamilyServiceTest {

    @InjectMocks
    private InvalidatedTokenFamilyService invalidatedTokenFamilyService;

    @Mock
    private InvalidatedTokenFamilyRepository invalidatedTokenFamilyRepository;

    @Test
    void revokeFamily_valid_success() {
        String familyId = "family-123";
        Date expiryTime = new Date(
                System.currentTimeMillis() + 60_000
        );

        invalidatedTokenFamilyService.revokeFamily(
                familyId,
                expiryTime
        );

        verify(invalidatedTokenFamilyRepository)
                .revokeFamily(familyId, expiryTime);
    }
}
