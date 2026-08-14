package com.hen.flastcard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hen.flastcard.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {}
