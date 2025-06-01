package com.shaffaf.shaffafservice.service;

import com.shaffaf.shaffafservice.service.dto.ExpenseTypeDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.shaffaf.shaffafservice.domain.ExpenseType}.
 */
public interface ExpenseTypeService {
    /**
     * Save a expenseType.
     *
     * @param expenseTypeDTO the entity to save.
     * @return the persisted entity.
     */
    ExpenseTypeDTO save(ExpenseTypeDTO expenseTypeDTO);

    /**
     * Updates a expenseType.
     *
     * @param expenseTypeDTO the entity to update.
     * @return the persisted entity.
     */
    ExpenseTypeDTO update(ExpenseTypeDTO expenseTypeDTO);

    /**
     * Partially updates a expenseType.
     *
     * @param expenseTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ExpenseTypeDTO> partialUpdate(ExpenseTypeDTO expenseTypeDTO);

    /**
     * Get all the expenseTypes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ExpenseTypeDTO> findAll(Pageable pageable);

    /**
     * Get the "id" expenseType.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ExpenseTypeDTO> findOne(Long id);

    /**
     * Delete the "id" expenseType.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
