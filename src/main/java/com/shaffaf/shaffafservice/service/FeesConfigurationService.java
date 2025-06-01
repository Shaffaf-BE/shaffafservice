package com.shaffaf.shaffafservice.service;

import com.shaffaf.shaffafservice.service.dto.FeesConfigurationDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.shaffaf.shaffafservice.domain.FeesConfiguration}.
 */
public interface FeesConfigurationService {
    /**
     * Save a feesConfiguration.
     *
     * @param feesConfigurationDTO the entity to save.
     * @return the persisted entity.
     */
    FeesConfigurationDTO save(FeesConfigurationDTO feesConfigurationDTO);

    /**
     * Updates a feesConfiguration.
     *
     * @param feesConfigurationDTO the entity to update.
     * @return the persisted entity.
     */
    FeesConfigurationDTO update(FeesConfigurationDTO feesConfigurationDTO);

    /**
     * Partially updates a feesConfiguration.
     *
     * @param feesConfigurationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FeesConfigurationDTO> partialUpdate(FeesConfigurationDTO feesConfigurationDTO);

    /**
     * Get all the feesConfigurations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FeesConfigurationDTO> findAll(Pageable pageable);

    /**
     * Get the "id" feesConfiguration.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FeesConfigurationDTO> findOne(Long id);

    /**
     * Delete the "id" feesConfiguration.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
