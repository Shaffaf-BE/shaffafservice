package com.shaffaf.shaffafservice.service;

import com.shaffaf.shaffafservice.service.dto.ComplainStatusDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.shaffaf.shaffafservice.domain.ComplainStatus}.
 */
public interface ComplainStatusService {
    /**
     * Save a complainStatus.
     *
     * @param complainStatusDTO the entity to save.
     * @return the persisted entity.
     */
    ComplainStatusDTO save(ComplainStatusDTO complainStatusDTO);

    /**
     * Updates a complainStatus.
     *
     * @param complainStatusDTO the entity to update.
     * @return the persisted entity.
     */
    ComplainStatusDTO update(ComplainStatusDTO complainStatusDTO);

    /**
     * Partially updates a complainStatus.
     *
     * @param complainStatusDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ComplainStatusDTO> partialUpdate(ComplainStatusDTO complainStatusDTO);

    /**
     * Get all the complainStatuses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ComplainStatusDTO> findAll(Pageable pageable);

    /**
     * Get the "id" complainStatus.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ComplainStatusDTO> findOne(Long id);

    /**
     * Delete the "id" complainStatus.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
