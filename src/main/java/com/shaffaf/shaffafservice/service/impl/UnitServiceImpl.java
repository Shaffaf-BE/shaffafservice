package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.Unit;
import com.shaffaf.shaffafservice.repository.UnitRepository;
import com.shaffaf.shaffafservice.service.UnitService;
import com.shaffaf.shaffafservice.service.dto.UnitDTO;
import com.shaffaf.shaffafservice.service.mapper.UnitMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shaffaf.shaffafservice.domain.Unit}.
 */
@Service
@Transactional
public class UnitServiceImpl implements UnitService {

    private static final Logger LOG = LoggerFactory.getLogger(UnitServiceImpl.class);

    private final UnitRepository unitRepository;

    private final UnitMapper unitMapper;

    public UnitServiceImpl(UnitRepository unitRepository, UnitMapper unitMapper) {
        this.unitRepository = unitRepository;
        this.unitMapper = unitMapper;
    }

    @Override
    public UnitDTO save(UnitDTO unitDTO) {
        LOG.debug("Request to save Unit : {}", unitDTO);
        Unit unit = unitMapper.toEntity(unitDTO);
        unit = unitRepository.save(unit);
        return unitMapper.toDto(unit);
    }

    @Override
    public UnitDTO update(UnitDTO unitDTO) {
        LOG.debug("Request to update Unit : {}", unitDTO);
        Unit unit = unitMapper.toEntity(unitDTO);
        unit = unitRepository.save(unit);
        return unitMapper.toDto(unit);
    }

    @Override
    public Optional<UnitDTO> partialUpdate(UnitDTO unitDTO) {
        LOG.debug("Request to partially update Unit : {}", unitDTO);

        return unitRepository
            .findById(unitDTO.getId())
            .map(existingUnit -> {
                unitMapper.partialUpdate(existingUnit, unitDTO);

                return existingUnit;
            })
            .map(unitRepository::save)
            .map(unitMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UnitDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Units");
        return unitRepository.findAll(pageable).map(unitMapper::toDto);
    }

    public Page<UnitDTO> findAllWithEagerRelationships(Pageable pageable) {
        return unitRepository.findAllWithEagerRelationships(pageable).map(unitMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UnitDTO> findOne(Long id) {
        LOG.debug("Request to get Unit : {}", id);
        return unitRepository.findOneWithEagerRelationships(id).map(unitMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Unit : {}", id);
        unitRepository.deleteById(id);
    }
}
