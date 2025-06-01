package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.Resident;
import com.shaffaf.shaffafservice.repository.ResidentRepository;
import com.shaffaf.shaffafservice.service.ResidentService;
import com.shaffaf.shaffafservice.service.dto.ResidentDTO;
import com.shaffaf.shaffafservice.service.mapper.ResidentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shaffaf.shaffafservice.domain.Resident}.
 */
@Service
@Transactional
public class ResidentServiceImpl implements ResidentService {

    private static final Logger LOG = LoggerFactory.getLogger(ResidentServiceImpl.class);

    private final ResidentRepository residentRepository;

    private final ResidentMapper residentMapper;

    public ResidentServiceImpl(ResidentRepository residentRepository, ResidentMapper residentMapper) {
        this.residentRepository = residentRepository;
        this.residentMapper = residentMapper;
    }

    @Override
    public ResidentDTO save(ResidentDTO residentDTO) {
        LOG.debug("Request to save Resident : {}", residentDTO);
        Resident resident = residentMapper.toEntity(residentDTO);
        resident = residentRepository.save(resident);
        return residentMapper.toDto(resident);
    }

    @Override
    public ResidentDTO update(ResidentDTO residentDTO) {
        LOG.debug("Request to update Resident : {}", residentDTO);
        Resident resident = residentMapper.toEntity(residentDTO);
        resident = residentRepository.save(resident);
        return residentMapper.toDto(resident);
    }

    @Override
    public Optional<ResidentDTO> partialUpdate(ResidentDTO residentDTO) {
        LOG.debug("Request to partially update Resident : {}", residentDTO);

        return residentRepository
            .findById(residentDTO.getId())
            .map(existingResident -> {
                residentMapper.partialUpdate(existingResident, residentDTO);

                return existingResident;
            })
            .map(residentRepository::save)
            .map(residentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResidentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Residents");
        return residentRepository.findAll(pageable).map(residentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ResidentDTO> findOne(Long id) {
        LOG.debug("Request to get Resident : {}", id);
        return residentRepository.findById(id).map(residentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Resident : {}", id);
        residentRepository.deleteById(id);
    }
}
