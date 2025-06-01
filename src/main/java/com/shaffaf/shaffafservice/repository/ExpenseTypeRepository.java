package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.ExpenseType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ExpenseType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExpenseTypeRepository extends JpaRepository<ExpenseType, Long> {}
