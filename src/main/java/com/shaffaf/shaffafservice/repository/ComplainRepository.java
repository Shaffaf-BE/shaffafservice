package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.Complain;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Complain entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ComplainRepository extends JpaRepository<Complain, Long> {}
