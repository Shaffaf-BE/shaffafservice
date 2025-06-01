package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.SellerCommission;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SellerCommission entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SellerCommissionRepository extends JpaRepository<SellerCommission, Long> {}
