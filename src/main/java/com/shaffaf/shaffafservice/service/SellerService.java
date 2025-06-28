package com.shaffaf.shaffafservice.service;

import com.shaffaf.shaffafservice.service.dto.DashboardDataDTO;
import com.shaffaf.shaffafservice.service.dto.SellerDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.shaffaf.shaffafservice.domain.Seller}.
 */
public interface SellerService {
    /**
     * Partially updates a seller.
     *
     * @param sellerDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SellerDTO> partialUpdate(SellerDTO sellerDTO);

    /**
     * Delete the "id" seller.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Get all the sellers using optimized native SQL query.
     *
     * @param searchTerm optional search term to filter results
     * @param pageable the pagination information.
     * @return the page of entities.
     */
    Page<SellerDTO> findAllOptimized(String searchTerm, Pageable pageable);

    /**
     * Get the "id" seller using optimized and secure native query.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<SellerDTO> findOneOptimized(Long id);

    /**
     * Save a seller using native SQL query.
     *
     * @param sellerDTO the entity to save
     * @return the persisted entity
     */
    SellerDTO saveWithNativeQuery(SellerDTO sellerDTO);

    /**
     * Get dashboard data with financial aggregations and transaction details.
     *
     * @param pageable the pagination information for transaction details
     * @param sortBy the field to sort by (sellerName, amount, transactionDate)
     * @param sortDirection the sort direction (ASC or DESC)
     * @return the dashboard data with aggregated information
     */
    DashboardDataDTO getDashboardData(Pageable pageable, String sortBy, String sortDirection);

    /**
     * Update a seller using native SQL query.
     *
     * @param sellerDTO the entity to update
     * @return the persisted entity
     */
    SellerDTO updateWithNativeQuery(SellerDTO sellerDTO);
}
