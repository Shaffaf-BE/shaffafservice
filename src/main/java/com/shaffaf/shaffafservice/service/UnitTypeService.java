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
     * Save a unit type.
     *
     * @param unitTypeDTO the entity to save.
     * @return the persisted entity.
     */
    UnitTypeDTO save(UnitTypeDTO unitTypeDTO);

    /**
     * Updates a unit type.
     *
     * @param unitTypeDTO the entity to update.
     * @return the persisted entity.
     */
    UnitTypeDTO update(UnitTypeDTO unitTypeDTO);

    /**
     * Get the "id" unit type.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UnitTypeDTO> findOne(Long id);

    /**
     * Get all unit types by project ID.
     *
     * @param projectId the ID of the project.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<UnitTypeDTO> findAllByProjectId(Long projectId, Pageable pageable);
}
