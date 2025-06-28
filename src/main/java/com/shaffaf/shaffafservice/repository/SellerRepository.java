package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.Seller;
import com.shaffaf.shaffafservice.domain.enumeration.Status;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
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
    Optional<Seller> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);/**
     * Get total payment amount using native SQL.
     */

    @Query(
        value = "SELECT CAST(COALESCE(SUM(p.fees_per_unit_per_month * p.number_of_units), 0) AS DECIMAL(19,2)) " +
        "FROM seller s LEFT JOIN project p ON s.id = p.seller_id " +
        "WHERE s.deleted_on IS NULL AND s.status = 'ACTIVE' AND (p.id IS NULL OR p.deleted_date IS NULL)",
        nativeQuery = true
    )
    BigDecimal getTotalPayment();

    /**
     * Get total sellers count using native SQL.
     */
    @Query(
        value = "SELECT CAST(COUNT(DISTINCT s.id) AS BIGINT) " +
        "FROM seller s LEFT JOIN project p ON s.id = p.seller_id " +
        "WHERE s.deleted_on IS NULL AND s.status = 'ACTIVE' AND (p.id IS NULL OR p.deleted_date IS NULL)",
        nativeQuery = true
    )
    Long getTotalSellers();

    /**
     * Get new sellers count (last 30 days) using native SQL.
     */
    @Query(
        value = "SELECT CAST(COUNT(DISTINCT CASE WHEN s.created_date >= CURRENT_DATE - INTERVAL '30 days' THEN s.id END) AS BIGINT) " +
        "FROM seller s LEFT JOIN project p ON s.id = p.seller_id " +
        "WHERE s.deleted_on IS NULL AND s.status = 'ACTIVE' AND (p.id IS NULL OR p.deleted_date IS NULL)",
        nativeQuery = true
    )
    Long getNewSellers();

    /**
     * Get total dues using native SQL.
     */
    @Query(
        value = "SELECT CAST(COALESCE(SUM(CASE WHEN p.status = 'PENDING' THEN p.fees_per_unit_per_month * p.number_of_units ELSE 0 END), 0) AS DECIMAL(19,2)) " +
        "FROM seller s LEFT JOIN project p ON s.id = p.seller_id " +
        "WHERE s.deleted_on IS NULL AND s.status = 'ACTIVE' AND (p.id IS NULL OR p.deleted_date IS NULL)",
        nativeQuery = true
    )
    BigDecimal getTotalDues();

    /**
     * Get transaction details with pagination and sorting using native SQL.
     * Uses correct soft delete columns: s.deleted_on and p.deleted_date.
     *
     * @param sortBy the field to sort by
     * @param sortDirection the sort direction (ASC or DESC)
     * @param pageable pagination information
     * @return Page of transaction details
     */@Query(
        value = "SELECT " +
        "COALESCE(p.id, s.id) as id, " +
        "TRIM(CONCAT(COALESCE(s.first_name, ''), ' ', COALESCE(s.last_name, ''))) as seller_name, " +
        "COALESCE(s.phone_number, '') as seller_phone_number, " +
        "COALESCE(p.name, 'No Project') as project_name, " +
        "COALESCE((p.fees_per_unit_per_month * p.number_of_units), 0) as amount, " +
        "COALESCE(p.number_of_units, 0) as number_of_units, " +
        "COALESCE(p.fees_per_unit_per_month, 0) as fees_per_unit, " +
        "COALESCE(p.created_date, s.created_date) as transaction_date, " +
        "COALESCE(p.status, s.status) as status, " +
        "COALESCE(p.description, 'Seller record') as description " +
        "FROM seller s " +
        "LEFT JOIN project p ON s.id = p.seller_id AND p.deleted_date IS NULL " +
        "WHERE s.deleted_on IS NULL AND s.status = 'ACTIVE' " +
        "ORDER BY " +
        "CASE WHEN :sortBy = 'sellerName' AND :sortDirection = 'ASC' THEN TRIM(CONCAT(COALESCE(s.first_name, ''), ' ', COALESCE(s.last_name, ''))) END ASC, " +
        "CASE WHEN :sortBy = 'sellerName' AND :sortDirection = 'DESC' THEN TRIM(CONCAT(COALESCE(s.first_name, ''), ' ', COALESCE(s.last_name, ''))) END DESC, " +
        "CASE WHEN :sortBy = 'amount' AND :sortDirection = 'ASC' THEN COALESCE((p.fees_per_unit_per_month * p.number_of_units), 0) END ASC, " +
        "CASE WHEN :sortBy = 'amount' AND :sortDirection = 'DESC' THEN COALESCE((p.fees_per_unit_per_month * p.number_of_units), 0) END DESC, " +
        "CASE WHEN :sortBy = 'transactionDate' AND :sortDirection = 'ASC' THEN COALESCE(p.created_date, s.created_date) END ASC, " +
        "CASE WHEN :sortBy = 'transactionDate' AND :sortDirection = 'DESC' THEN COALESCE(p.created_date, s.created_date) END DESC, " +
        "p.id DESC NULLS LAST, s.id DESC",
        countQuery = "SELECT COUNT(*) FROM seller s " +
        "LEFT JOIN project p ON s.id = p.seller_id AND p.deleted_date IS NULL " +
        "WHERE s.deleted_on IS NULL AND s.status = 'ACTIVE'",
        nativeQuery = true
    )
    Page<Object[]> getTransactionDetails(
        @Param("sortBy") String sortBy,
        @Param("sortDirection") String sortDirection,
        Pageable pageable
    );/**
     * Simple count of active sellers for debugging.
     */

    @Query(value = "SELECT COUNT(*) FROM seller s WHERE s.deleted_on IS NULL AND s.status = 'ACTIVE'", nativeQuery = true)
    Long countActiveSellers();/**
     * Get simplified transaction details for debugging - shows all active sellers.
     */

    @Query(
        value = "SELECT " +
        "s.id, " +
        "CONCAT(COALESCE(s.first_name, ''), ' ', COALESCE(s.last_name, '')) as seller_name, " +
        "COALESCE(s.phone_number, '') as seller_phone_number, " +
        "'Sample Project' as project_name, " +
        "1000.00 as amount, " +
        "1 as number_of_units, " +
        "1000.00 as fees_per_unit, " +
        "s.created_date as transaction_date, " +
        "'ACTIVE' as status, " +
        "'Sample transaction for seller' as description " +
        "FROM seller s " +
        "WHERE s.deleted_on IS NULL AND s.status = 'ACTIVE' " +
        "ORDER BY s.id " +
        "LIMIT 10",
        nativeQuery = true
    )
    List<Object[]> getSimpleTransactionDetails();

    /**
     * Get transaction details for sellers with actual projects - prioritizes real project data.
     * This query returns sellers with their associated projects first, then sellers without projects.
     *
     * @param sortBy the field to sort by
     * @param sortDirection the sort direction (ASC or DESC)
     * @param pageable pagination information
     * @return Page of transaction details with real project data prioritized
     */@Query(
        value = "SELECT * FROM (" +
        "SELECT " +
        "p.id as id, " +
        "TRIM(CONCAT(COALESCE(s.first_name, ''), ' ', COALESCE(s.last_name, ''))) as seller_name, " +
        "COALESCE(s.phone_number, '') as seller_phone_number, " +
        "p.name as project_name, " +
        "(p.fees_per_unit_per_month * p.number_of_units) as amount, " +
        "p.number_of_units as number_of_units, " +
        "p.fees_per_unit_per_month as fees_per_unit, " +
        "p.created_date as transaction_date, " +
        "p.status as status, " +
        "COALESCE(p.description, 'Project transaction') as description " +
        "FROM project p " +
        "INNER JOIN seller s ON p.seller_id = s.id " +
        "WHERE s.deleted_on IS NULL AND s.status = 'ACTIVE' AND p.deleted_date IS NULL " +
        "UNION ALL " +
        "SELECT " +
        "s.id as id, " +
        "TRIM(CONCAT(COALESCE(s.first_name, ''), ' ', COALESCE(s.last_name, ''))) as seller_name, " +
        "COALESCE(s.phone_number, '') as seller_phone_number, " +
        "'No Project' as project_name, " +
        "0 as amount, " +
        "0 as number_of_units, " +
        "0 as fees_per_unit, " +
        "s.created_date as transaction_date, " +
        "s.status as status, " +
        "'Seller without project' as description " +
        "FROM seller s " +
        "WHERE s.deleted_on IS NULL AND s.status = 'ACTIVE' " +
        "AND NOT EXISTS (SELECT 1 FROM project p WHERE p.seller_id = s.id AND p.deleted_date IS NULL) " +
        ") AS combined_data " +
        "ORDER BY " +
        "CASE WHEN :sortBy = 'sellerName' AND :sortDirection = 'ASC' THEN seller_name END ASC, " +
        "CASE WHEN :sortBy = 'sellerName' AND :sortDirection = 'DESC' THEN seller_name END DESC, " +
        "CASE WHEN :sortBy = 'amount' AND :sortDirection = 'ASC' THEN amount END ASC, " +
        "CASE WHEN :sortBy = 'amount' AND :sortDirection = 'DESC' THEN amount END DESC, " +
        "CASE WHEN :sortBy = 'transactionDate' AND :sortDirection = 'ASC' THEN transaction_date END ASC, " +
        "CASE WHEN :sortBy = 'transactionDate' AND :sortDirection = 'DESC' THEN transaction_date END DESC, " +
        "amount DESC, id DESC",
        countQuery = "SELECT COUNT(*) FROM (" +
        "SELECT p.id FROM project p " +
        "INNER JOIN seller s ON p.seller_id = s.id " +
        "WHERE s.deleted_on IS NULL AND s.status = 'ACTIVE' AND p.deleted_date IS NULL " +
        "UNION ALL " +
        "SELECT s.id FROM seller s " +
        "WHERE s.deleted_on IS NULL AND s.status = 'ACTIVE' " +
        "AND NOT EXISTS (SELECT 1 FROM project p WHERE p.seller_id = s.id AND p.deleted_date IS NULL)" +
        ") AS total_records",
        nativeQuery = true
    )
    Page<Object[]> getTransactionDetailsWithRealProjects(
        @Param("sortBy") String sortBy,
        @Param("sortDirection") String sortDirection,
        Pageable pageable
    );

    /**
     * Get only actual project transactions (no sellers without projects).
     * This query focuses solely on real project data with number_of_units and fees_per_unit_per_month.
     *
     * @param sortBy the field to sort by
     * @param sortDirection the sort direction (ASC or DESC)
     * @param pageable pagination information
     * @return Page of actual project transaction details
     */
    @Query(
        value = "SELECT " +
        "p.id as id, " +
        "TRIM(CONCAT(COALESCE(s.first_name, ''), ' ', COALESCE(s.last_name, ''))) as seller_name, " +
        "COALESCE(s.phone_number, '') as seller_phone_number, " +
        "p.name as project_name, " +
        "(p.fees_per_unit_per_month * p.number_of_units) as amount, " +
        "p.number_of_units as number_of_units, " +
        "p.fees_per_unit_per_month as fees_per_unit, " +
        "p.created_date as transaction_date, " +
        "p.status as status, " +
        "COALESCE(p.description, 'Project transaction') as description " +
        "FROM project p " +
        "INNER JOIN seller s ON p.seller_id = s.id " +
        "WHERE s.deleted_on IS NULL AND s.status = 'ACTIVE' AND p.deleted_date IS NULL " +
        "ORDER BY " +
        "CASE WHEN :sortBy = 'sellerName' AND :sortDirection = 'ASC' THEN TRIM(CONCAT(COALESCE(s.first_name, ''), ' ', COALESCE(s.last_name, ''))) END ASC, " +
        "CASE WHEN :sortBy = 'sellerName' AND :sortDirection = 'DESC' THEN TRIM(CONCAT(COALESCE(s.first_name, ''), ' ', COALESCE(s.last_name, ''))) END DESC, " +
        "CASE WHEN :sortBy = 'amount' AND :sortDirection = 'ASC' THEN (p.fees_per_unit_per_month * p.number_of_units) END ASC, " +
        "CASE WHEN :sortBy = 'amount' AND :sortDirection = 'DESC' THEN (p.fees_per_unit_per_month * p.number_of_units) END DESC, " +
        "CASE WHEN :sortBy = 'transactionDate' AND :sortDirection = 'ASC' THEN p.created_date END ASC, " +
        "CASE WHEN :sortBy = 'transactionDate' AND :sortDirection = 'DESC' THEN p.created_date END DESC, " +
        "(p.fees_per_unit_per_month * p.number_of_units) DESC, p.id DESC",
        countQuery = "SELECT COUNT(*) FROM project p " +
        "INNER JOIN seller s ON p.seller_id = s.id " +
        "WHERE s.deleted_on IS NULL AND s.status = 'ACTIVE' AND p.deleted_date IS NULL",
        nativeQuery = true
    )
    Page<Object[]> getActualProjectTransactions(
        @Param("sortBy") String sortBy,
        @Param("sortDirection") String sortDirection,
        Pageable pageable
    );

    /**
     * Simple count of active projects for debugging.
     */
    @Query(
        value = "SELECT COUNT(*) FROM project p " +
        "INNER JOIN seller s ON p.seller_id = s.id " +
        "WHERE s.deleted_on IS NULL AND s.status = 'ACTIVE' AND p.deleted_date IS NULL",
        nativeQuery = true
    )
    Long countActiveProjects();

    /**
     * Get sample of actual project data for debugging.
     */
    @Query(
        value = "SELECT " +
        "p.id, p.name, p.fees_per_unit_per_month, p.number_of_units, " +
        "CONCAT(s.first_name, ' ', s.last_name) as seller_name, p.status " +
        "FROM project p " +
        "INNER JOIN seller s ON p.seller_id = s.id " +
        "WHERE s.deleted_on IS NULL AND s.status = 'ACTIVE' AND p.deleted_date IS NULL " +
        "LIMIT 5",
        nativeQuery = true
    )
    List<Object[]> getSampleProjectData();
}
