package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.FeesConfiguration;
import com.shaffaf.shaffafservice.repository.FeesConfigurationRepository;
import com.shaffaf.shaffafservice.service.FeesConfigurationService;
import com.shaffaf.shaffafservice.service.dto.FeesConfigurationDTO;
import com.shaffaf.shaffafservice.service.mapper.FeesConfigurationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shaffaf.shaffafservice.domain.FeesConfiguration}.
 */
@Service
@Transactional
public class FeesConfigurationServiceImpl implements FeesConfigurationService {

    private static final Logger LOG = LoggerFactory.getLogger(FeesConfigurationServiceImpl.class);

    private final FeesConfigurationRepository feesConfigurationRepository;

    private final FeesConfigurationMapper feesConfigurationMapper;

    public FeesConfigurationServiceImpl(
        FeesConfigurationRepository feesConfigurationRepository,
        FeesConfigurationMapper feesConfigurationMapper
    ) {
        this.feesConfigurationRepository = feesConfigurationRepository;
        this.feesConfigurationMapper = feesConfigurationMapper;
    }

    @Override
    public FeesConfigurationDTO save(FeesConfigurationDTO feesConfigurationDTO) {
        LOG.debug("Request to save FeesConfiguration : {}", feesConfigurationDTO);
        FeesConfiguration feesConfiguration = feesConfigurationMapper.toEntity(feesConfigurationDTO);
        feesConfiguration = feesConfigurationRepository.save(feesConfiguration);
        return feesConfigurationMapper.toDto(feesConfiguration);
    }

    @Override
    public FeesConfigurationDTO update(FeesConfigurationDTO feesConfigurationDTO) {
        LOG.debug("Request to update FeesConfiguration : {}", feesConfigurationDTO);
        FeesConfiguration feesConfiguration = feesConfigurationMapper.toEntity(feesConfigurationDTO);
        feesConfiguration = feesConfigurationRepository.save(feesConfiguration);
        return feesConfigurationMapper.toDto(feesConfiguration);
    }

    @Override
    public Optional<FeesConfigurationDTO> partialUpdate(FeesConfigurationDTO feesConfigurationDTO) {
        LOG.debug("Request to partially update FeesConfiguration : {}", feesConfigurationDTO);

        return feesConfigurationRepository
            .findById(feesConfigurationDTO.getId())
            .map(existingFeesConfiguration -> {
                feesConfigurationMapper.partialUpdate(existingFeesConfiguration, feesConfigurationDTO);

                return existingFeesConfiguration;
            })
            .map(feesConfigurationRepository::save)
            .map(feesConfigurationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FeesConfigurationDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all FeesConfigurations");
        return feesConfigurationRepository.findAll(pageable).map(feesConfigurationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FeesConfigurationDTO> findOne(Long id) {
        LOG.debug("Request to get FeesConfiguration : {}", id);
        return feesConfigurationRepository.findById(id).map(feesConfigurationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete FeesConfiguration : {}", id);
        feesConfigurationRepository.deleteById(id);
    }
}
