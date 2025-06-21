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
}
