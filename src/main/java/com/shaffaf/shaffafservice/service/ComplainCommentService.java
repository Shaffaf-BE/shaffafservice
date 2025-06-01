package com.shaffaf.shaffafservice.service;

import com.shaffaf.shaffafservice.service.dto.ComplainCommentDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.shaffaf.shaffafservice.domain.ComplainComment}.
 */
public interface ComplainCommentService {
    /**
     * Save a complainComment.
     *
     * @param complainCommentDTO the entity to save.
     * @return the persisted entity.
     */
    ComplainCommentDTO save(ComplainCommentDTO complainCommentDTO);

    /**
     * Updates a complainComment.
     *
     * @param complainCommentDTO the entity to update.
     * @return the persisted entity.
     */
    ComplainCommentDTO update(ComplainCommentDTO complainCommentDTO);

    /**
     * Partially updates a complainComment.
     *
     * @param complainCommentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ComplainCommentDTO> partialUpdate(ComplainCommentDTO complainCommentDTO);

    /**
     * Get all the complainComments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ComplainCommentDTO> findAll(Pageable pageable);

    /**
     * Get the "id" complainComment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ComplainCommentDTO> findOne(Long id);

    /**
     * Delete the "id" complainComment.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
