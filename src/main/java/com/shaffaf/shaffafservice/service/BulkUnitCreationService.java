package com.shaffaf.shaffafservice.service;

import com.shaffaf.shaffafservice.service.dto.BulkUnitCreationRequestDTO;
import com.shaffaf.shaffafservice.service.dto.BulkUnitCreationResponseDTO;
import com.shaffaf.shaffafservice.service.dto.BulkUnitInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing bulk unit creation.
 */
public interface BulkUnitCreationService {
    /**
     * Create units in bulk for a project.
     * Validates project ownership for sellers.
     *
     * @param request the bulk creation request
     * @param currentUserLogin the current user login
     * @return the bulk creation response
     */
    BulkUnitCreationResponseDTO createUnitsInBulk(BulkUnitCreationRequestDTO request, String currentUserLogin);

    /**
     * Get all units with block and unit type information for a specific project.
     * Validates project ownership for sellers.
     *
     * @param projectId the project id
     * @param pageable the pagination information
     * @param currentUserLogin the current user login
     * @return the page of unit information
     */
    Page<BulkUnitInfoDTO> getAllUnitsForProject(Long projectId, Pageable pageable, String currentUserLogin);
}
