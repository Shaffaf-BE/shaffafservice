package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.UnitType;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UnitType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UnitTypeRepository extends JpaRepository<UnitType, Long> {
    /**
     * Find unit type by name.
     */
    @Query("SELECT ut FROM UnitType ut WHERE ut.name = :name AND ut.deletedOn IS NULL")
    Optional<UnitType> findByName(@Param("name") String name);

    /**
     * Check if unit type exists by name.
     */
    @Query("SELECT COUNT(ut) > 0 FROM UnitType ut WHERE ut.name = :name AND ut.deletedOn IS NULL")
    boolean existsByName(@Param("name") String name);
}
