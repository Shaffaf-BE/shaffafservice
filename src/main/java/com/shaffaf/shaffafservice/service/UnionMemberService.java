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
}
