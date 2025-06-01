package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.SellerCommission;
import com.shaffaf.shaffafservice.repository.SellerCommissionRepository;
import com.shaffaf.shaffafservice.service.SellerCommissionService;
import com.shaffaf.shaffafservice.service.dto.SellerCommissionDTO;
import com.shaffaf.shaffafservice.service.mapper.SellerCommissionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shaffaf.shaffafservice.domain.SellerCommission}.
 */
@Service
@Transactional
public class SellerCommissionServiceImpl implements SellerCommissionService {

    private static final Logger LOG = LoggerFactory.getLogger(SellerCommissionServiceImpl.class);

    private final SellerCommissionRepository sellerCommissionRepository;

    private final SellerCommissionMapper sellerCommissionMapper;

    public SellerCommissionServiceImpl(
        SellerCommissionRepository sellerCommissionRepository,
        SellerCommissionMapper sellerCommissionMapper
    ) {
        this.sellerCommissionRepository = sellerCommissionRepository;
        this.sellerCommissionMapper = sellerCommissionMapper;
    }

    @Override
    public SellerCommissionDTO save(SellerCommissionDTO sellerCommissionDTO) {
        LOG.debug("Request to save SellerCommission : {}", sellerCommissionDTO);
        SellerCommission sellerCommission = sellerCommissionMapper.toEntity(sellerCommissionDTO);
        sellerCommission = sellerCommissionRepository.save(sellerCommission);
        return sellerCommissionMapper.toDto(sellerCommission);
    }

    @Override
    public SellerCommissionDTO update(SellerCommissionDTO sellerCommissionDTO) {
        LOG.debug("Request to update SellerCommission : {}", sellerCommissionDTO);
        SellerCommission sellerCommission = sellerCommissionMapper.toEntity(sellerCommissionDTO);
        sellerCommission = sellerCommissionRepository.save(sellerCommission);
        return sellerCommissionMapper.toDto(sellerCommission);
    }

    @Override
    public Optional<SellerCommissionDTO> partialUpdate(SellerCommissionDTO sellerCommissionDTO) {
        LOG.debug("Request to partially update SellerCommission : {}", sellerCommissionDTO);

        return sellerCommissionRepository
            .findById(sellerCommissionDTO.getId())
            .map(existingSellerCommission -> {
                sellerCommissionMapper.partialUpdate(existingSellerCommission, sellerCommissionDTO);

                return existingSellerCommission;
            })
            .map(sellerCommissionRepository::save)
            .map(sellerCommissionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SellerCommissionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all SellerCommissions");
        return sellerCommissionRepository.findAll(pageable).map(sellerCommissionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SellerCommissionDTO> findOne(Long id) {
        LOG.debug("Request to get SellerCommission : {}", id);
        return sellerCommissionRepository.findById(id).map(sellerCommissionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SellerCommission : {}", id);
        sellerCommissionRepository.deleteById(id);
    }
}
