package com.shaffaf.shaffafservice.service;

import com.shaffaf.shaffafservice.service.dto.UnionMemberDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.shaffaf.shaffafservice.domain.UnionMember}.
 */
public interface UnionMemberService {
    /**
     * Save a unionMember.
     *
     * @param unionMemberDTO the entity to save.
     * @return the persisted entity.
     */
    UnionMemberDTO save(UnionMemberDTO unionMemberDTO);

    /**
     * Updates a unionMember.
     *
     * @param unionMemberDTO the entity to update.
     * @return the persisted entity.
     */
    UnionMemberDTO update(UnionMemberDTO unionMemberDTO);

    /**
     * Partially updates a unionMember.
     *
     * @param unionMemberDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<UnionMemberDTO> partialUpdate(UnionMemberDTO unionMemberDTO);

    /**
     * Get all the unionMembers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<UnionMemberDTO> findAll(Pageable pageable);

    /**
     * Get the "id" unionMember.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UnionMemberDTO> findOne(Long id);

    /**
     * Delete the "id" unionMember.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    // Native SQL-based methods for secure operations

    /**
     * Save a new union member using native SQL.
     *
     * @param unionMemberDTO the entity to save
     * @param currentUserLogin the current user login
     * @return the saved entity
     */
    UnionMemberDTO saveUnionMemberNative(UnionMemberDTO unionMemberDTO, String currentUserLogin);

    /**
     * Save a new union head using native SQL, ensuring only one head per project.
     *
     * @param unionMemberDTO the entity to save (with isUnionHead = true)
     * @param currentUserLogin the current user login
     * @return the saved entity
     * @throws IllegalStateException if a union head already exists for the project
     */
    UnionMemberDTO saveUnionHeadNative(UnionMemberDTO unionMemberDTO, String currentUserLogin);

    /**
     * Update a union member using native SQL.
     *
     * @param id the id of the entity to update
     * @param unionMemberDTO the entity data to update
     * @param currentUserLogin the current user login
     * @return the updated entity
     */
    Optional<UnionMemberDTO> updateUnionMemberNative(Long id, UnionMemberDTO unionMemberDTO, String currentUserLogin);

    /**
     * Find a union member by ID using native SQL.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<UnionMemberDTO> findOneNative(Long id);

    /**
     * Get all union members for a specific project using native SQL with pagination.
     *
     * @param projectId the project ID
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<UnionMemberDTO> findUnionMembersByProjectNative(Long projectId, Pageable pageable);

    /**
     * Get all union members using native SQL with pagination and sorting.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<UnionMemberDTO> findAllUnionMembersNative(Pageable pageable);
}
