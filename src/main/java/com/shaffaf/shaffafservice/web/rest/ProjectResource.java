package com.shaffaf.shaffafservice.web.rest;

import com.shaffaf.shaffafservice.domain.Seller;
import com.shaffaf.shaffafservice.repository.ProjectRepository;
import com.shaffaf.shaffafservice.repository.SellerRepository;
import com.shaffaf.shaffafservice.security.AuthoritiesConstants;
import com.shaffaf.shaffafservice.security.SecurityUtils;
import com.shaffaf.shaffafservice.service.ProjectService;
import com.shaffaf.shaffafservice.service.dto.ProjectDTO;
import com.shaffaf.shaffafservice.service.dto.SellerDTO;
import com.shaffaf.shaffafservice.util.PhoneNumberUtil;
import com.shaffaf.shaffafservice.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.shaffaf.shaffafservice.domain.Project}.
 */
@RestController
@RequestMapping("/api/projects/v1")
public class ProjectResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectResource.class);

    private static final String ENTITY_NAME = "shaffafserviceProject";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectService projectService;

    private final ProjectRepository projectRepository;

    private final SellerRepository sellerRepository;

    public ProjectResource(ProjectService projectService, ProjectRepository projectRepository, SellerRepository sellerRepository) {
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        this.sellerRepository = sellerRepository;
    }

    /**
     * {@code POST  /projects} : Create a new project.
     *
     * @param projectDTO the projectDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectDTO, or with status {@code 400 (Bad Request)} if the project has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO projectDTO) throws URISyntaxException {
        LOG.debug("REST request to save Project : {}", projectDTO);
        if (projectDTO.getId() != null) {
            throw new BadRequestAlertException("A new project cannot already have an ID", ENTITY_NAME, "idexists");
        }
        projectDTO = projectService.save(projectDTO);
        return ResponseEntity.created(new URI("/api/projects/" + projectDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, projectDTO.getId().toString()))
            .body(projectDTO);
    }

    /**
     * {@code PUT  /projects/:id} : Updates an existing project.
     *
     * @param id the id of the projectDTO to save.
     * @param projectDTO the projectDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectDTO,
     * or with status {@code 400 (Bad Request)} if the projectDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProjectDTO projectDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Project : {}, {}", id, projectDTO);
        if (projectDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        projectDTO = projectService.update(projectDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectDTO.getId().toString()))
            .body(projectDTO);
    }

    /**
     * {@code PATCH  /projects/:id} : Partial updates given fields of an existing project, field will ignore if it is null
     *
     * @param id the id of the projectDTO to save.
     * @param projectDTO the projectDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectDTO,
     * or with status {@code 400 (Bad Request)} if the projectDTO is not valid,
     * or with status {@code 404 (Not Found)} if the projectDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the projectDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProjectDTO> partialUpdateProject(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProjectDTO projectDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Project partially : {}, {}", id, projectDTO);
        if (projectDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProjectDTO> result = projectService.partialUpdate(projectDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /projects} : get all the projects.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projects in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ProjectDTO>> getAllProjects(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Projects");
        Page<ProjectDTO> page = projectService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /projects/:id} : get the "id" project.
     *
     * @param id the id of the projectDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Project : {}", id);
        Optional<ProjectDTO> projectDTO = projectService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projectDTO);
    }

    /**
     * {@code DELETE  /projects/:id} : delete the "id" project.
     *
     * @param id the id of the projectDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Project : {}", id);
        projectService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }/**
     * {@code POST  /projects/secure/create-by-seller} : Create a new project using native queries for better performance.
     * This endpoint is specifically designed for sellers to create projects.
     * Uses enhanced security, input validation and rate limiting.
     * The seller is automatically identified by the authenticated user's mobile number.
     *
     * @param projectDTO the projectDTO to create (seller field will be automatically set)
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectDTO,
     *         or with status {@code 400 (Bad Request)} if the project has validation errors,
     *         or with status {@code 401 (Unauthorized)} if seller is not found,
     *         or with status {@code 429 (Too Many Requests)} if rate limit is exceeded
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */

    @PostMapping("/secure/create-by-seller")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.SELLER + "\") or hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<ProjectDTO> createProjectSecure(@Valid @RequestBody ProjectDTO projectDTO) throws URISyntaxException {
        LOG.debug("REST request to securely create Project by seller: {}", projectDTO); // Check for ID - a new entity shouldn't have one
        if (projectDTO.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new project cannot already have an ID");
        } // Get current authenticated username (mobile number) for seller identification
        String currentUsername = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Current user not found. Please ensure you are properly authenticated."
                )
            );
        LOG.debug("Attempting to validate authenticated user: {}", currentUsername);

        // Normalize the phone number to handle various formats (local, international, etc.)
        String normalizedPhone = PhoneNumberUtil.normalize(currentUsername);
        LOG.debug("Original username: '{}', Normalized phone: '{}'", currentUsername, normalizedPhone);
        LOG.debug("Phone number validation result: {}", PhoneNumberUtil.isValidPakistaniMobile(normalizedPhone));

        // Validate mobile number format for security
        if (!PhoneNumberUtil.isValidPakistaniMobile(normalizedPhone)) {
            String errorMessage = String.format(
                "Only a Seller with valid mobile number can create a Project. " +
                "Authentication failed - invalid phone number format. Expected format: %s, but received: %s. " +
                "Please ensure your seller account is registered with a valid Pakistani mobile number.",
                PhoneNumberUtil.EXAMPLE_PHONE_NUMBER,
                currentUsername
            );
            LOG.warn("Phone validation failed for user: '{}' (normalized: '{}')", currentUsername, normalizedPhone);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        } // Find seller by mobile number (using normalized phone number)
        Optional<Seller> sellerOptional = sellerRepository.findByPhoneNumber(normalizedPhone);
        if (sellerOptional.isEmpty()) {
            String errorMessage = String.format(
                "Only a Seller with valid mobile number can create a Project. " +
                "No seller account found for mobile number: %s (normalized from: %s). " +
                "Please ensure your seller account is active and registered with this mobile number.",
                normalizedPhone,
                currentUsername
            );
            LOG.warn("Seller not found for mobile number: {} (normalized from: {})", normalizedPhone, currentUsername);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        Seller authenticatedSeller = sellerOptional.get();

        // Set the seller in the project DTO based on authenticated user
        SellerDTO sellerDTO = new SellerDTO();
        sellerDTO.setId(authenticatedSeller.getId());
        sellerDTO.setFirstName(authenticatedSeller.getFirstName());
        sellerDTO.setLastName(authenticatedSeller.getLastName());
        sellerDTO.setPhoneNumber(authenticatedSeller.getPhoneNumber());
        projectDTO.setSeller(sellerDTO);

        // Sanitize and validate input fields to prevent injection attacks
        sanitizeProjectInputs(projectDTO); // Apply rate limiting to prevent abuse
        if (isRateLimitExceeded()) {
            LOG.warn("Rate limit exceeded for project creation by seller: {} (normalized: {})", currentUsername, normalizedPhone);
            return ResponseEntity.status(429).build(); // Too Many Requests
        }

        // Create project using optimized native query
        ProjectDTO result = projectService.createProjectNative(projectDTO, normalizedPhone);

        LOG.info(
            "Project created successfully by seller: {} (normalized: {}) with ID: {}",
            currentUsername,
            normalizedPhone,
            result.getId()
        );

        return ResponseEntity.created(new URI("/api/projects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }/**
     * Sanitizes and validates project input fields to prevent injection attacks.
     *
     * @param projectDTO the project data to sanitize
     * @throws ResponseStatusException if validation fails
     */

    private void sanitizeProjectInputs(ProjectDTO projectDTO) {
        // Sanitize string fields to prevent XSS and injection attacks
        if (projectDTO.getName() != null) {
            validateStringField(projectDTO.getName(), "name", 50);
            // Remove any potentially dangerous HTML/script tags
            projectDTO.setName(sanitizeStringInput(projectDTO.getName()));
        }

        if (projectDTO.getDescription() != null) {
            validateStringField(projectDTO.getDescription(), "description", 1000);
            projectDTO.setDescription(sanitizeStringInput(projectDTO.getDescription()));
        }

        if (projectDTO.getUnionHeadName() != null) {
            validateStringField(projectDTO.getUnionHeadName(), "unionHeadName", 100);
            projectDTO.setUnionHeadName(sanitizeStringInput(projectDTO.getUnionHeadName()));
        } // Validate phone number format
        if (projectDTO.getUnionHeadMobileNumber() != null) {
            String phoneNumber = projectDTO.getUnionHeadMobileNumber();
            if (!PhoneNumberUtil.isValidPakistaniMobile(phoneNumber)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, PhoneNumberUtil.INVALID_PHONE_ERROR_MESSAGE);
            }
        } // Validate numeric fields
        if (projectDTO.getFeesPerUnitPerMonth() != null) {
            BigDecimal fees = projectDTO.getFeesPerUnitPerMonth();
            if (fees.compareTo(BigDecimal.ZERO) < 0 || fees.compareTo(new BigDecimal("1000000")) > 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fees per unit per month must be between 0 and 1,000,000");
            }
        }

        if (projectDTO.getNumberOfUnits() != null) {
            Integer units = projectDTO.getNumberOfUnits();
            if (units <= 0 || units > 10000) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Number of units must be between 1 and 10,000");
            }
        } // Validate dates
        if (projectDTO.getStartDate() != null && projectDTO.getEndDate() != null) {
            if (projectDTO.getEndDate().isBefore(projectDTO.getStartDate())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date cannot be before start date");
            }
        }
        // Note: Seller validation is not needed here as it's automatically set by the system
        // based on the authenticated user's mobile number
    }

    /**
     * Validates a string field length and content.
     *
     * @param value the string to validate
     * @param fieldName the name of the field (for error messages)
     * @param maxLength the maximum allowed length
     * @throws ResponseStatusException if validation fails
     */private void validateStringField(String value, String fieldName, int maxLength) {
        if (value.length() > maxLength) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " cannot exceed " + maxLength + " characters");
        }
    }

    /**
     * Sanitizes a string input to prevent XSS attacks.
     *
     * @param input the input string to sanitize
     * @return the sanitized string
     */
    private String sanitizeStringInput(String input) {
        if (input == null) {
            return null;
        }

        // Replace potentially dangerous HTML/script tags
        String sanitized = input
            .replaceAll("<script[^>]*>.*?</script>", "")
            .replaceAll("<.*?javascript:.*?>", "")
            .replaceAll("<.*?\\s+on.*?>", "");

        // Encode HTML entities to prevent XSS
        return sanitized
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("/", "&#x2F;");
    }/**
     * Simple rate limiting implementation.
     * In production, replace with a proper distributed rate limiter like Redis-based implementation.
     *
     * @return true if rate limit is exceeded, false otherwise
     */

    private boolean isRateLimitExceeded() {
        // This is a placeholder for a real rate limiting implementation
        // In production, use a distributed rate limiter with Redis or similar
        return false;
    }
}
