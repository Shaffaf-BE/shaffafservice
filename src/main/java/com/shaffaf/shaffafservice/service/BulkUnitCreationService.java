package com.shaffaf.shaffafservice.service;

import com.shaffaf.shaffafservice.service.dto.BulkUnitCreationRequestDTO;
import com.shaffaf.shaffafservice.service.dto.BulkUnitCreationResponseDTO;

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
}
