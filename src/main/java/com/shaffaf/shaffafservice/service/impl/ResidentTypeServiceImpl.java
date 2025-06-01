package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.ResidentType;
import com.shaffaf.shaffafservice.repository.ResidentTypeRepository;
import com.shaffaf.shaffafservice.service.ResidentTypeService;
import com.shaffaf.shaffafservice.service.dto.ResidentTypeDTO;
import com.shaffaf.shaffafservice.service.mapper.ResidentTypeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shaffaf.shaffafservice.domain.ResidentType}.
 */
@Service
@Transactional
public class ResidentTypeServiceImpl implements ResidentTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(ResidentTypeServiceImpl.class);

    private final ResidentTypeRepository residentTypeRepository;

    private final ResidentTypeMapper residentTypeMapper;

    public ResidentTypeServiceImpl(ResidentTypeRepository residentTypeRepository, ResidentTypeMapper residentTypeMapper) {
        this.residentTypeRepository = residentTypeRepository;
        this.residentTypeMapper = residentTypeMapper;
    }

    @Override
    public ResidentTypeDTO save(ResidentTypeDTO residentTypeDTO) {
        LOG.debug("Request to save ResidentType : {}", residentTypeDTO);
        ResidentType residentType = residentTypeMapper.toEntity(residentTypeDTO);
        residentType = residentTypeRepository.save(residentType);
        return residentTypeMapper.toDto(residentType);
    }

    @Override
    public ResidentTypeDTO update(ResidentTypeDTO residentTypeDTO) {
        LOG.debug("Request to update ResidentType : {}", residentTypeDTO);
        ResidentType residentType = residentTypeMapper.toEntity(residentTypeDTO);
        residentType = residentTypeRepository.save(residentType);
        return residentTypeMapper.toDto(residentType);
    }

    @Override
    public Optional<ResidentTypeDTO> partialUpdate(ResidentTypeDTO residentTypeDTO) {
        LOG.debug("Request to partially update ResidentType : {}", residentTypeDTO);

        return residentTypeRepository
            .findById(residentTypeDTO.getId())
            .map(existingResidentType -> {
                residentTypeMapper.partialUpdate(existingResidentType, residentTypeDTO);

                return existingResidentType;
            })
            .map(residentTypeRepository::save)
            .map(residentTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResidentTypeDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ResidentTypes");
        return residentTypeRepository.findAll(pageable).map(residentTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ResidentTypeDTO> findOne(Long id) {
        LOG.debug("Request to get ResidentType : {}", id);
        return residentTypeRepository.findById(id).map(residentTypeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ResidentType : {}", id);
        residentTypeRepository.deleteById(id);
    }
}
