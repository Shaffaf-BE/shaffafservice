package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.Block;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Block entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    /**
     * Save a new block with native SQL query.
     *
     * @param blockName the name of the block
     * @param projectId the ID of the project
     * @param username the username of the creator
     * @return the ID of the created block
     */
    @Query(
        value = "INSERT INTO block(name, project_id, created_by, created_date, last_modified_by, last_modified_date) " +
        "VALUES (:blockName, :projectId, :username, now(), :username, now()) RETURNING id",
        nativeQuery = true
    )
    Long saveBlock(@Param("blockName") String blockName, @Param("projectId") Long projectId, @Param("username") String username);

    /**
     * Update an existing block with native SQL query.
     *
     * @param blockId the ID of the block to update
     * @param blockName the new name of the block
     * @param projectId the new project ID
     * @param username the username of the modifier
     * @param deletedOn the deletion timestamp (nullable)
     */
    @Modifying
    @Query(
        value = "UPDATE block b " +
        "SET name = :blockName, " +
        "    project_id = :projectId, " +
        "    deleted_on = :deletedOn, " +
        "    last_modified_by = :username, " +
        "    last_modified_date = now() " +
        "WHERE b.id = :blockId",
        nativeQuery = true
    )
    void updateBlock(
        @Param("blockId") Long blockId,
        @Param("blockName") String blockName,
        @Param("projectId") Long projectId,
        @Param("username") String username,
        @Param("deletedOn") Instant deletedOn
    );

    /**
     * Find a block by ID using native SQL query, excluding soft-deleted records.
     *
     * @param id the ID of the block
     * @return Optional containing the block if found
     */
    @Query(value = "SELECT * FROM block WHERE id = :id AND (deleted_on IS NULL OR deleted_on > now())", nativeQuery = true)
    Optional<Block> findByIdNative(@Param("id") Long id);

    /**
     * Find all blocks by project ID with pagination using native SQL query.
     *
     * @param projectId the ID of the project
     * @param pageable pagination information
     * @return Page of blocks
     */
    @Query(
        value = "SELECT * FROM block WHERE project_id = :projectId AND (deleted_on IS NULL OR deleted_on > now()) ORDER BY id ASC",
        countQuery = "SELECT COUNT(*) FROM block WHERE project_id = :projectId AND (deleted_on IS NULL OR deleted_on > now())",
        nativeQuery = true
    )
    Page<Block> findAllByProjectIdNative(@Param("projectId") Long projectId, Pageable pageable);

    /**
     * Check if a block exists by ID using native SQL query, excluding soft-deleted records.
     *
     * @param id the ID of the block
     * @return true if the block exists, false otherwise
     */
    @Query(
        value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END " +
        "FROM block b " +
        "WHERE b.id = :id " +
        "AND (b.deleted_on IS NULL OR b.deleted_on > now())",
        nativeQuery = true
    )
    Boolean existsByIdNative(@Param("id") Long id);
}
