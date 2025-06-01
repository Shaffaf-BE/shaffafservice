package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.ComplainType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ComplainType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ComplainTypeRepository extends JpaRepository<ComplainType, Long> {}
