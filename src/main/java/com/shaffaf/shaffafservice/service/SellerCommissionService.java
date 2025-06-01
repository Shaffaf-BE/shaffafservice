package com.shaffaf.shaffafservice.service;

import com.shaffaf.shaffafservice.service.dto.SellerCommissionDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.shaffaf.shaffafservice.domain.SellerCommission}.
 */
public interface SellerCommissionService {
    /**
     * Save a sellerCommission.
     *
     * @param sellerCommissionDTO the entity to save.
     * @return the persisted entity.
     */
    SellerCommissionDTO save(SellerCommissionDTO sellerCommissionDTO);

    /**
     * Updates a sellerCommission.
     *
     * @param sellerCommissionDTO the entity to update.
     * @return the persisted entity.
     */
    SellerCommissionDTO update(SellerCommissionDTO sellerCommissionDTO);

    /**
     * Partially updates a sellerCommission.
     *
     * @param sellerCommissionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SellerCommissionDTO> partialUpdate(SellerCommissionDTO sellerCommissionDTO);

    /**
     * Get all the sellerCommissions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SellerCommissionDTO> findAll(Pageable pageable);

    /**
     * Get the "id" sellerCommission.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SellerCommissionDTO> findOne(Long id);

    /**
     * Delete the "id" sellerCommission.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
