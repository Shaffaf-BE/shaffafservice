package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.ComplainType;
import com.shaffaf.shaffafservice.repository.ComplainTypeRepository;
import com.shaffaf.shaffafservice.service.ComplainTypeService;
import com.shaffaf.shaffafservice.service.dto.ComplainTypeDTO;
import com.shaffaf.shaffafservice.service.mapper.ComplainTypeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shaffaf.shaffafservice.domain.ComplainType}.
 */
@Service
@Transactional
public class ComplainTypeServiceImpl implements ComplainTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(ComplainTypeServiceImpl.class);

    private final ComplainTypeRepository complainTypeRepository;

    private final ComplainTypeMapper complainTypeMapper;

    public ComplainTypeServiceImpl(ComplainTypeRepository complainTypeRepository, ComplainTypeMapper complainTypeMapper) {
        this.complainTypeRepository = complainTypeRepository;
        this.complainTypeMapper = complainTypeMapper;
    }

    @Override
    public ComplainTypeDTO save(ComplainTypeDTO complainTypeDTO) {
        LOG.debug("Request to save ComplainType : {}", complainTypeDTO);
        ComplainType complainType = complainTypeMapper.toEntity(complainTypeDTO);
        complainType = complainTypeRepository.save(complainType);
        return complainTypeMapper.toDto(complainType);
    }

    @Override
    public ComplainTypeDTO update(ComplainTypeDTO complainTypeDTO) {
        LOG.debug("Request to update ComplainType : {}", complainTypeDTO);
        ComplainType complainType = complainTypeMapper.toEntity(complainTypeDTO);
        complainType = complainTypeRepository.save(complainType);
        return complainTypeMapper.toDto(complainType);
    }

    @Override
    public Optional<ComplainTypeDTO> partialUpdate(ComplainTypeDTO complainTypeDTO) {
        LOG.debug("Request to partially update ComplainType : {}", complainTypeDTO);

        return complainTypeRepository
            .findById(complainTypeDTO.getId())
            .map(existingComplainType -> {
                complainTypeMapper.partialUpdate(existingComplainType, complainTypeDTO);

                return existingComplainType;
            })
            .map(complainTypeRepository::save)
            .map(complainTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComplainTypeDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ComplainTypes");
        return complainTypeRepository.findAll(pageable).map(complainTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ComplainTypeDTO> findOne(Long id) {
        LOG.debug("Request to get ComplainType : {}", id);
        return complainTypeRepository.findById(id).map(complainTypeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ComplainType : {}", id);
        complainTypeRepository.deleteById(id);
    }
}
