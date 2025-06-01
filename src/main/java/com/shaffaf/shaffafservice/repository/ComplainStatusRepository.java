package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.ComplainStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ComplainStatus entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ComplainStatusRepository extends JpaRepository<ComplainStatus, Long> {}
