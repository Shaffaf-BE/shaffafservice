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

    /**
     * Find all units with block, unit type and project information for a specific project using native SQL.
     */
    @Query(
        value = """
        SELECT
            u.id as unit_id,
            u.unit_number,
            b.id as block_id,
            b.name as block_name,
            ut.id as unit_type_id,
            ut.name as unit_type_name,
            p.id as project_id,
            p.name as project_name,
            u.created_by,
            u.created_date
        FROM unit u
        INNER JOIN block b ON u.block_id = b.id
        INNER JOIN unit_type ut ON u.unit_type_id = ut.id
        INNER JOIN project p ON b.project_id = p.id
        WHERE p.id = :projectId
        AND u.deleted_on IS NULL
        AND b.deleted_on IS NULL
        AND ut.deleted_on IS NULL
        AND p.deleted_date IS NULL
        ORDER BY b.name, ut.name, CAST(u.unit_number AS INTEGER)
        """,
        countQuery = """
        SELECT COUNT(*)
        FROM unit u
        INNER JOIN block b ON u.block_id = b.id
        INNER JOIN unit_type ut ON u.unit_type_id = ut.id
        INNER JOIN project p ON b.project_id = p.id
        WHERE p.id = :projectId
        AND u.deleted_on IS NULL
        AND b.deleted_on IS NULL
        AND ut.deleted_on IS NULL
        AND p.deleted_date IS NULL
        """,
        nativeQuery = true
    )
    Page<Object[]> findAllUnitsWithDetailsNativeByProject(@Param("projectId") Long projectId, Pageable pageable);

    /**
     * Find all units with block, unit type and project information for seller's projects using native SQL.
     */
    @Query(
        value = """
        SELECT
            u.id as unit_id,
            u.unit_number,
            b.id as block_id,
            b.name as block_name,
            ut.id as unit_type_id,
            ut.name as unit_type_name,
            p.id as project_id,
            p.name as project_name,
            u.created_by,
            u.created_date
        FROM unit u
        INNER JOIN block b ON u.block_id = b.id
        INNER JOIN unit_type ut ON u.unit_type_id = ut.id
        INNER JOIN project p ON b.project_id = p.id
        INNER JOIN seller s ON p.seller_id = s.id
        WHERE p.id = :projectId
        AND s.id = :sellerId
        AND u.deleted_on IS NULL
        AND b.deleted_on IS NULL
        AND ut.deleted_on IS NULL
        AND p.deleted_date IS NULL
        ORDER BY b.name, ut.name, CAST(u.unit_number AS INTEGER)
        """,
        countQuery = """
        SELECT COUNT(*)
        FROM unit u
        INNER JOIN block b ON u.block_id = b.id
        INNER JOIN unit_type ut ON u.unit_type_id = ut.id
        INNER JOIN project p ON b.project_id = p.id
        INNER JOIN seller s ON p.seller_id = s.id
        WHERE p.id = :projectId
        AND s.id = :sellerId
        AND u.deleted_on IS NULL
        AND b.deleted_on IS NULL
        AND ut.deleted_on IS NULL
        AND p.deleted_date IS NULL
        """,
        nativeQuery = true
    )
    Page<Object[]> findAllUnitsWithDetailsNativeBySellerProject(
        @Param("projectId") Long projectId,
        @Param("sellerId") Long sellerId,
        Pageable pageable
    );

    /**
     * Check if a project is owned by a specific seller.
     */
    @Query(
        value = """
        SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END
        FROM project p
        WHERE p.id = :projectId
        AND p.seller_id = :sellerId
        AND p.deleted_date IS NULL
        """,
        nativeQuery = true
    )
    boolean isProjectOwnedBySellerNative(@Param("projectId") Long projectId, @Param("sellerId") Long sellerId);
}
