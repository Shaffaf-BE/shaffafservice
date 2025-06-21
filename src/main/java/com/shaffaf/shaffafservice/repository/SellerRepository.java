package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.Seller;
import com.shaffaf.shaffafservice.domain.enumeration.Status;
import java.time.Instant;
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

    /**
     * Save a seller entity using native SQL query with PostgreSQL's sequence.
     * This approach is thread-safe and handles high concurrency properly.
     *
     * @param firstName the first name of the seller
     * @param lastName the last name of the seller
     * @param email the email of the seller
     * @param phoneNumber the phone number of the seller
     * @param status the status of the seller
     * @param createdBy who created the seller
     * @param createdDate when the seller was created
     * @return The ID of the newly created seller
     */
    @Query(
        value = "INSERT INTO seller (id, first_name, last_name, email, phone_number, status, created_by, created_date) " +
        "VALUES (nextval('sequence_generator'), :firstName, :lastName, :email, :phoneNumber, :status, :createdBy, :createdDate) " +
        "RETURNING id",
        nativeQuery = true
    )
    Long saveWithNativeQueryReturningId(
        @Param("firstName") String firstName,
        @Param("lastName") String lastName,
        @Param("email") String email,
        @Param("phoneNumber") String phoneNumber,
        @Param("status") String status,
        @Param("createdBy") String createdBy,
        @Param("createdDate") Instant createdDate
    );

    /**
     * Update a seller entity using native SQL query.
     *
     * @param id the ID of the seller to update
     * @param firstName the first name of the seller
     * @param lastName the last name of the seller
     * @param email the email of the seller
     * @param phoneNumber the phone number of the seller
     * @param status the status of the seller
     * @param lastModifiedBy who last modified the seller
     * @param lastModifiedDate when the seller was last modified
     * @return The number of rows affected (should be 1)
     */
    @Modifying
    @Query(
        value = "UPDATE seller " +
        "SET first_name = :firstName, " +
        "    last_name = :lastName, " +
        "    email = :email, " +
        "    phone_number = :phoneNumber, " +
        "    status = :status, " +
        "    last_modified_by = :lastModifiedBy, " +
        "    last_modified_date = :lastModifiedDate " +
        "WHERE id = :id AND deleted_on IS NULL",
        nativeQuery = true
    )
    int updateWithNativeQuery(
        @Param("id") Long id,
        @Param("firstName") String firstName,
        @Param("lastName") String lastName,
        @Param("email") String email,
        @Param("phoneNumber") String phoneNumber,
        @Param("status") String status,
        @Param("lastModifiedBy") String lastModifiedBy,
        @Param("lastModifiedDate") Instant lastModifiedDate
    );

    /**
     * Find a seller by phone number using native SQL query.
     * This is used for authentication when sellers use their mobile number as user ID.
     *
     * @param phoneNumber the phone number of the seller
     * @return An Optional containing the Seller entity, or empty if not found
     */
    @Query(
        value = "SELECT s.* FROM seller s " +
        "WHERE s.phone_number = :phoneNumber " +
        "AND s.deleted_on IS NULL " +
        "AND s.status = 'ACTIVE'",
        nativeQuery = true
    )
    Optional<Seller> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}
