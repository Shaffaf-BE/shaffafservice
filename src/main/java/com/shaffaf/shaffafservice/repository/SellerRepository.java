package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.Seller;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Seller entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    /**
     * Find all sellers with pagination and filtering using native SQL query.
     *
     * @param searchTerm optional search term to filter by first name, last name, or email
     * @param pageable pagination information
     * @return A Page of Seller entities
     */
    @Query(
        value = "SELECT s.* FROM seller s " +
        "WHERE (:searchTerm IS NULL OR " +
        "       LOWER(s.first_name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
        "       LOWER(s.last_name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
        "       LOWER(s.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
        "AND s.deleted_on IS NULL " +
        "ORDER BY s.id",
        countQuery = "SELECT COUNT(*) FROM seller s " +
        "WHERE (:searchTerm IS NULL OR " +
        "       LOWER(s.first_name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
        "       LOWER(s.last_name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
        "       LOWER(s.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
        "AND s.deleted_on IS NULL",
        nativeQuery = true
    )
    Page<Seller> findAllWithNativeQuery(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find a seller by ID using a secure and optimized native SQL query.
     * This query ensures that only non-deleted sellers are returned.
     *
     * @param id the ID of the seller to retrieve
     * @return An Optional containing the Seller entity, or empty if not found
     */
    @Query(value = "SELECT s.* FROM seller s " + "WHERE s.id = :id " + "AND s.deleted_on IS NULL", nativeQuery = true)
    Optional<Seller> findByIdOptimized(@Param("id") Long id);
}
