package com.shaffaf.shaffafservice.service;

import com.shaffaf.shaffafservice.service.dto.UnitTypeDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.shaffaf.shaffafservice.domain.UnitType}.
 */
public interface UnitTypeService {
    /**
     * Save a unitType.
     *
     * @param unitTypeDTO the entity to save.
     * @return the persisted entity.
     */
    UnitTypeDTO save(UnitTypeDTO unitTypeDTO);

    /**
     * Updates a unitType.
     *
     * @param unitTypeDTO the entity to update.
     * @return the persisted entity.
     */
    UnitTypeDTO update(UnitTypeDTO unitTypeDTO);

    /**
     * Partially updates a unitType.
     *
     * @param unitTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<UnitTypeDTO> partialUpdate(UnitTypeDTO unitTypeDTO);

    /**
     * Get all the unitTypes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<UnitTypeDTO> findAll(Pageable pageable);

    /**
     * Get the "id" unitType.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UnitTypeDTO> findOne(Long id);

    /**
     * Delete the "id" unitType.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
