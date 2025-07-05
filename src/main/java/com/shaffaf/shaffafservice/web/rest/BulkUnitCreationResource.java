package com.shaffaf.shaffafservice.web.rest;

import com.shaffaf.shaffafservice.security.AuthoritiesConstants;
import com.shaffaf.shaffafservice.security.SecurityUtils;
import com.shaffaf.shaffafservice.service.BulkUnitCreationService;
import com.shaffaf.shaffafservice.service.dto.BulkUnitCreationRequestDTO;
import com.shaffaf.shaffafservice.service.dto.BulkUnitCreationResponseDTO;
import com.shaffaf.shaffafservice.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for bulk unit creation operations.
 */
@RestController
@RequestMapping("/api/bulk-unit-creation/v1")
public class BulkUnitCreationResource {

    private static final Logger LOG = LoggerFactory.getLogger(BulkUnitCreationResource.class);

    private static final String ENTITY_NAME = "bulkUnitCreation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BulkUnitCreationService bulkUnitCreationService;

    public BulkUnitCreationResource(BulkUnitCreationService bulkUnitCreationService) {
        this.bulkUnitCreationService = bulkUnitCreationService;
    }

    /**
     * {@code POST  /units} : Create units in bulk for a project.
     * Creates blocks, unit types, and units based on the provided specification.
     * Restricted to ADMIN and SELLER roles only. Sellers can only create units in their own projects.
     *
     * @param request the bulk unit creation request containing project ID and creation items
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the creation response,
     * or with status {@code 400 (Bad Request)} if validation fails or access is denied.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/units")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\") or hasAuthority(\"" + AuthoritiesConstants.SELLER + "\")")
    public ResponseEntity<BulkUnitCreationResponseDTO> createUnitsInBulk(@Valid @RequestBody BulkUnitCreationRequestDTO request)
        throws URISyntaxException {
        LOG.debug("REST request to create units in bulk for project: {}", request.getProjectId());

        if (request.getProjectId() == null) {
            throw new BadRequestAlertException("Project ID is required", ENTITY_NAME, "projectidrequired");
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestAlertException("Bulk creation items cannot be empty", ENTITY_NAME, "itemsrequired");
        }

        String currentUserLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("Current user login not found", ENTITY_NAME, "usernotfound"));

        try {
            BulkUnitCreationResponseDTO response = bulkUnitCreationService.createUnitsInBulk(request, currentUserLogin);

            return ResponseEntity.created(new URI("/api/bulk-unit-creation/v1/units/" + request.getProjectId()))
                .headers(HeaderUtil.createAlert(applicationName, response.getMessage(), ""))
                .body(response);
        } catch (IllegalArgumentException e) {
            LOG.warn("Bulk unit creation failed: {}", e.getMessage());
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "bulkcreationfailed");
        } catch (Exception e) {
            LOG.error("Unexpected error during bulk unit creation", e);
            throw new BadRequestAlertException("An unexpected error occurred during bulk unit creation", ENTITY_NAME, "internalerror");
        }
    }
}
