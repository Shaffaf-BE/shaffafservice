package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.UnitType;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * Save a new unit type with native SQL query.
     *
     * @param unitTypeName the name of the unit type
     * @param projectId the ID of the project
     * @param username the username of the creator
     * @return the ID of the created unit type
     */
    @Query(
        value = "INSERT INTO unit_type(name, project_id, created_by, created_date, last_modified_by, last_modified_date) " +
        "VALUES (:unitTypeName, :projectId, :username, now(), :username, now()) RETURNING id",
        nativeQuery = true
    )
    Long saveUnitType(@Param("unitTypeName") String unitTypeName, @Param("projectId") Long projectId, @Param("username") String username);

    /**
     * Update an existing unit type with native SQL query.
     *
     * @param unitTypeId the ID of the unit type to update
     * @param unitTypeName the new name of the unit type
     * @param projectId the new project ID
     * @param username the username of the modifier
     * @param deletedOn the deletion timestamp (nullable)
     */
    @Modifying
    @Query(
        value = "UPDATE unit_type ut " +
        "SET name = :unitTypeName, " +
        "    project_id = :projectId, " +
        "    deleted_on = :deletedOn, " +
        "    last_modified_by = :username, " +
        "    last_modified_date = now() " +
        "WHERE ut.id = :unitTypeId",
        nativeQuery = true
    )
    void updateUnitType(
        @Param("unitTypeId") Long unitTypeId,
        @Param("unitTypeName") String unitTypeName,
        @Param("projectId") Long projectId,
        @Param("username") String username,
        @Param("deletedOn") Instant deletedOn
    );

    /**
     * Find a unit type by ID using native SQL query, excluding soft-deleted records.
     *
     * @param id the ID of the unit type
     * @return Optional containing the unit type if found
     */
    @Query(value = "SELECT * FROM unit_type WHERE id = :id AND (deleted_on IS NULL OR deleted_on > now())", nativeQuery = true)
    Optional<UnitType> findByIdNative(@Param("id") Long id);

    /**
     * Find all unit types by project ID with pagination using native SQL query.
     *
     * @param projectId the ID of the project
     * @param pageable pagination information
     * @return Page of unit types
     */
    @Query(
        value = "SELECT * FROM unit_type WHERE project_id = :projectId AND (deleted_on IS NULL OR deleted_on > now()) ORDER BY id ASC",
        countQuery = "SELECT COUNT(*) FROM unit_type WHERE project_id = :projectId AND (deleted_on IS NULL OR deleted_on > now())",
        nativeQuery = true
    )
    Page<UnitType> findAllByProjectIdNative(@Param("projectId") Long projectId, Pageable pageable);

    /**
     * Check if a unit type exists by ID using native SQL query, excluding soft-deleted records.
     *
     * @param id the ID of the unit type
     * @return true if the unit type exists, false otherwise
     */
    @Query(
        value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END " +
        "FROM unit_type ut " +
        "WHERE ut.id = :id " +
        "AND (ut.deleted_on IS NULL OR ut.deleted_on > now())",
        nativeQuery = true
    )
    boolean existsByIdNative(@Param("id") Long id);
}
