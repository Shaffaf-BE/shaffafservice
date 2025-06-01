package com.shaffaf.shaffafservice.service;

import com.shaffaf.shaffafservice.service.dto.ProjectDiscountDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.shaffaf.shaffafservice.domain.ProjectDiscount}.
 */
public interface ProjectDiscountService {
    /**
     * Save a projectDiscount.
     *
     * @param projectDiscountDTO the entity to save.
     * @return the persisted entity.
     */
    ProjectDiscountDTO save(ProjectDiscountDTO projectDiscountDTO);

    /**
     * Updates a projectDiscount.
     *
     * @param projectDiscountDTO the entity to update.
     * @return the persisted entity.
     */
    ProjectDiscountDTO update(ProjectDiscountDTO projectDiscountDTO);

    /**
     * Partially updates a projectDiscount.
     *
     * @param projectDiscountDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProjectDiscountDTO> partialUpdate(ProjectDiscountDTO projectDiscountDTO);

    /**
     * Get all the projectDiscounts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProjectDiscountDTO> findAll(Pageable pageable);

    /**
     * Get the "id" projectDiscount.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProjectDiscountDTO> findOne(Long id);

    /**
     * Delete the "id" projectDiscount.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
