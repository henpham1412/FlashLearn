package com.hen.flastcard.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hen.flastcard.entity.InvalidatedToken;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
    @Modifying
    @Query(value = """
		INSERT INTO invalidated_token (id, family_id, expiry_time)
		VALUES (:jti, :familyId, :expiryTime)
		ON DUPLICATE KEY UPDATE id=id
		""", nativeQuery = true)
    int consume(@Param("jti") String jti, @Param("familyId") String familyId, @Param("expiryTime") Date expiryTime);
}
