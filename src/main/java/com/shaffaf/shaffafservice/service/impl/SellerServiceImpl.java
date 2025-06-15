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

    @Override
    @Transactional
    public SellerDTO saveWithNativeQuery(SellerDTO sellerDTO) {
        LOG.debug("Request to save Seller with native query: {}", sellerDTO);

        // Sanitize inputs to prevent injection attacks
        String firstName = sanitizeInput(sellerDTO.getFirstName());
        String lastName = sanitizeInput(sellerDTO.getLastName());
        String email = sanitizeInput(sellerDTO.getEmail());
        String phoneNumber = sanitizeInput(sellerDTO.getPhoneNumber());

        // Always set status to ACTIVE when creating a new seller
        Status status = Status.ACTIVE;

        // Set audit fields
        sellerDTO.setCreatedDate(java.time.Instant.now());
        String createdBy = "system"; // This should be replaced with the current authenticated user

        // Execute native SQL query with PostgreSQL's RETURNING clause to get the ID in one step
        Long newId = sellerRepository.saveWithNativeQueryReturningId(
            firstName,
            lastName,
            email,
            phoneNumber,
            status.toString(),
            createdBy,
            sellerDTO.getCreatedDate()
        );

        if (newId == null) {
            LOG.error("Failed to create new seller, no ID returned");
            throw new RuntimeException("Failed to create seller - database did not return an ID");
        }

        // Fetch the complete seller entity to return
        return sellerMapper.toDto(
            sellerRepository
                .findByIdOptimized(newId)
                .orElseThrow(() -> {
                    LOG.error("Failed to retrieve newly created seller with ID: {}", newId);
                    return new RuntimeException("Failed to retrieve newly created seller");
                })
        );
    }

    @Override
    @Transactional
    public SellerDTO updateWithNativeQuery(SellerDTO sellerDTO) {
        LOG.debug("Request to update Seller with native query: {}", sellerDTO);

        // Input validation
        if (sellerDTO.getId() == null) {
            LOG.error("Cannot update seller with null ID");
            throw new IllegalArgumentException("ID cannot be null for update operation");
        }

        // Validate seller exists before attempting update
        if (!sellerRepository.existsById(sellerDTO.getId())) {
            LOG.error("Seller with ID {} does not exist", sellerDTO.getId());
            throw new IllegalArgumentException("Seller not found with ID: " + sellerDTO.getId());
        }

        // Validate phone number format
        if (sellerDTO.getPhoneNumber() != null && !sellerDTO.getPhoneNumber().matches("^\\+[0-9]{11,12}$")) {
            LOG.error("Invalid phone number format: {}", sellerDTO.getPhoneNumber());
            throw new IllegalArgumentException("Phone number must be in format +923311234569");
        }

        // Sanitize inputs
        String firstName = sanitizeInput(sellerDTO.getFirstName());
        String lastName = sanitizeInput(sellerDTO.getLastName());
        String email = sanitizeInput(sellerDTO.getEmail());
        String phoneNumber = sanitizeInput(sellerDTO.getPhoneNumber());

        // Set audit fields
        sellerDTO.setLastModifiedDate(java.time.Instant.now());
        String lastModifiedBy = "system"; // This should be replaced with the current authenticated user

        // Execute native SQL query
        int rowsAffected = sellerRepository.updateWithNativeQuery(
            sellerDTO.getId(),
            firstName,
            lastName,
            email,
            phoneNumber,
            sellerDTO.getStatus().toString(),
            lastModifiedBy,
            sellerDTO.getLastModifiedDate()
        );

        if (rowsAffected == 0) {
            LOG.error("Failed to update seller with ID: {}", sellerDTO.getId());
            throw new RuntimeException("Failed to update seller. The seller may have been deleted.");
        }

        // Fetch the updated entity to return
        return sellerMapper.toDto(
            sellerRepository
                .findByIdOptimized(sellerDTO.getId())
                .orElseThrow(() -> {
                    LOG.error("Failed to retrieve updated seller with ID: {}", sellerDTO.getId());
                    return new RuntimeException("Failed to retrieve updated seller");
                })
        );
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
