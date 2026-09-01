package com.hen.flastcard.repository;

import com.hen.flastcard.entity.InvalidatedTokenFamily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidatedTokenFamilyRepository
        extends JpaRepository<InvalidatedTokenFamily, String> {
}