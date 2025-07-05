package com.shaffaf.shaffafservice.service;

import com.shaffaf.shaffafservice.service.dto.DashboardDataDTO;
import com.shaffaf.shaffafservice.service.dto.SellerDTO;
import com.shaffaf.shaffafservice.service.dto.SellerPersonalDashboardDTO;
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

    /**
     * Get seller sales dashboard with aggregated sales data for each unique seller.
     *
     * @param pageable the pagination information for seller sales data
     * @param sortBy the field to sort by (sellerName, totalSalesAmount, totalProjects, totalUnits)
     * @param sortDirection the sort direction (ASC or DESC)
     * @return the seller sales dashboard with aggregated information
     */
    com.shaffaf.shaffafservice.service.dto.SellerSalesDashboardDTO getSellerSalesDashboard(
        Pageable pageable,
        String sortBy,
        String sortDirection
    );

    /**
     * Get seller's personal dashboard with aggregated data and their projects.
     *
     * @param sellerId the ID of the seller
     * @param pageable the pagination information for projects
     * @param sortBy the field to sort projects by
     * @param sortDirection the sort direction (ASC or DESC)
     * @return the seller's personal dashboard data
     */
    SellerPersonalDashboardDTO getSellerPersonalDashboard(Long sellerId, Pageable pageable, String sortBy, String sortDirection);
}
