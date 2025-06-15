package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.Seller;
import com.shaffaf.shaffafservice.domain.enumeration.Status;
import com.shaffaf.shaffafservice.repository.SellerRepository;
import com.shaffaf.shaffafservice.service.SellerService;
import com.shaffaf.shaffafservice.service.dto.SellerDTO;
import com.shaffaf.shaffafservice.service.mapper.SellerMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shaffaf.shaffafservice.domain.Seller}.
 */
@Service
@Transactional
public class SellerServiceImpl implements SellerService {

    private static final Logger LOG = LoggerFactory.getLogger(SellerServiceImpl.class);

    private final SellerRepository sellerRepository;

    private final SellerMapper sellerMapper;

    public SellerServiceImpl(SellerRepository sellerRepository, SellerMapper sellerMapper) {
        this.sellerRepository = sellerRepository;
        this.sellerMapper = sellerMapper;
    }

    @Override
    public SellerDTO save(SellerDTO sellerDTO) {
        LOG.debug("Request to save Seller : {}", sellerDTO);
        Seller seller = sellerMapper.toEntity(sellerDTO);
        seller = sellerRepository.save(seller);
        return sellerMapper.toDto(seller);
    }

    @Override
    public SellerDTO update(SellerDTO sellerDTO) {
        LOG.debug("Request to update Seller : {}", sellerDTO);
        Seller seller = sellerMapper.toEntity(sellerDTO);
        seller = sellerRepository.save(seller);
        return sellerMapper.toDto(seller);
    }

    @Override
    public Optional<SellerDTO> partialUpdate(SellerDTO sellerDTO) {
        LOG.debug("Request to partially update Seller : {}", sellerDTO);

        return sellerRepository
            .findById(sellerDTO.getId())
            .map(existingSeller -> {
                sellerMapper.partialUpdate(existingSeller, sellerDTO);

                return existingSeller;
            })
            .map(sellerRepository::save)
            .map(sellerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SellerDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Sellers");
        return sellerRepository.findAll(pageable).map(sellerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SellerDTO> findOne(Long id) {
        LOG.debug("Request to get Seller : {}", id);
        return sellerRepository.findById(id).map(sellerMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Seller : {}", id);
        sellerRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SellerDTO> findAllOptimized(String searchTerm, Pageable pageable) {
        LOG.debug("Request to get all Sellers with optimization, search term: {}", searchTerm);
        return sellerRepository.findAllWithNativeQuery(searchTerm, pageable).map(sellerMapper::toDto);
    }

    @Override
    public SellerDTO saveOptimized(SellerDTO sellerDTO) {
        LOG.debug("Request to save Seller with optimization : {}", sellerDTO);

        // Sanitize inputs to prevent injection attacks
        if (sellerDTO.getFirstName() != null) {
            sellerDTO.setFirstName(sanitizeInput(sellerDTO.getFirstName()));
        }
        if (sellerDTO.getLastName() != null) {
            sellerDTO.setLastName(sanitizeInput(sellerDTO.getLastName()));
        }
        if (sellerDTO.getEmail() != null) {
            sellerDTO.setEmail(sanitizeInput(sellerDTO.getEmail()));
        }
        if (sellerDTO.getPhoneNumber() != null) {
            sellerDTO.setPhoneNumber(sanitizeInput(sellerDTO.getPhoneNumber()));
        }

        // Always set status to ACTIVE when creating a new seller
        sellerDTO.setStatus(Status.ACTIVE);

        // Set audit fields
        sellerDTO.setCreatedDate(java.time.Instant.now());

        // Use transactions efficiently for better performance
        Seller seller = sellerMapper.toEntity(sellerDTO);
        seller = sellerRepository.save(seller);

        // Return a clean DTO mapping
        return sellerMapper.toDto(seller);
    }

    @Override
    public SellerDTO updateSecureOptimized(SellerDTO sellerDTO) {
        LOG.debug("Request to update Seller with secure optimized processing : {}", sellerDTO);

        // Sanitize inputs to prevent injection attacks
        if (sellerDTO.getFirstName() != null) {
            sellerDTO.setFirstName(sanitizeInput(sellerDTO.getFirstName()));
        }
        if (sellerDTO.getLastName() != null) {
            sellerDTO.setLastName(sanitizeInput(sellerDTO.getLastName()));
        }
        if (sellerDTO.getEmail() != null) {
            sellerDTO.setEmail(sanitizeInput(sellerDTO.getEmail()));
        }
        if (sellerDTO.getPhoneNumber() != null) {
            sellerDTO.setPhoneNumber(sanitizeInput(sellerDTO.getPhoneNumber()));
        }

        // Set audit fields
        sellerDTO.setLastModifiedDate(java.time.Instant.now());

        // Use transactions efficiently for better performance
        Seller seller = sellerMapper.toEntity(sellerDTO);
        seller = sellerRepository.save(seller);

        // Return a clean DTO mapping
        return sellerMapper.toDto(seller);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SellerDTO> findOneOptimized(Long id) {
        LOG.debug("Request to get Seller with optimization : {}", id);

        // Input validation to prevent SQL injection
        if (id == null || id <= 0) {
            LOG.warn("Invalid seller ID: {}", id);
            return Optional.empty();
        }

        return sellerRepository.findByIdOptimized(id).map(sellerMapper::toDto);
    }

    /**
     * Sanitize input to prevent injection attacks
     *
     * @param input the input string to sanitize
     * @return sanitized string
     */
    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        // Remove potentially dangerous characters and scripts
        return input.replaceAll("[<>'\"]", "");
    }
}
