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
    Optional<Seller> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    /**
     * Check if a seller is associated with a project using native SQL query.
     *
     * @param projectId the ID of the project
     * @param sellerPhoneNumber the phone number of the seller
     * @return true if the seller is associated with the project, false otherwise
     */
    @Query(
        value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END " +
        "FROM seller s " +
        "INNER JOIN project p ON s.id = p.seller_id " +
        "WHERE p.id = :projectId " +
        "AND s.phone_number = :sellerPhoneNumber " +
        "AND (s.deleted_on IS NULL OR s.deleted_on > now()) " +
        "AND s.status = 'ACTIVE'",
        nativeQuery = true
    )
    Boolean isSellerAssociatedWithProject(@Param("projectId") Long projectId, @Param("sellerPhoneNumber") String sellerPhoneNumber);

    /**
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

    /**
     * Get aggregated sales data for each unique seller.
     * Returns comprehensive sales statistics for each seller based on their projects.
     *
     * @param sortBy the field to sort by
     * @param sortDirection the sort direction (ASC or DESC)
     * @param pageable pagination information
     * @return Page of aggregated seller sales data
     */
    @Query(
        value = "SELECT " +
        "s.id as seller_id, " +
        "TRIM(CONCAT(COALESCE(s.first_name, ''), ' ', COALESCE(s.last_name, ''))) as seller_name, " +
        "COALESCE(s.phone_number, '') as seller_phone_number, " +
        "COALESCE(s.email, '') as seller_email, " +
        "COALESCE(COUNT(p.id), 0) as total_projects, " +
        "COALESCE(SUM(p.number_of_units), 0) as total_units, " +
        "COALESCE(SUM(p.fees_per_unit_per_month * p.number_of_units), 0) as total_sales_amount, " +
        "COALESCE(AVG(p.fees_per_unit_per_month), 0) as average_fees_per_unit, " +
        "COALESCE(MAX(p.fees_per_unit_per_month * p.number_of_units), 0) as highest_project_amount, " +
        "COALESCE(MIN(p.fees_per_unit_per_month * p.number_of_units), 0) as lowest_project_amount, " +
        "COALESCE(p_recent.name, 'No Projects') as most_recent_project_name, " +
        "COALESCE(p_recent.created_date, s.created_date) as last_project_date, " +
        "s.status as seller_status " +
        "FROM seller s " +
        "LEFT JOIN project p ON s.id = p.seller_id AND p.deleted_date IS NULL " +
        "LEFT JOIN LATERAL (" +
        "   SELECT p2.name, p2.created_date " +
        "   FROM project p2 " +
        "   WHERE p2.seller_id = s.id AND p2.deleted_date IS NULL " +
        "   ORDER BY p2.created_date DESC " +
        "   LIMIT 1" +
        ") p_recent ON true " +
        "WHERE s.deleted_on IS NULL AND s.status = 'ACTIVE' " +
        "GROUP BY s.id, s.first_name, s.last_name, s.phone_number, s.email, s.status, s.created_date, p_recent.name, p_recent.created_date " +
        "ORDER BY " +
        "CASE WHEN :sortBy = 'sellerName' AND :sortDirection = 'ASC' THEN TRIM(CONCAT(COALESCE(s.first_name, ''), ' ', COALESCE(s.last_name, ''))) END ASC, " +
        "CASE WHEN :sortBy = 'sellerName' AND :sortDirection = 'DESC' THEN TRIM(CONCAT(COALESCE(s.first_name, ''), ' ', COALESCE(s.last_name, ''))) END DESC, " +
        "CASE WHEN :sortBy = 'totalSalesAmount' AND :sortDirection = 'ASC' THEN COALESCE(SUM(p.fees_per_unit_per_month * p.number_of_units), 0) END ASC, " +
        "CASE WHEN :sortBy = 'totalSalesAmount' AND :sortDirection = 'DESC' THEN COALESCE(SUM(p.fees_per_unit_per_month * p.number_of_units), 0) END DESC, " +
        "CASE WHEN :sortBy = 'totalProjects' AND :sortDirection = 'ASC' THEN COALESCE(COUNT(p.id), 0) END ASC, " +
        "CASE WHEN :sortBy = 'totalProjects' AND :sortDirection = 'DESC' THEN COALESCE(COUNT(p.id), 0) END DESC, " +
        "CASE WHEN :sortBy = 'totalUnits' AND :sortDirection = 'ASC' THEN COALESCE(SUM(p.number_of_units), 0) END ASC, " +
        "CASE WHEN :sortBy = 'totalUnits' AND :sortDirection = 'DESC' THEN COALESCE(SUM(p.number_of_units), 0) END DESC, " +
        "COALESCE(SUM(p.fees_per_unit_per_month * p.number_of_units), 0) DESC, s.id DESC",
        countQuery = "SELECT COUNT(DISTINCT s.id) FROM seller s " +
        "LEFT JOIN project p ON s.id = p.seller_id AND p.deleted_date IS NULL " +
        "WHERE s.deleted_on IS NULL AND s.status = 'ACTIVE'",
        nativeQuery = true
    )
    Page<Object[]> getSellerSalesAggregates(
        @Param("sortBy") String sortBy,
        @Param("sortDirection") String sortDirection,
        Pageable pageable
    );

    /**
     * Simple test of seller sales aggregation for debugging.
     */
    @Query(
        value = "SELECT " +
        "s.id, " +
        "CONCAT(s.first_name, ' ', s.last_name) as seller_name, " +
        "COUNT(p.id) as project_count, " +
        "COALESCE(SUM(p.number_of_units), 0) as total_units, " +
        "COALESCE(SUM(p.fees_per_unit_per_month * p.number_of_units), 0) as total_amount " +
        "FROM seller s " +
        "LEFT JOIN project p ON s.id = p.seller_id AND p.deleted_date IS NULL " +
        "WHERE s.deleted_on IS NULL AND s.status = 'ACTIVE' " +
        "GROUP BY s.id, s.first_name, s.last_name " +
        "ORDER BY total_amount DESC " +
        "LIMIT 5",
        nativeQuery = true
    )
    List<Object[]> getSimpleSellerSalesAggregates();

    /**
     * Get seller's personal aggregated statistics using native SQL.
     *
     * @param sellerId the ID of the seller
     * @return Object array with seller statistics
     */
    @Query(
        value = "SELECT " +
        "s.id as seller_id, " +
        "TRIM(CONCAT(COALESCE(s.first_name, ''), ' ', COALESCE(s.last_name, ''))) as seller_name, " +
        "COALESCE(s.phone_number, '') as phone_number, " +
        "COALESCE(s.email, '') as email, " +
        "COALESCE(COUNT(p.id), 0) as total_projects, " +
        "COALESCE(SUM(p.fees_per_unit_per_month * p.number_of_units), 0) as total_payment, " +
        "COALESCE(SUM(CASE WHEN p.status = 'PENDING' THEN p.fees_per_unit_per_month * p.number_of_units ELSE 0 END), 0) as total_dues, " +
        "COALESCE(COUNT(CASE WHEN p.created_date >= CURRENT_DATE - INTERVAL '30 days' THEN p.id END), 0) as new_projects " +
        "FROM seller s " +
        "LEFT JOIN project p ON s.id = p.seller_id AND p.deleted_date IS NULL " +
        "WHERE s.id = :sellerId AND s.deleted_on IS NULL AND s.status = 'ACTIVE' " +
        "GROUP BY s.id, s.first_name, s.last_name, s.phone_number, s.email",
        nativeQuery = true
    )
    Optional<Object[]> getSellerPersonalStatistics(@Param("sellerId") Long sellerId);

    /**
     * Get seller's projects with pagination and sorting using native SQL.
     *
     * @param sellerId the ID of the seller
     * @param sortBy the field to sort by
     * @param sortDirection the sort direction (ASC or DESC)
     * @param pageable pagination information
     * @return Page of seller's projects
     */
    @Query(
        value = "SELECT " +
        "p.id as project_id, " +
        "p.name as project_name, " +
        "(p.fees_per_unit_per_month * p.number_of_units) as amount, " +
        "p.number_of_units as number_of_units, " +
        "p.fees_per_unit_per_month as fees_per_unit, " +
        "p.created_date as created_date, " +
        "p.status as status, " +
        "COALESCE(p.description, 'Project for seller') as description, " +
        "(p.fees_per_unit_per_month * p.number_of_units) as total_revenue, " +
        "CASE " +
        "   WHEN p.created_date IS NOT NULL THEN " +
        "       EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - p.created_date))::INTEGER / 86400 " +
        "   ELSE 0 " +
        "END as days_active " +
        "FROM project p " +
        "INNER JOIN seller s ON p.seller_id = s.id " +
        "WHERE s.id = :sellerId AND s.deleted_on IS NULL AND s.status = 'ACTIVE' AND p.deleted_date IS NULL " +
        "ORDER BY " +
        "CASE WHEN :sortBy = 'projectName' AND :sortDirection = 'ASC' THEN p.name END ASC, " +
        "CASE WHEN :sortBy = 'projectName' AND :sortDirection = 'DESC' THEN p.name END DESC, " +
        "CASE WHEN :sortBy = 'amount' AND :sortDirection = 'ASC' THEN (p.fees_per_unit_per_month * p.number_of_units) END ASC, " +
        "CASE WHEN :sortBy = 'amount' AND :sortDirection = 'DESC' THEN (p.fees_per_unit_per_month * p.number_of_units) END DESC, " +
        "CASE WHEN :sortBy = 'createdDate' AND :sortDirection = 'ASC' THEN p.created_date END ASC, " +
        "CASE WHEN :sortBy = 'createdDate' AND :sortDirection = 'DESC' THEN p.created_date END DESC, " +
        "CASE WHEN :sortBy = 'status' AND :sortDirection = 'ASC' THEN p.status END ASC, " +
        "CASE WHEN :sortBy = 'status' AND :sortDirection = 'DESC' THEN p.status END DESC, " +
        "p.created_date DESC, p.id DESC",
        countQuery = "SELECT COUNT(*) FROM project p " +
        "INNER JOIN seller s ON p.seller_id = s.id " +
        "WHERE s.id = :sellerId AND s.deleted_on IS NULL AND s.status = 'ACTIVE' AND p.deleted_date IS NULL",
        nativeQuery = true
    )
    Page<Object[]> getSellerProjects(
        @Param("sellerId") Long sellerId,
        @Param("sortBy") String sortBy,
        @Param("sortDirection") String sortDirection,
        Pageable pageable
    );/**
     * Get count of sellers referred by a specific seller (for new sellers metric).
     * Falls back to 0 if referred_by column doesn't exist.
     *
     * @param sellerId the ID of the referring seller
     * @return count of referred sellers
     */

    @Query(
        value = "SELECT COALESCE(COUNT(*), 0) FROM seller s " +
        "WHERE s.deleted_on IS NULL AND s.status = 'ACTIVE' " +
        "AND s.created_date >= CURRENT_DATE - INTERVAL '30 days' " +
        "AND s.created_by = CAST(:sellerId AS VARCHAR)",
        nativeQuery = true
    )
    Long getNewSellersReferredBy(@Param("sellerId") Long sellerId);

    /**
     * Verify seller exists and is active.
     *
     * @param sellerId the ID of the seller to check
     * @return true if seller exists and is active, false otherwise
     */
    @Query(
        value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
        "FROM seller s WHERE s.id = :sellerId AND s.deleted_on IS NULL AND s.status = 'ACTIVE'",
        nativeQuery = true
    )
    Boolean isSellerActiveById(@Param("sellerId") Long sellerId);
}
