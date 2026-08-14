package com.hen.flastcard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hen.flastcard.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {}
