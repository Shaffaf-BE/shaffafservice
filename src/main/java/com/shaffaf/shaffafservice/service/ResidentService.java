package com.shaffaf.shaffafservice.service;

import com.shaffaf.shaffafservice.service.dto.ResidentDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.shaffaf.shaffafservice.domain.Resident}.
 */
public interface ResidentService {
    /**
     * Save a resident.
     *
     * @param residentDTO the entity to save.
     * @return the persisted entity.
     */
    ResidentDTO save(ResidentDTO residentDTO);

    /**
     * Updates a resident.
     *
     * @param residentDTO the entity to update.
     * @return the persisted entity.
     */
    ResidentDTO update(ResidentDTO residentDTO);

    /**
     * Partially updates a resident.
     *
     * @param residentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ResidentDTO> partialUpdate(ResidentDTO residentDTO);

    /**
     * Get all the residents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ResidentDTO> findAll(Pageable pageable);

    /**
     * Get the "id" resident.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ResidentDTO> findOne(Long id);

    /**
     * Delete the "id" resident.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
