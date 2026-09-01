package com.hen.flastcard.repository;

import com.hen.flastcard.entity.InvalidatedTokenFamily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface InvalidatedTokenFamilyRepository
        extends JpaRepository<InvalidatedTokenFamily, String> {
    @Modifying
    @Query(value = """
        INSERT INTO invalidated_token_family (family_id, expiry_time)
        VALUES (:familyId, :expiryTime)
        ON DUPLICATE KEY UPDATE family_id = family_id
        """, nativeQuery = true)
    int revokeFamily(
            @Param("familyId") String familyId,
            @Param("expiryTime") Date expiryTime
    );
}