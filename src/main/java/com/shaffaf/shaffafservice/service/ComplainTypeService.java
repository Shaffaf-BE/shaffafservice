package com.shaffaf.shaffafservice.service;

import com.shaffaf.shaffafservice.service.dto.ComplainTypeDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.shaffaf.shaffafservice.domain.ComplainType}.
 */
public interface ComplainTypeService {
    /**
     * Save a complainType.
     *
     * @param complainTypeDTO the entity to save.
     * @return the persisted entity.
     */
    ComplainTypeDTO save(ComplainTypeDTO complainTypeDTO);

    /**
     * Updates a complainType.
     *
     * @param complainTypeDTO the entity to update.
     * @return the persisted entity.
     */
    ComplainTypeDTO update(ComplainTypeDTO complainTypeDTO);

    /**
     * Partially updates a complainType.
     *
     * @param complainTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ComplainTypeDTO> partialUpdate(ComplainTypeDTO complainTypeDTO);

    /**
     * Get all the complainTypes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ComplainTypeDTO> findAll(Pageable pageable);

    /**
     * Get the "id" complainType.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ComplainTypeDTO> findOne(Long id);

    /**
     * Delete the "id" complainType.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
