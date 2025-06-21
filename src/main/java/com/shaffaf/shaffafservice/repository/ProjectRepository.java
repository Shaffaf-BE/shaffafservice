package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.Project;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Project entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    /**
     * Create a new project using native SQL query for better performance and security.
     *
     * @param name Project name
     * @param description Project description
     * @param startDate Project start date
     * @param endDate Project end date (can be null)
     * @param status Project status
     * @param feesPerUnitPerMonth Fees per unit per month
     * @param unionHeadName Union head name
     * @param unionHeadMobileNumber Union head mobile number
     * @param numberOfUnits Number of units
     * @param sellerId ID of the seller creating the project
     * @param createdBy Username of the creator
     * @return The created project ID
     */
    @Query(
        value = "INSERT INTO project(" +
        "id, name, description, start_date, end_date, status, " +
        "fees_per_unit_per_month, union_head_name, union_head_mobile_number, " +
        "number_of_units, seller_id, created_by, created_date) " +
        "VALUES (nextval('project_seq'), :name, :description, :startDate, :endDate, :status, " +
        ":feesPerUnitPerMonth, :unionHeadName, :unionHeadMobileNumber, " +
        ":numberOfUnits, :sellerId, :createdBy, CURRENT_TIMESTAMP) " +
        "RETURNING id",
        nativeQuery = true
    )
    Long createProjectNative(
        @Param("name") String name,
        @Param("description") String description,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("status") String status,
        @Param("feesPerUnitPerMonth") BigDecimal feesPerUnitPerMonth,
        @Param("unionHeadName") String unionHeadName,
        @Param("unionHeadMobileNumber") String unionHeadMobileNumber,
        @Param("numberOfUnits") Integer numberOfUnits,
        @Param("sellerId") Long sellerId,
        @Param("createdBy") String createdBy
    );

    /**
     * Check if a seller exists.
     * Used for validation before creating a project.
     *
     * @param sellerId ID of the seller
     * @return true if seller exists
     */
    @Query(value = "SELECT COUNT(*) > 0 FROM seller WHERE id = :sellerId", nativeQuery = true)
    boolean existsSeller(@Param("sellerId") Long sellerId);

    /**
     * Update an existing project using native SQL query for better performance and security.
     * This query includes ownership validation to ensure only the project owner or admin can update.
     *
     * @param projectId ID of the project to update
     * @param name Project name
     * @param description Project description
     * @param startDate Project start date
     * @param endDate Project end date (can be null)
     * @param status Project status
     * @param feesPerUnitPerMonth Fees per unit per month
     * @param unionHeadName Union head name
     * @param unionHeadMobileNumber Union head mobile number
     * @param numberOfUnits Number of units
     * @param lastModifiedBy Username of the modifier
     * @return Number of rows affected (should be 1 if successful)
     */
    @Modifying
    @Query(
        value = "UPDATE project SET " +
        "name = TRIM(SUBSTRING(:name FROM 1 FOR 255)), " +
        "description = TRIM(SUBSTRING(:description FROM 1 FOR 1000)), " +
        "start_date = :startDate, " +
        "end_date = :endDate, " +
        "status = UPPER(TRIM(:status)), " +
        "fees_per_unit_per_month = ABS(:feesPerUnitPerMonth), " +
        "union_head_name = TRIM(SUBSTRING(:unionHeadName FROM 1 FOR 100)), " +
        "union_head_mobile_number = TRIM(:unionHeadMobileNumber), " +
        "number_of_units = ABS(:numberOfUnits), " +
        "last_modified_by = TRIM(:lastModifiedBy), " +
        "last_modified_date = CURRENT_TIMESTAMP " +
        "WHERE id = :projectId " +
        "AND EXISTS (SELECT 1 FROM seller WHERE id = (SELECT seller_id FROM project WHERE id = :projectId) AND deleted_on IS NULL) " +
        "AND LENGTH(TRIM(:name)) > 0 " +
        "AND LENGTH(TRIM(:unionHeadName)) > 0 " +
        "AND :numberOfUnits > 0 " +
        "AND :feesPerUnitPerMonth >= 0",
        nativeQuery = true
    )
    int updateProjectNative(
        @Param("projectId") Long projectId,
        @Param("name") String name,
        @Param("description") String description,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("status") String status,
        @Param("feesPerUnitPerMonth") BigDecimal feesPerUnitPerMonth,
        @Param("unionHeadName") String unionHeadName,
        @Param("unionHeadMobileNumber") String unionHeadMobileNumber,
        @Param("numberOfUnits") Integer numberOfUnits,
        @Param("lastModifiedBy") String lastModifiedBy
    );

    /**
     * Get project owner information using native SQL for security validation.
     * This is used to verify project ownership before updates.
     *
     * @param projectId ID of the project
     * @return Seller ID of the project owner, null if project doesn't exist
     */
    @Query(
        value = "SELECT p.seller_id FROM project p " +
        "WHERE p.id = :projectId " +
        "AND EXISTS (SELECT 1 FROM seller s WHERE s.id = p.seller_id AND s.deleted_on IS NULL)",
        nativeQuery = true
    )
    Long getProjectOwnerId(@Param("projectId") Long projectId);
}
