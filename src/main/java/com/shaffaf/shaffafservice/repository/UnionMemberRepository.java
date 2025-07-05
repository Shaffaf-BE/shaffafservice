package com.shaffaf.shaffafservice.repository;

import com.shaffaf.shaffafservice.domain.UnionMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UnionMember entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UnionMemberRepository extends JpaRepository<UnionMember, Long> {
    /**
     * Find all union members who are union heads.
     */
    List<UnionMember> findByIsUnionHeadTrue();

    /**
     * Find all union members who are not union heads.
     */
    List<UnionMember> findByIsUnionHeadFalse();

    /**
     * Find union members by project ID and union head status.
     */
    List<UnionMember> findByProjectIdAndIsUnionHead(Long projectId, Boolean isUnionHead);

    /**
     * Find all union members for a specific project using native SQL with pagination.
     */@Query(
        value = """
        SELECT um.id, um.first_name, um.last_name, um.email, um.phone_number,
               um.created_by, um.created_date, um.last_modified_by, um.last_modified_date,
               um.deleted_on, um.is_union_head, um.project_id,
               p.name as project_name, p.description as project_description
        FROM union_member um
        LEFT JOIN project p ON um.project_id = p.id
        WHERE um.project_id = :projectId
        AND um.deleted_on IS NULL
        ORDER BY um.is_union_head DESC, um.created_date ASC
        """,
        countQuery = """
        SELECT COUNT(*)
        FROM union_member um
        WHERE um.project_id = :projectId
        AND um.deleted_on IS NULL
        """,
        nativeQuery = true
    )
    Page<Object[]> findUnionMembersByProjectIdNative(@Param("projectId") Long projectId, Pageable pageable);

    /**
     * Find all union members (heads and regular) using native SQL with pagination and sorting.
     */@Query(
        value = """
        SELECT um.id, um.first_name, um.last_name, um.email, um.phone_number,
               um.created_by, um.created_date, um.last_modified_by, um.last_modified_date,
               um.deleted_on, um.is_union_head, um.project_id,
               p.name as project_name, p.description as project_description
        FROM union_member um
        LEFT JOIN project p ON um.project_id = p.id
        WHERE um.deleted_on IS NULL
        ORDER BY
        CASE WHEN :sortBy = 'firstName' AND :sortDirection = 'ASC' THEN um.first_name END ASC,
        CASE WHEN :sortBy = 'firstName' AND :sortDirection = 'DESC' THEN um.first_name END DESC,
        CASE WHEN :sortBy = 'lastName' AND :sortDirection = 'ASC' THEN um.last_name END ASC,
        CASE WHEN :sortBy = 'lastName' AND :sortDirection = 'DESC' THEN um.last_name END DESC,
        CASE WHEN :sortBy = 'email' AND :sortDirection = 'ASC' THEN um.email END ASC,
        CASE WHEN :sortBy = 'email' AND :sortDirection = 'DESC' THEN um.email END DESC,
        CASE WHEN :sortBy = 'createdDate' AND :sortDirection = 'ASC' THEN um.created_date END ASC,
        CASE WHEN :sortBy = 'createdDate' AND :sortDirection = 'DESC' THEN um.created_date END DESC,
        CASE WHEN :sortBy = 'isUnionHead' AND :sortDirection = 'ASC' THEN um.is_union_head END ASC,
        CASE WHEN :sortBy = 'isUnionHead' AND :sortDirection = 'DESC' THEN um.is_union_head END DESC,
        um.created_date DESC
        """,
        countQuery = """
        SELECT COUNT(*)
        FROM union_member um
        WHERE um.deleted_on IS NULL
        """,
        nativeQuery = true
    )
    Page<Object[]> findAllUnionMembersNative(
        @Param("sortBy") String sortBy,
        @Param("sortDirection") String sortDirection,
        Pageable pageable
    );

    /**
     * Check if a project is owned by a specific seller.
     */
    @Query(
        value = """
        SELECT COUNT(*) > 0
        FROM project p
        WHERE p.id = :projectId AND p.seller_id = :sellerId AND p.deleted_date IS NULL
        """,
        nativeQuery = true
    )
    boolean isProjectOwnedBySeller(@Param("projectId") Long projectId, @Param("sellerId") Long sellerId);

    /**
     * Find all union members by seller's projects using native SQL with pagination.
     */
    @Query(
        value = """
        SELECT um.id, um.first_name, um.last_name, um.email, um.phone_number,
               um.created_by, um.created_date, um.last_modified_by, um.last_modified_date,
               um.deleted_on, um.is_union_head, um.project_id,
               p.name as project_name, p.description as project_description
        FROM union_member um
        INNER JOIN project p ON um.project_id = p.id
        WHERE p.seller_id = :sellerId
        AND um.deleted_on IS NULL
        AND p.deleted_date IS NULL
        ORDER BY
            CASE WHEN :sortDirection = 'asc' THEN
                CASE
                    WHEN :sortBy = 'firstName' THEN um.first_name
                    WHEN :sortBy = 'lastName' THEN um.last_name
                    WHEN :sortBy = 'email' THEN um.email
                    WHEN :sortBy = 'phoneNumber' THEN um.phone_number
                    WHEN :sortBy = 'createdDate' THEN um.created_date::text
                    ELSE um.id::text
                END
            END ASC,
            CASE WHEN :sortDirection = 'desc' THEN
                CASE
                    WHEN :sortBy = 'firstName' THEN um.first_name
                    WHEN :sortBy = 'lastName' THEN um.last_name
                    WHEN :sortBy = 'email' THEN um.email
                    WHEN :sortBy = 'phoneNumber' THEN um.phone_number
                    WHEN :sortBy = 'createdDate' THEN um.created_date::text
                    ELSE um.id::text
                END
            END DESC
        """,
        countQuery = """
        SELECT COUNT(um.id)
        FROM union_member um
        INNER JOIN project p ON um.project_id = p.id
        WHERE p.seller_id = :sellerId
        AND um.deleted_on IS NULL
        AND p.deleted_date IS NULL
        """,
        nativeQuery = true
    )
    Page<Object[]> findAllUnionMembersNativeBySellerProjects(
        @Param("sellerId") Long sellerId,
        @Param("sortBy") String sortBy,
        @Param("sortDirection") String sortDirection,
        Pageable pageable
    );/**
     * Get the next sequence value for union_member_seq.
     */

    @Query(value = "SELECT nextval('union_member_seq')", nativeQuery = true)
    Long getNextSequenceValue();

    /**
     * Save a new union member using native SQL with auto-generated ID.
     */
    @Modifying
    @Query(
        value = """
        INSERT INTO union_member (first_name, last_name, email, phone_number, is_union_head,
                                 project_id, created_by, created_date)
        VALUES (:firstName, :lastName, :email, :phoneNumber, :isUnionHead,
                :projectId, :createdBy, :createdDate)
        """,
        nativeQuery = true
    )
    int saveUnionMemberNative(
        @Param("firstName") String firstName,
        @Param("lastName") String lastName,
        @Param("email") String email,
        @Param("phoneNumber") String phoneNumber,
        @Param("isUnionHead") boolean isUnionHead,
        @Param("projectId") Long projectId,
        @Param("createdBy") String createdBy,
        @Param("createdDate") java.time.Instant createdDate
    );/**
     * Update a union member using native SQL.
     */

    @Modifying
    @Query(
        value = """
        UPDATE union_member
        SET first_name = :firstName, last_name = :lastName, email = :email,
            phone_number = :phoneNumber, is_union_head = :isUnionHead,
            project_id = :projectId, last_modified_by = :lastModifiedBy,
            last_modified_date = :lastModifiedDate
        WHERE id = :id AND deleted_on IS NULL
        """,
        nativeQuery = true
    )
    int updateUnionMemberNative(
        @Param("id") Long id,
        @Param("firstName") String firstName,
        @Param("lastName") String lastName,
        @Param("email") String email,
        @Param("phoneNumber") String phoneNumber,
        @Param("isUnionHead") boolean isUnionHead,
        @Param("projectId") Long projectId,
        @Param("lastModifiedBy") String lastModifiedBy,
        @Param("lastModifiedDate") java.time.Instant lastModifiedDate
    );

    /**
     * Find union member by ID using native SQL.
     */
    @Query(
        value = """
        SELECT um.id, um.first_name, um.last_name, um.email, um.phone_number,
               um.created_by, um.created_date, um.last_modified_by, um.last_modified_date,
               um.deleted_on, um.is_union_head, um.project_id,
               p.name as project_name, p.description as project_description
        FROM union_member um
        LEFT JOIN project p ON um.project_id = p.id
        WHERE um.id = :id AND um.deleted_on IS NULL
        """,
        nativeQuery = true
    )
    List<Object[]> findUnionMemberByIdNativeList(@Param("id") Long id);

    /**
     * Check if a union head already exists for the given project.
     */
    @Query(
        value = """
        SELECT COUNT(*) > 0
        FROM union_member um
        WHERE um.project_id = :projectId
        AND um.is_union_head = true
        AND um.deleted_on IS NULL
        """,
        nativeQuery = true
    )
    boolean existsUnionHeadForProject(@Param("projectId") Long projectId);

    /**
     * Check if a union member already exists for the given project with same criteria.
     * Criteria: phoneNumber, projectId
     */
    @Query(
        value = """
        SELECT COUNT(*) > 0
        FROM union_member um
        WHERE um.project_id = :projectId
        AND um.phone_number = :phoneNumber
        AND um.deleted_on IS NULL
        """,
        nativeQuery = true
    )
    boolean existsUnionMemberForProject(@Param("projectId") Long projectId, @Param("phoneNumber") String phoneNumber);

    /**
     * Check if a union member already exists for the given project with same criteria, excluding a specific ID.
     * Used for updates to allow updating the same record.
     * Criteria: phoneNumber, projectId
     */
    @Query(
        value = """
        SELECT COUNT(*) > 0
        FROM union_member um
        WHERE um.project_id = :projectId
        AND um.phone_number = :phoneNumber
        AND um.id != :excludeId
        AND um.deleted_on IS NULL
        """,
        nativeQuery = true
    )
    boolean existsUnionMemberForProjectExcludingId(
        @Param("projectId") Long projectId,
        @Param("phoneNumber") String phoneNumber,
        @Param("excludeId") Long excludeId
    );

    /**
     * Find the most recently created union member by email and project (helper for native save operations).
     */@Query(
        value = """
        SELECT um.id, um.first_name, um.last_name, um.email, um.phone_number,
               um.created_by, um.created_date, um.last_modified_by, um.last_modified_date,
               um.deleted_on, um.is_union_head, um.project_id,
               p.name as project_name, p.description as project_description
        FROM union_member um
        LEFT JOIN project p ON um.project_id = p.id
        WHERE um.email = :email
        AND (:projectId IS NULL OR um.project_id = :projectId)
        AND um.deleted_on IS NULL
        ORDER BY um.created_date DESC
        LIMIT 1
        """,
        nativeQuery = true
    )
    Optional<Object[]> findLatestByEmailAndProject(@Param("email") String email, @Param("projectId") Long projectId);
}
