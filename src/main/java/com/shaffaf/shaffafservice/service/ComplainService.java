package com.shaffaf.shaffafservice.service;

import com.shaffaf.shaffafservice.service.dto.ComplainDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.shaffaf.shaffafservice.domain.Complain}.
 */
public interface ComplainService {
    /**
     * Save a complain.
     *
     * @param complainDTO the entity to save.
     * @return the persisted entity.
     */
    ComplainDTO save(ComplainDTO complainDTO);

    /**
     * Updates a complain.
     *
     * @param complainDTO the entity to update.
     * @return the persisted entity.
     */
    ComplainDTO update(ComplainDTO complainDTO);

    /**
     * Partially updates a complain.
     *
     * @param complainDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ComplainDTO> partialUpdate(ComplainDTO complainDTO);

    /**
     * Get all the complains.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ComplainDTO> findAll(Pageable pageable);

    /**
     * Get the "id" complain.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ComplainDTO> findOne(Long id);

    /**
     * Delete the "id" complain.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
