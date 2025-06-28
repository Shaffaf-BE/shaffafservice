package com.shaffaf.shaffafservice.web.rest;

import com.shaffaf.shaffafservice.domain.Seller;
import com.shaffaf.shaffafservice.repository.SellerRepository;
import com.shaffaf.shaffafservice.security.AuthoritiesConstants;
import com.shaffaf.shaffafservice.security.SecurityUtils;
import com.shaffaf.shaffafservice.service.ProjectService;
import com.shaffaf.shaffafservice.service.dto.ProjectDTO;
import com.shaffaf.shaffafservice.service.dto.SellerDTO;
import com.shaffaf.shaffafservice.util.PhoneNumberUtil;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;

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

    private final SellerRepository sellerRepository;

    public ProjectResource(ProjectService projectService, SellerRepository sellerRepository) {
        this.projectService = projectService;
        this.sellerRepository = sellerRepository;
    }

    /**
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
        LOG.debug("REST request to securely create Project by seller: {}", projectDTO);

        // Check for ID - a new entity shouldn't have one
        if (projectDTO.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new project cannot already have an ID");
        }

        // Get current authenticated username (mobile number) for seller identification
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
        }

        // Find seller by mobile number (using normalized phone number)
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

        Seller authenticatedSeller = sellerOptional.orElseThrow(() ->
            new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Seller not found for mobile number: %s", normalizedPhone))
        );

        // Set the seller in the project DTO based on authenticated user
        SellerDTO sellerDTO = new SellerDTO();
        sellerDTO.setId(authenticatedSeller.getId());
        sellerDTO.setFirstName(authenticatedSeller.getFirstName());
        sellerDTO.setLastName(authenticatedSeller.getLastName());
        sellerDTO.setPhoneNumber(authenticatedSeller.getPhoneNumber());
        projectDTO.setSeller(sellerDTO);

        // Sanitize and validate input fields to prevent injection attacks
        sanitizeProjectInputs(projectDTO);

        // Apply rate limiting to prevent abuse
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

    /**
     * {@code PUT  /projects/secure/update-by-seller} : Update an existing project using native queries for better performance.
     * This endpoint is specifically designed for sellers to update their own projects.
     * Uses enhanced security, input validation and rate limiting.
     * Only the seller who created the project or an admin can update it.
     * The project ID must be provided in the request body, not in the URL.
     *
     * @param projectDTO the projectDTO to update (must include the project ID in the request body)
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectDTO,
     *         or with status {@code 400 (Bad Request)} if the project has validation errors or ID is missing,
     *         or with status {@code 401 (Unauthorized)} if seller is not found,
     *         or with status {@code 403 (Forbidden)} if seller is not the owner of the project,
     *         or with status {@code 404 (Not Found)} if project is not found,
     *         or with status {@code 429 (Too Many Requests)} if rate limit is exceeded
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/secure/update-by-seller")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.SELLER + "\") or hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<ProjectDTO> updateProjectSecure(@Valid @RequestBody ProjectDTO projectDTO) throws URISyntaxException {
        LOG.debug("REST request to securely update Project by seller: {}", projectDTO);

        // Validate that the project ID is provided in the request body
        if (projectDTO.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project ID is required in the request body to update a project");
        }
        Long projectId = projectDTO.getId();

        // Get current authenticated username (mobile number) for seller identification
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
                "Only a Seller with valid mobile number can update a Project. " +
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
                "Only a Seller with valid mobile number can update a Project. " +
                "No seller account found for mobile number: %s (normalized from: %s). " +
                "Please ensure your seller account is active and registered with this mobile number.",
                normalizedPhone,
                currentUsername
            );
            LOG.warn("Seller not found for mobile number: {} (normalized from: {})", normalizedPhone, currentUsername);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        Seller authenticatedSeller = sellerOptional.get();

        // Check if user is admin (needed for findByIdSecure call)
        boolean isAdmin = SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN);

        // Check if the project exists and get its current details
        Optional<ProjectDTO> existingProjectOptional = projectService.findByIdSecure(projectId, currentUsername, isAdmin);
        if (existingProjectOptional.isEmpty()) {
            LOG.warn("Project not found with ID: {}", projectId);
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Project not found with ID: " + projectId + ". Please verify the project ID and try again."
            );
        }

        ProjectDTO existingProject = existingProjectOptional.get();

        // Check if the authenticated seller is the owner of the project or if user is admin
        boolean isProjectOwner =
            existingProject.getSeller() != null && Objects.equals(existingProject.getSeller().getId(), authenticatedSeller.getId());

        if (!isAdmin && !isProjectOwner) {
            LOG.warn("Seller {} attempted to update project {} which they don't own", normalizedPhone, projectId);
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Access denied. Only the seller who created this project or an admin can update it. " +
                "This project belongs to a different seller."
            );
        }

        // Set the seller in the project DTO (maintain original seller)
        projectDTO.setSeller(existingProject.getSeller());

        // Sanitize and validate input fields to prevent injection attacks
        sanitizeProjectInputs(projectDTO);

        // Apply rate limiting to prevent abuse
        if (isRateLimitExceeded()) {
            LOG.warn("Rate limit exceeded for project update by seller: {} (normalized: {})", currentUsername, normalizedPhone);
            return ResponseEntity.status(429).build(); // Too Many Requests
        }

        // Update project using optimized native query
        ProjectDTO result = projectService.updateProjectNative(projectDTO, normalizedPhone);

        LOG.info(
            "Project updated successfully by seller. Original username: '{}', Normalized phone: '{}', Project ID: {}",
            currentUsername,
            normalizedPhone,
            result.getId()
        );

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /secure/:id} : Get a project by ID with security validation.
     * Only ADMIN and SELLER users can access this endpoint.
     * SELLER users can only access their own projects (validated by mobile number).
     * ADMIN users can access any project.
     *
     * @param id the id of the projectDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectDTO,
     *         or with status {@code 404 (Not Found)} if the project is not found or not accessible.
     */
    @GetMapping("/secure/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.SELLER + "\") or hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<ProjectDTO> getProjectSecure(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Project securely : {}", id);

        // Get current user information
        String currentUsername = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Current user not found. Please ensure you are properly authenticated."
                )
            );

        // Check if user is admin
        boolean isAdmin = SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN);

        LOG.debug("Attempting secure project retrieval. User: '{}', IsAdmin: {}, ProjectId: {}", currentUsername, isAdmin, id);

        try {
            Optional<ProjectDTO> projectDTO = projectService.findByIdSecure(id, currentUsername, isAdmin);
            if (projectDTO.isPresent()) {
                LOG.info("Project {} retrieved successfully by user '{}' (Admin: {})", id, currentUsername, isAdmin);
                return ResponseEntity.ok().body(projectDTO.get());
            } else {
                LOG.warn("Project {} not found or not accessible for user '{}' (Admin: {})", id, currentUsername, isAdmin);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            LOG.error("Error retrieving project {} for user '{}': {}", id, currentUsername, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * {@code GET  /secure} : Get all projects with security validation, pagination, and filtering.
     * Only ADMIN and SELLER users can access this endpoint.
     * SELLER users can only access their own projects (validated by mobile number).
     * ADMIN users can access all projects.
     *
     * @param page the page number (0-based, default: 0)
     * @param size the page size (1-100, default: 20)
     * @param nameFilter filter by project name (optional)
     * @param statusFilter filter by project status (optional)
     * @param sellerNameFilter filter by seller name (optional, admin only)
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body containing page of projects,
     *         or with status {@code 400 (Bad Request)} if validation fails.
     */
    @GetMapping("/secure")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.SELLER + "\") or hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Page<ProjectDTO>> getAllProjectsSecure(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "20") int size,
        @RequestParam(value = "nameFilter", required = false) String nameFilter,
        @RequestParam(value = "statusFilter", required = false) String statusFilter,
        @RequestParam(value = "sellerNameFilter", required = false) String sellerNameFilter
    ) {
        LOG.debug("REST request to get all projects securely. Page: {}, Size: {}", page, size);
        LOG.debug("Filters - Name: '{}', Status: '{}', SellerName: '{}'", nameFilter, statusFilter, sellerNameFilter);

        // Get current user information
        String currentUsername = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Current user not found. Please ensure you are properly authenticated."
                )
            );

        // Check if user is admin
        boolean isAdmin = SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN);

        LOG.debug("Attempting secure projects retrieval. User: '{}', IsAdmin: {}", currentUsername, isAdmin);

        // Validate input parameters
        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page number cannot be negative");
        }
        if (size <= 0 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page size must be between 1 and 100");
        }

        // For non-admin users, ignore sellerNameFilter
        if (!isAdmin && sellerNameFilter != null) {
            LOG.debug("Ignoring sellerNameFilter for non-admin user: {}", currentUsername);
            sellerNameFilter = null;
        }

        try {
            Page<ProjectDTO> projectsPage = projectService.findAllSecure(
                currentUsername,
                isAdmin,
                page,
                size,
                nameFilter,
                statusFilter,
                sellerNameFilter
            );

            LOG.info(
                "Retrieved {} projects (page {}, size {}) for user '{}' (Admin: {})",
                projectsPage.getContent().size(),
                page,
                size,
                currentUsername,
                isAdmin
            );
            LOG.info("Total projects available: {}, Total pages: {}", projectsPage.getTotalElements(), projectsPage.getTotalPages());

            // Add pagination headers
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
                ServletUriComponentsBuilder.fromCurrentRequest(),
                projectsPage
            );

            return ResponseEntity.ok().headers(headers).body(projectsPage);
        } catch (Exception e) {
            LOG.error("Error retrieving projects for user '{}': {}", currentUsername, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
