package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.ProjectDiscount;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProjectDiscount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectDiscountRepository extends JpaRepository<ProjectDiscount, Long> {}
