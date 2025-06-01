package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.FeesConfiguration;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FeesConfiguration entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FeesConfigurationRepository extends JpaRepository<FeesConfiguration, Long> {}
