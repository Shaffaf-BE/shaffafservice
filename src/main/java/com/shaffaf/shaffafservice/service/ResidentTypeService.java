package com.shaffaf.shaffafservice.service;

import com.shaffaf.shaffafservice.service.dto.ResidentTypeDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.shaffaf.shaffafservice.domain.ResidentType}.
 */
public interface ResidentTypeService {
    /**
     * Save a residentType.
     *
     * @param residentTypeDTO the entity to save.
     * @return the persisted entity.
     */
    ResidentTypeDTO save(ResidentTypeDTO residentTypeDTO);

    /**
     * Updates a residentType.
     *
     * @param residentTypeDTO the entity to update.
     * @return the persisted entity.
     */
    ResidentTypeDTO update(ResidentTypeDTO residentTypeDTO);

    /**
     * Partially updates a residentType.
     *
     * @param residentTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ResidentTypeDTO> partialUpdate(ResidentTypeDTO residentTypeDTO);

    /**
     * Get all the residentTypes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ResidentTypeDTO> findAll(Pageable pageable);

    /**
     * Get the "id" residentType.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ResidentTypeDTO> findOne(Long id);

    /**
     * Delete the "id" residentType.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
