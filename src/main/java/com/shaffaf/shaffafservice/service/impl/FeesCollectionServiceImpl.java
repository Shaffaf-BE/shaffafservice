package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.FeesCollection;
import com.shaffaf.shaffafservice.repository.FeesCollectionRepository;
import com.shaffaf.shaffafservice.service.FeesCollectionService;
import com.shaffaf.shaffafservice.service.dto.FeesCollectionDTO;
import com.shaffaf.shaffafservice.service.mapper.FeesCollectionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shaffaf.shaffafservice.domain.FeesCollection}.
 */
@Service
@Transactional
public class FeesCollectionServiceImpl implements FeesCollectionService {

    private static final Logger LOG = LoggerFactory.getLogger(FeesCollectionServiceImpl.class);

    private final FeesCollectionRepository feesCollectionRepository;

    private final FeesCollectionMapper feesCollectionMapper;

    public FeesCollectionServiceImpl(FeesCollectionRepository feesCollectionRepository, FeesCollectionMapper feesCollectionMapper) {
        this.feesCollectionRepository = feesCollectionRepository;
        this.feesCollectionMapper = feesCollectionMapper;
    }

    @Override
    public FeesCollectionDTO save(FeesCollectionDTO feesCollectionDTO) {
        LOG.debug("Request to save FeesCollection : {}", feesCollectionDTO);
        FeesCollection feesCollection = feesCollectionMapper.toEntity(feesCollectionDTO);
        feesCollection = feesCollectionRepository.save(feesCollection);
        return feesCollectionMapper.toDto(feesCollection);
    }

    @Override
    public FeesCollectionDTO update(FeesCollectionDTO feesCollectionDTO) {
        LOG.debug("Request to update FeesCollection : {}", feesCollectionDTO);
        FeesCollection feesCollection = feesCollectionMapper.toEntity(feesCollectionDTO);
        feesCollection = feesCollectionRepository.save(feesCollection);
        return feesCollectionMapper.toDto(feesCollection);
    }

    @Override
    public Optional<FeesCollectionDTO> partialUpdate(FeesCollectionDTO feesCollectionDTO) {
        LOG.debug("Request to partially update FeesCollection : {}", feesCollectionDTO);

        return feesCollectionRepository
            .findById(feesCollectionDTO.getId())
            .map(existingFeesCollection -> {
                feesCollectionMapper.partialUpdate(existingFeesCollection, feesCollectionDTO);

                return existingFeesCollection;
            })
            .map(feesCollectionRepository::save)
            .map(feesCollectionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FeesCollectionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all FeesCollections");
        return feesCollectionRepository.findAll(pageable).map(feesCollectionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FeesCollectionDTO> findOne(Long id) {
        LOG.debug("Request to get FeesCollection : {}", id);
        return feesCollectionRepository.findById(id).map(feesCollectionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete FeesCollection : {}", id);
        feesCollectionRepository.deleteById(id);
    }
}
