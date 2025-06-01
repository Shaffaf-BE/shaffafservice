package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.ProjectDiscount;
import com.shaffaf.shaffafservice.repository.ProjectDiscountRepository;
import com.shaffaf.shaffafservice.service.ProjectDiscountService;
import com.shaffaf.shaffafservice.service.dto.ProjectDiscountDTO;
import com.shaffaf.shaffafservice.service.mapper.ProjectDiscountMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shaffaf.shaffafservice.domain.ProjectDiscount}.
 */
@Service
@Transactional
public class ProjectDiscountServiceImpl implements ProjectDiscountService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectDiscountServiceImpl.class);

    private final ProjectDiscountRepository projectDiscountRepository;

    private final ProjectDiscountMapper projectDiscountMapper;

    public ProjectDiscountServiceImpl(ProjectDiscountRepository projectDiscountRepository, ProjectDiscountMapper projectDiscountMapper) {
        this.projectDiscountRepository = projectDiscountRepository;
        this.projectDiscountMapper = projectDiscountMapper;
    }

    @Override
    public ProjectDiscountDTO save(ProjectDiscountDTO projectDiscountDTO) {
        LOG.debug("Request to save ProjectDiscount : {}", projectDiscountDTO);
        ProjectDiscount projectDiscount = projectDiscountMapper.toEntity(projectDiscountDTO);
        projectDiscount = projectDiscountRepository.save(projectDiscount);
        return projectDiscountMapper.toDto(projectDiscount);
    }

    @Override
    public ProjectDiscountDTO update(ProjectDiscountDTO projectDiscountDTO) {
        LOG.debug("Request to update ProjectDiscount : {}", projectDiscountDTO);
        ProjectDiscount projectDiscount = projectDiscountMapper.toEntity(projectDiscountDTO);
        projectDiscount = projectDiscountRepository.save(projectDiscount);
        return projectDiscountMapper.toDto(projectDiscount);
    }

    @Override
    public Optional<ProjectDiscountDTO> partialUpdate(ProjectDiscountDTO projectDiscountDTO) {
        LOG.debug("Request to partially update ProjectDiscount : {}", projectDiscountDTO);

        return projectDiscountRepository
            .findById(projectDiscountDTO.getId())
            .map(existingProjectDiscount -> {
                projectDiscountMapper.partialUpdate(existingProjectDiscount, projectDiscountDTO);

                return existingProjectDiscount;
            })
            .map(projectDiscountRepository::save)
            .map(projectDiscountMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDiscountDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ProjectDiscounts");
        return projectDiscountRepository.findAll(pageable).map(projectDiscountMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProjectDiscountDTO> findOne(Long id) {
        LOG.debug("Request to get ProjectDiscount : {}", id);
        return projectDiscountRepository.findById(id).map(projectDiscountMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ProjectDiscount : {}", id);
        projectDiscountRepository.deleteById(id);
    }
}
