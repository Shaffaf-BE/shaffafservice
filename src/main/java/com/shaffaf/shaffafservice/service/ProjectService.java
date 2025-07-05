package com.shaffaf.shaffafservice.service;

import com.shaffaf.shaffafservice.service.dto.ProjectDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.shaffaf.shaffafservice.domain.Project}.
 */
public interface ProjectService {
    /**
     * Create a new project using native SQL for better performance and security.
     * This method is specifically designed for sellers to create projects.
     *
     * @param projectDTO the project to create, with seller information
     * @param username the username of the currently authenticated user
     * @return the created ProjectDTO with ID
     * @throws IllegalArgumentException if validation fails
     */
    ProjectDTO createProjectNative(ProjectDTO projectDTO, String username);

    /**
     * Update an existing project using native SQL for better performance and security.
     * This method is specifically designed for sellers to update their own projects.
     * Only the seller who created the project or an admin can update it.
     *
     * @param projectDTO the project to update, with seller information and project ID
     * @param username the username of the currently authenticated user
     * @return the updated ProjectDTO
     * @throws IllegalArgumentException if validation fails
     */
    ProjectDTO updateProjectNative(ProjectDTO projectDTO, String username);

    /**
     * Get a project by ID with security validation using native SQL.
     * This method ensures that:
     * - SELLER users can only access their own projects (validated by mobile number)
     * - ADMIN users can access any project
     * - Uses native SQL for better performance and security
     *
     * @param projectId the ID of the project to retrieve
     * @param username the username of the currently authenticated user (mobile number for sellers)
     * @param isAdmin whether the current user is an admin
     * @return the ProjectDTO if found and accessible, empty otherwise
     * @throws IllegalArgumentException if validation fails
     */
    Optional<ProjectDTO> findByIdSecure(Long projectId, String username, boolean isAdmin);

    /**
     * Get all projects with security validation, pagination, and filtering using native SQL.
     * This method ensures that:
     * - SELLER users can only access their own projects (validated by mobile number)
     * - ADMIN users can access all projects
     * - Uses native SQL for better performance and security
     * - Supports pagination and filtering
     *
     * @param username the username of the currently authenticated user (mobile number for sellers)
     * @param isAdmin whether the current user is an admin
     * @param page the page number (0-based)
     * @param size the page size
     * @param nameFilter filter by project name (optional)
     * @param statusFilter filter by project status (optional)
     * @param sellerNameFilter filter by seller name (optional, admin only)
     * @return a Page of ProjectDTOs accessible to the user
     * @throws IllegalArgumentException if validation fails
     */
    Page<ProjectDTO> findAllSecure(
        String username,
        boolean isAdmin,
        int page,
        int size,
        String nameFilter,
        String statusFilter,
        String sellerNameFilter
    );
}
