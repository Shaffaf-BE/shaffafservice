package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.UnitType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UnitType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UnitTypeRepository extends JpaRepository<UnitType, Long> {}
