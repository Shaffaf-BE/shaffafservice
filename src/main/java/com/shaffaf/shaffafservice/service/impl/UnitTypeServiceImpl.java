package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.UnitType;
import com.shaffaf.shaffafservice.repository.UnitTypeRepository;
import com.shaffaf.shaffafservice.security.SecurityUtils;
import com.shaffaf.shaffafservice.service.UnitTypeService;
import com.shaffaf.shaffafservice.service.dto.UnitTypeDTO;
import com.shaffaf.shaffafservice.service.mapper.UnitTypeMapper;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shaffaf.shaffafservice.domain.UnitType}.
 */
@Service
@Transactional
public class UnitTypeServiceImpl implements UnitTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(UnitTypeServiceImpl.class);

    private final UnitTypeRepository unitTypeRepository;

    private final UnitTypeMapper unitTypeMapper;

    public UnitTypeServiceImpl(UnitTypeMapper unitTypeMapper, UnitTypeRepository unitTypeRepository) {
        this.unitTypeMapper = unitTypeMapper;
        this.unitTypeRepository = unitTypeRepository;
    }

    @Override
    @Transactional
    public UnitTypeDTO save(UnitTypeDTO unitTypeDTO) {
        LOG.debug("Request to save UnitType : {}", unitTypeDTO);

        String username = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("Current user login not found"));
        unitTypeDTO.setCreatedBy(username);
        unitTypeDTO.setCreatedDate(Instant.now());

        UnitType unitType = unitTypeRepository.save(unitTypeMapper.toEntity(unitTypeDTO));
        return unitTypeMapper.toDto(unitType);
    }

    @Override
    @Transactional
    public UnitTypeDTO update(UnitTypeDTO unitTypeDTO) {
        LOG.debug("Request to update UnitType : {}", unitTypeDTO);

        String username = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("Current user login not found"));

        unitTypeRepository.updateUnitType(
            unitTypeDTO.getId(),
            unitTypeDTO.getName(),
            unitTypeDTO.getProject().getId(),
            username,
            unitTypeDTO.getDeletedOn()
        );

        return unitTypeDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UnitTypeDTO> findOne(Long id) {
        LOG.debug("Request to get UnitType : {}", id);
        return unitTypeRepository.findByIdNative(id).map(unitTypeMapper::toDto);
    }

    @Override
    public Page<UnitTypeDTO> findAllByProjectId(Long projectId, Pageable pageable) {
        LOG.debug("Request to get all UnitTypes by projectId : {}", projectId);
        return unitTypeRepository.findAllByProjectIdNative(projectId, pageable).map(unitTypeMapper::toDto);
    }
}
