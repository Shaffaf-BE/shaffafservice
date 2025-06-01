package com.shaffaf.shaffafservice.service;

import com.shaffaf.shaffafservice.service.dto.FeesCollectionDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.shaffaf.shaffafservice.domain.FeesCollection}.
 */
public interface FeesCollectionService {
    /**
     * Save a feesCollection.
     *
     * @param feesCollectionDTO the entity to save.
     * @return the persisted entity.
     */
    FeesCollectionDTO save(FeesCollectionDTO feesCollectionDTO);

    /**
     * Updates a feesCollection.
     *
     * @param feesCollectionDTO the entity to update.
     * @return the persisted entity.
     */
    FeesCollectionDTO update(FeesCollectionDTO feesCollectionDTO);

    /**
     * Partially updates a feesCollection.
     *
     * @param feesCollectionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FeesCollectionDTO> partialUpdate(FeesCollectionDTO feesCollectionDTO);

    /**
     * Get all the feesCollections.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FeesCollectionDTO> findAll(Pageable pageable);

    /**
     * Get the "id" feesCollection.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FeesCollectionDTO> findOne(Long id);

    /**
     * Delete the "id" feesCollection.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
