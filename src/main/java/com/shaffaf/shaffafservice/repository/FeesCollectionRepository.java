package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.FeesCollection;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FeesCollection entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FeesCollectionRepository extends JpaRepository<FeesCollection, Long> {}
