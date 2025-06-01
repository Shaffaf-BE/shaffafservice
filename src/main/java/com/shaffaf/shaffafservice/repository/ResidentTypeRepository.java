package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.ResidentType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ResidentType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ResidentTypeRepository extends JpaRepository<ResidentType, Long> {}
