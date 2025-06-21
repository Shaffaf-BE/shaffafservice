package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.Project;
import com.shaffaf.shaffafservice.domain.enumeration.Status;
import com.shaffaf.shaffafservice.repository.ProjectRepository;
import com.shaffaf.shaffafservice.service.ProjectService;
import com.shaffaf.shaffafservice.service.dto.ProjectDTO;
import com.shaffaf.shaffafservice.service.mapper.ProjectMapper;
import com.shaffaf.shaffafservice.web.rest.errors.BadRequestAlertException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shaffaf.shaffafservice.domain.Project}.
 */
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(ProjectRepository projectRepository, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
    }

    @Override
    public ProjectDTO save(ProjectDTO projectDTO) {
        LOG.debug("Request to save Project : {}", projectDTO);
        Project project = projectMapper.toEntity(projectDTO);
        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Override
    public ProjectDTO update(ProjectDTO projectDTO) {
        LOG.debug("Request to update Project : {}", projectDTO);
        Project project = projectMapper.toEntity(projectDTO);
        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Override
    public Optional<ProjectDTO> partialUpdate(ProjectDTO projectDTO) {
        LOG.debug("Request to partially update Project : {}", projectDTO);

        return projectRepository
            .findById(projectDTO.getId())
            .map(existingProject -> {
                projectMapper.partialUpdate(existingProject, projectDTO);

                return existingProject;
            })
            .map(projectRepository::save)
            .map(projectMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Projects");
        return projectRepository.findAll(pageable).map(projectMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProjectDTO> findOne(Long id) {
        LOG.debug("Request to get Project : {}", id);
        return projectRepository.findById(id).map(projectMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Project : {}", id);
        projectRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ProjectDTO createProjectNative(ProjectDTO projectDTO, String username) {
        LOG.debug("Request to create Project using native query : {}", projectDTO);

        // Validate input
        if (projectDTO.getId() != null) {
            throw new BadRequestAlertException("A new project cannot already have an ID", "project", "idexists");
        }

        if (projectDTO.getSeller() == null || projectDTO.getSeller().getId() == null) {
            throw new BadRequestAlertException("A seller is required to create a project", "project", "sellerrequired");
        }

        // Verify that the seller exists
        Long sellerId = projectDTO.getSeller().getId();
        if (!projectRepository.existsSeller(sellerId)) {
            throw new BadRequestAlertException("Seller not found", "project", "sellernotfound");
        }

        // Validate required fields
        if (projectDTO.getName() == null || projectDTO.getName().trim().isEmpty()) {
            throw new BadRequestAlertException("Name is required", "project", "namerequired");
        }

        if (projectDTO.getStartDate() == null) {
            throw new BadRequestAlertException("Start date is required", "project", "startdaterequired");
        }

        if (projectDTO.getUnionHeadName() == null || projectDTO.getUnionHeadName().trim().isEmpty()) {
            throw new BadRequestAlertException("Union head name is required", "project", "unionheadnamerequired");
        }

        if (projectDTO.getUnionHeadMobileNumber() == null || projectDTO.getUnionHeadMobileNumber().trim().isEmpty()) {
            throw new BadRequestAlertException("Union head mobile number is required", "project", "unionheadmobilenumberrequired");
        }

        if (projectDTO.getNumberOfUnits() == null || projectDTO.getNumberOfUnits() <= 0) {
            throw new BadRequestAlertException("Number of units must be greater than zero", "project", "numberofunitsrequired");
        }

        // Set default status if not provided
        String status = projectDTO.getStatus() != null ? projectDTO.getStatus().toString() : Status.ACTIVE.toString();

        // Execute native query
        Long projectId = projectRepository.createProjectNative(
            projectDTO.getName(),
            projectDTO.getDescription(),
            projectDTO.getStartDate(),
            projectDTO.getEndDate(),
            status,
            projectDTO.getFeesPerUnitPerMonth(),
            projectDTO.getUnionHeadName(),
            projectDTO.getUnionHeadMobileNumber(),
            projectDTO.getNumberOfUnits(),
            sellerId,
            username
        );

        // Retrieve the created project for the response
        return projectRepository
            .findById(projectId)
            .map(projectMapper::toDto)
            .orElseThrow(() -> new BadRequestAlertException("Error creating project", "project", "creationfailed"));
    }

    @Override
    @Transactional
    public ProjectDTO updateProjectNative(ProjectDTO projectDTO, String username) {
        LOG.debug("Request to update Project using native query : {}", projectDTO);

        // Validate input
        if (projectDTO.getId() == null) {
            throw new BadRequestAlertException("Project ID is required for update", "project", "idnull");
        }

        if (projectDTO.getSeller() == null || projectDTO.getSeller().getId() == null) {
            throw new BadRequestAlertException("A seller is required to update a project", "project", "sellerrequired");
        }

        // Verify that the project exists
        Long projectId = projectDTO.getId();
        if (!projectRepository.existsById(projectId)) {
            throw new BadRequestAlertException("Project not found", "project", "projectnotfound");
        }

        // Verify that the seller exists
        Long sellerId = projectDTO.getSeller().getId();
        if (!projectRepository.existsSeller(sellerId)) {
            throw new BadRequestAlertException("Seller not found", "project", "sellernotfound");
        }

        // Additional validation for required fields
        if (projectDTO.getName() == null || projectDTO.getName().trim().isEmpty()) {
            throw new BadRequestAlertException("Project name is required", "project", "namerequired");
        }

        if (projectDTO.getUnionHeadName() == null || projectDTO.getUnionHeadName().trim().isEmpty()) {
            throw new BadRequestAlertException("Union head name is required", "project", "unionheadnamerequired");
        }

        if (projectDTO.getUnionHeadMobileNumber() == null || projectDTO.getUnionHeadMobileNumber().trim().isEmpty()) {
            throw new BadRequestAlertException("Union head mobile number is required", "project", "unionheadmobilenumberrequired");
        }

        if (projectDTO.getNumberOfUnits() == null || projectDTO.getNumberOfUnits() <= 0) {
            throw new BadRequestAlertException("Number of units must be greater than zero", "project", "numberofunitsrequired");
        }

        // Set default status if not provided
        String status = projectDTO.getStatus() != null ? projectDTO.getStatus().toString() : Status.ACTIVE.toString();

        // Execute secure native update query with enhanced validation
        try {
            int rowsAffected = projectRepository.updateProjectNative(
                projectId,
                projectDTO.getName(),
                projectDTO.getDescription(),
                projectDTO.getStartDate(),
                projectDTO.getEndDate(),
                status,
                projectDTO.getFeesPerUnitPerMonth(),
                projectDTO.getUnionHeadName(),
                projectDTO.getUnionHeadMobileNumber(),
                projectDTO.getNumberOfUnits(),
                username
            );

            if (rowsAffected == 0) {
                throw new BadRequestAlertException(
                    "Failed to update project. Project may not exist or validation failed.",
                    "project",
                    "updatefailed"
                );
            }

            // Retrieve the updated project for the response
            return projectRepository
                .findById(projectId)
                .map(projectMapper::toDto)
                .orElseThrow(() -> new BadRequestAlertException("Error retrieving updated project", "project", "retrievalfailed"));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            LOG.error("Data integrity violation while updating project: {}", e.getMessage());
            throw new BadRequestAlertException(
                "Failed to update project due to data validation errors. Please check your input.",
                "project",
                "dataintegrity"
            );
        } catch (Exception e) {
            LOG.error("Unexpected error while updating project: {}", e.getMessage());
            throw new BadRequestAlertException("Failed to update project. Please try again.", "project", "updatefailed");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProjectDTO> findByIdSecure(Long projectId, String username, boolean isAdmin) {
        LOG.debug("Request to get Project securely : projectId={}, username={}, isAdmin={}", projectId, username, isAdmin);

        // Validate input
        if (projectId == null) {
            throw new BadRequestAlertException("Project ID cannot be null", "project", "idnull");
        }

        try {
            Optional<Project> projectOptional;

            if (isAdmin) {
                // Admin can access any project
                LOG.debug("Admin access - retrieving project {} without seller validation", projectId);
                projectOptional = projectRepository.findByIdForAdmin(projectId);
            } else {
                // For sellers, validate mobile number and ownership
                if (username == null || username.trim().isEmpty()) {
                    throw new BadRequestAlertException("Username cannot be null for seller access", "project", "usernamenull");
                }

                // Normalize the phone number to handle various formats
                String normalizedPhone = com.shaffaf.shaffafservice.util.PhoneNumberUtil.normalize(username);
                LOG.debug("Seller access - original username: '{}', normalized phone: '{}'", username, normalizedPhone);

                // Validate mobile number format for security
                if (!com.shaffaf.shaffafservice.util.PhoneNumberUtil.isValidPakistaniMobile(normalizedPhone)) {
                    String errorMessage = String.format(
                        "Invalid phone number format for seller access. Expected format: %s, but received: %s. " +
                        "Please ensure your seller account is registered with a valid Pakistani mobile number.",
                        com.shaffaf.shaffafservice.util.PhoneNumberUtil.EXAMPLE_PHONE_NUMBER,
                        username
                    );
                    LOG.warn("Phone validation failed for user: '{}' (normalized: '{}')", username, normalizedPhone);
                    throw new BadRequestAlertException(errorMessage, "project", "invalidphone");
                }

                // Get project with seller ownership validation
                LOG.debug("Seller access - retrieving project {} with phone number validation: {}", projectId, normalizedPhone);
                projectOptional = projectRepository.findByIdSecure(projectId, normalizedPhone);

                if (projectOptional.isEmpty()) {
                    LOG.debug("Project {} not found or not accessible for seller with phone: {}", projectId, normalizedPhone);
                    return Optional.empty();
                }
            }

            return projectOptional.map(projectMapper::toDto);
        } catch (BadRequestAlertException e) {
            // Re-throw validation exceptions
            throw e;
        } catch (Exception e) {
            LOG.error("Unexpected error while retrieving project {}: {}", projectId, e.getMessage());
            throw new BadRequestAlertException("Failed to retrieve project. Please try again.", "project", "retrievalfailed");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDTO> findAllSecure(
        String username,
        boolean isAdmin,
        int page,
        int size,
        String nameFilter,
        String statusFilter,
        String sellerNameFilter
    ) {
        LOG.debug("Request to get all projects securely. User: '{}', IsAdmin: {}, Page: {}, Size: {}", username, isAdmin, page, size);
        LOG.debug("Filters - Name: '{}', Status: '{}', SellerName: '{}'", nameFilter, statusFilter, sellerNameFilter);

        // Validate pagination parameters
        if (page < 0) {
            throw new BadRequestAlertException("Page number cannot be negative", "project", "invalidpage");
        }
        if (size <= 0 || size > 100) {
            throw new BadRequestAlertException("Page size must be between 1 and 100", "project", "invalidpagesize");
        }

        // Calculate offset for native SQL pagination
        int offset = page * size;

        // Sanitize filter inputs to prevent SQL injection
        String sanitizedNameFilter = sanitizeFilter(nameFilter);
        String sanitizedStatusFilter = sanitizeFilter(statusFilter);
        String sanitizedSellerNameFilter = sanitizeFilter(sellerNameFilter);

        try {
            if (isAdmin) {
                // Admin can access all projects
                LOG.debug("Admin access - retrieving all projects with filters");

                // Get projects and total count
                java.util.List<Project> projects = projectRepository.findAllForAdminSecure(
                    sanitizedNameFilter,
                    sanitizedStatusFilter,
                    sanitizedSellerNameFilter,
                    offset,
                    size
                );
                long totalCount = projectRepository.countForAdminSecure(
                    sanitizedNameFilter,
                    sanitizedStatusFilter,
                    sanitizedSellerNameFilter
                );

                // Convert to DTOs
                java.util.List<ProjectDTO> projectDTOs = projects
                    .stream()
                    .map(projectMapper::toDto)
                    .collect(java.util.stream.Collectors.toList());

                LOG.info("Admin retrieved {} projects out of {} total", projectDTOs.size(), totalCount);

                // Create Page object
                return new org.springframework.data.domain.PageImpl<>(
                    projectDTOs,
                    org.springframework.data.domain.PageRequest.of(page, size),
                    totalCount
                );
            } else {
                // For sellers, validate mobile number and get only their projects
                if (username == null || username.trim().isEmpty()) {
                    throw new BadRequestAlertException("Username cannot be null for seller access", "project", "usernamenull");
                }

                // Normalize the phone number to handle various formats
                String normalizedPhone = com.shaffaf.shaffafservice.util.PhoneNumberUtil.normalize(username);
                LOG.debug("Seller access - original username: '{}', normalized phone: '{}'", username, normalizedPhone);

                // Validate mobile number format for security
                if (!com.shaffaf.shaffafservice.util.PhoneNumberUtil.isValidPakistaniMobile(normalizedPhone)) {
                    String errorMessage = String.format(
                        "Invalid phone number format for seller access. Expected format: %s, but received: %s. " +
                        "Please ensure your seller account is registered with a valid Pakistani mobile number.",
                        com.shaffaf.shaffafservice.util.PhoneNumberUtil.EXAMPLE_PHONE_NUMBER,
                        username
                    );
                    LOG.warn("Phone validation failed for user: '{}' (normalized: '{}')", username, normalizedPhone);
                    throw new BadRequestAlertException(errorMessage, "project", "invalidphone");
                }

                // Get projects for this seller only (ignore sellerNameFilter for seller users)
                LOG.debug("Seller access - retrieving projects for phone number: {}", normalizedPhone);

                java.util.List<Project> projects = projectRepository.findAllForSellerSecure(
                    normalizedPhone,
                    sanitizedNameFilter,
                    sanitizedStatusFilter,
                    offset,
                    size
                );
                long totalCount = projectRepository.countForSellerSecure(normalizedPhone, sanitizedNameFilter, sanitizedStatusFilter);

                // Convert to DTOs
                java.util.List<ProjectDTO> projectDTOs = projects
                    .stream()
                    .map(projectMapper::toDto)
                    .collect(java.util.stream.Collectors.toList());

                LOG.info("Seller '{}' retrieved {} projects out of {} total", normalizedPhone, projectDTOs.size(), totalCount);

                // Create Page object
                return new org.springframework.data.domain.PageImpl<>(
                    projectDTOs,
                    org.springframework.data.domain.PageRequest.of(page, size),
                    totalCount
                );
            }
        } catch (BadRequestAlertException e) {
            // Re-throw validation exceptions
            throw e;
        } catch (Exception e) {
            LOG.error("Unexpected error while retrieving projects for user '{}': {}", username, e.getMessage());
            throw new BadRequestAlertException("Failed to retrieve projects. Please try again.", "project", "retrievalfailed");
        }
    }

    /**
     * Sanitizes filter inputs to prevent SQL injection attacks.
     *
     * @param filter the filter string to sanitize
     * @return sanitized filter string, null if input was null or empty
     */
    private String sanitizeFilter(String filter) {
        if (filter == null || filter.trim().isEmpty()) {
            return null;
        }

        // Remove potentially dangerous characters and limit length
        String sanitized = filter
            .trim()
            .replaceAll("[;'\"\\\\]", "") // Remove SQL injection characters
            .replaceAll("--", "") // Remove SQL comments
            .replaceAll("/\\*.*?\\*/", "") // Remove SQL block comments
            .substring(0, Math.min(filter.length(), 100)); // Limit length

        return sanitized.isEmpty() ? null : sanitized;
    }
}
