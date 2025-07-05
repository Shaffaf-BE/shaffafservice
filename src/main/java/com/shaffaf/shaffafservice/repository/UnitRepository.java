package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.Unit;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Unit entity.
 *
 * When extending this class, extend UnitRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface UnitRepository extends UnitRepositoryWithBagRelationships, JpaRepository<Unit, Long> {
    default Optional<Unit> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Unit> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Unit> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }

    /**
     * Check if unit number exists in a specific block.
     */
    @Query("SELECT COUNT(u) > 0 FROM Unit u WHERE u.unitNumber = :unitNumber AND u.block.id = :blockId AND u.deletedOn IS NULL")
    boolean existsByUnitNumberAndBlockId(@Param("unitNumber") String unitNumber, @Param("blockId") Long blockId);

    /**
     * Find units by block and unit type with pagination.
     */
    @Query("SELECT u FROM Unit u WHERE u.block.id = :blockId AND u.unitType.id = :unitTypeId AND u.deletedOn IS NULL")
    Page<Unit> findByBlockIdAndUnitTypeId(@Param("blockId") Long blockId, @Param("unitTypeId") Long unitTypeId, Pageable pageable);
}
