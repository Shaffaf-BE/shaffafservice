package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.UnitType;
import com.shaffaf.shaffafservice.repository.UnitTypeRepository;
import com.shaffaf.shaffafservice.service.UnitTypeService;
import com.shaffaf.shaffafservice.service.dto.UnitTypeDTO;
import com.shaffaf.shaffafservice.service.mapper.UnitTypeMapper;
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

    public UnitTypeServiceImpl(UnitTypeRepository unitTypeRepository, UnitTypeMapper unitTypeMapper) {
        this.unitTypeRepository = unitTypeRepository;
        this.unitTypeMapper = unitTypeMapper;
    }

    @Override
    public UnitTypeDTO save(UnitTypeDTO unitTypeDTO) {
        LOG.debug("Request to save UnitType : {}", unitTypeDTO);
        UnitType unitType = unitTypeMapper.toEntity(unitTypeDTO);
        unitType = unitTypeRepository.save(unitType);
        return unitTypeMapper.toDto(unitType);
    }

    @Override
    public UnitTypeDTO update(UnitTypeDTO unitTypeDTO) {
        LOG.debug("Request to update UnitType : {}", unitTypeDTO);
        UnitType unitType = unitTypeMapper.toEntity(unitTypeDTO);
        unitType = unitTypeRepository.save(unitType);
        return unitTypeMapper.toDto(unitType);
    }

    @Override
    public Optional<UnitTypeDTO> partialUpdate(UnitTypeDTO unitTypeDTO) {
        LOG.debug("Request to partially update UnitType : {}", unitTypeDTO);

        return unitTypeRepository
            .findById(unitTypeDTO.getId())
            .map(existingUnitType -> {
                unitTypeMapper.partialUpdate(existingUnitType, unitTypeDTO);

                return existingUnitType;
            })
            .map(unitTypeRepository::save)
            .map(unitTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UnitTypeDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all UnitTypes");
        return unitTypeRepository.findAll(pageable).map(unitTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UnitTypeDTO> findOne(Long id) {
        LOG.debug("Request to get UnitType : {}", id);
        return unitTypeRepository.findById(id).map(unitTypeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete UnitType : {}", id);
        unitTypeRepository.deleteById(id);
    }
}
