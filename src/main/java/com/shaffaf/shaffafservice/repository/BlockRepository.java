package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.Block;
import java.util.Optional;
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
     * Find block by name and project ID.
     */
    @Query("SELECT b FROM Block b WHERE b.name = :name AND b.project.id = :projectId AND b.deletedOn IS NULL")
    Optional<Block> findByNameAndProjectId(@Param("name") String name, @Param("projectId") Long projectId);

    /**
     * Check if block exists by name and project ID.
     */
    @Query("SELECT COUNT(b) > 0 FROM Block b WHERE b.name = :name AND b.project.id = :projectId AND b.deletedOn IS NULL")
    boolean existsByNameAndProjectId(@Param("name") String name, @Param("projectId") Long projectId);
}
