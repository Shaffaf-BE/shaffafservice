package com.shaffaf.shaffafservice.service;

import com.shaffaf.shaffafservice.service.dto.ExpenseDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.shaffaf.shaffafservice.domain.Expense}.
 */
public interface ExpenseService {
    /**
     * Save a expense.
     *
     * @param expenseDTO the entity to save.
     * @return the persisted entity.
     */
    ExpenseDTO save(ExpenseDTO expenseDTO);

    /**
     * Updates a expense.
     *
     * @param expenseDTO the entity to update.
     * @return the persisted entity.
     */
    ExpenseDTO update(ExpenseDTO expenseDTO);

    /**
     * Partially updates a expense.
     *
     * @param expenseDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ExpenseDTO> partialUpdate(ExpenseDTO expenseDTO);

    /**
     * Get all the expenses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ExpenseDTO> findAll(Pageable pageable);

    /**
     * Get the "id" expense.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ExpenseDTO> findOne(Long id);

    /**
     * Delete the "id" expense.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
