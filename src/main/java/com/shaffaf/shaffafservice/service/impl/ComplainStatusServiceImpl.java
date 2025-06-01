package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.ComplainStatus;
import com.shaffaf.shaffafservice.repository.ComplainStatusRepository;
import com.shaffaf.shaffafservice.service.ComplainStatusService;
import com.shaffaf.shaffafservice.service.dto.ComplainStatusDTO;
import com.shaffaf.shaffafservice.service.mapper.ComplainStatusMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shaffaf.shaffafservice.domain.ComplainStatus}.
 */
@Service
@Transactional
public class ComplainStatusServiceImpl implements ComplainStatusService {

    private static final Logger LOG = LoggerFactory.getLogger(ComplainStatusServiceImpl.class);

    private final ComplainStatusRepository complainStatusRepository;

    private final ComplainStatusMapper complainStatusMapper;

    public ComplainStatusServiceImpl(ComplainStatusRepository complainStatusRepository, ComplainStatusMapper complainStatusMapper) {
        this.complainStatusRepository = complainStatusRepository;
        this.complainStatusMapper = complainStatusMapper;
    }

    @Override
    public ComplainStatusDTO save(ComplainStatusDTO complainStatusDTO) {
        LOG.debug("Request to save ComplainStatus : {}", complainStatusDTO);
        ComplainStatus complainStatus = complainStatusMapper.toEntity(complainStatusDTO);
        complainStatus = complainStatusRepository.save(complainStatus);
        return complainStatusMapper.toDto(complainStatus);
    }

    @Override
    public ComplainStatusDTO update(ComplainStatusDTO complainStatusDTO) {
        LOG.debug("Request to update ComplainStatus : {}", complainStatusDTO);
        ComplainStatus complainStatus = complainStatusMapper.toEntity(complainStatusDTO);
        complainStatus = complainStatusRepository.save(complainStatus);
        return complainStatusMapper.toDto(complainStatus);
    }

    @Override
    public Optional<ComplainStatusDTO> partialUpdate(ComplainStatusDTO complainStatusDTO) {
        LOG.debug("Request to partially update ComplainStatus : {}", complainStatusDTO);

        return complainStatusRepository
            .findById(complainStatusDTO.getId())
            .map(existingComplainStatus -> {
                complainStatusMapper.partialUpdate(existingComplainStatus, complainStatusDTO);

                return existingComplainStatus;
            })
            .map(complainStatusRepository::save)
            .map(complainStatusMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComplainStatusDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ComplainStatuses");
        return complainStatusRepository.findAll(pageable).map(complainStatusMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ComplainStatusDTO> findOne(Long id) {
        LOG.debug("Request to get ComplainStatus : {}", id);
        return complainStatusRepository.findById(id).map(complainStatusMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ComplainStatus : {}", id);
        complainStatusRepository.deleteById(id);
    }
}
