package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.Complain;
import com.shaffaf.shaffafservice.repository.ComplainRepository;
import com.shaffaf.shaffafservice.service.ComplainService;
import com.shaffaf.shaffafservice.service.dto.ComplainDTO;
import com.shaffaf.shaffafservice.service.mapper.ComplainMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shaffaf.shaffafservice.domain.Complain}.
 */
@Service
@Transactional
public class ComplainServiceImpl implements ComplainService {

    private static final Logger LOG = LoggerFactory.getLogger(ComplainServiceImpl.class);

    private final ComplainRepository complainRepository;

    private final ComplainMapper complainMapper;

    public ComplainServiceImpl(ComplainRepository complainRepository, ComplainMapper complainMapper) {
        this.complainRepository = complainRepository;
        this.complainMapper = complainMapper;
    }

    @Override
    public ComplainDTO save(ComplainDTO complainDTO) {
        LOG.debug("Request to save Complain : {}", complainDTO);
        Complain complain = complainMapper.toEntity(complainDTO);
        complain = complainRepository.save(complain);
        return complainMapper.toDto(complain);
    }

    @Override
    public ComplainDTO update(ComplainDTO complainDTO) {
        LOG.debug("Request to update Complain : {}", complainDTO);
        Complain complain = complainMapper.toEntity(complainDTO);
        complain = complainRepository.save(complain);
        return complainMapper.toDto(complain);
    }

    @Override
    public Optional<ComplainDTO> partialUpdate(ComplainDTO complainDTO) {
        LOG.debug("Request to partially update Complain : {}", complainDTO);

        return complainRepository
            .findById(complainDTO.getId())
            .map(existingComplain -> {
                complainMapper.partialUpdate(existingComplain, complainDTO);

                return existingComplain;
            })
            .map(complainRepository::save)
            .map(complainMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComplainDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Complains");
        return complainRepository.findAll(pageable).map(complainMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ComplainDTO> findOne(Long id) {
        LOG.debug("Request to get Complain : {}", id);
        return complainRepository.findById(id).map(complainMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Complain : {}", id);
        complainRepository.deleteById(id);
    }
}
