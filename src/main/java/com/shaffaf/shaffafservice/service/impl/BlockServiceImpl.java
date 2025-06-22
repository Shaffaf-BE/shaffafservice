package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.repository.jdbctemplate.BlockJdbcRepository;
import com.shaffaf.shaffafservice.security.SecurityUtils;
import com.shaffaf.shaffafservice.service.BlockService;
import com.shaffaf.shaffafservice.service.dto.BlockDTO;
import com.shaffaf.shaffafservice.service.mapper.BlockMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shaffaf.shaffafservice.domain.Block}.
 */
@Service
@Transactional
public class BlockServiceImpl implements BlockService {

    private static final Logger LOG = LoggerFactory.getLogger(BlockServiceImpl.class);

    private final BlockJdbcRepository blockJdbcRepository;

    private final BlockMapper blockMapper;

    public BlockServiceImpl(BlockMapper blockMapper, BlockJdbcRepository blockJdbcRepository) {
        this.blockMapper = blockMapper;
        this.blockJdbcRepository = blockJdbcRepository;
    }

    @Override
    @Transactional
    public BlockDTO save(BlockDTO blockDTO) {
        LOG.debug("Request to save Block : {}", blockDTO);

        String username = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("Current user login not found"));

        Long id = blockJdbcRepository.saveBlock(blockDTO.getName(), blockDTO.getProject().getId(), username);
        blockDTO.setId(id);
        return blockDTO;
    }

    @Override
    @Transactional
    public BlockDTO update(BlockDTO blockDTO) {
        LOG.debug("Request to update Block : {}", blockDTO);

        String username = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("Current user login not found"));

        blockJdbcRepository.updateBlock(
            blockDTO.getId(),
            blockDTO.getName(),
            blockDTO.getProject().getId(),
            username,
            blockDTO.getDeletedOn()
        );

        return blockDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BlockDTO> findOne(Long id) {
        LOG.debug("Request to get Block : {}", id);
        return blockJdbcRepository.findById(id).map(blockMapper::toDto);
    }

    @Override
    public Page<BlockDTO> findAllByProjectId(Long projectId, Pageable pageable) {
        LOG.debug("Request to get all Blocks by projectId : {}", projectId);
        return blockJdbcRepository.findAllByProjectId(projectId, pageable).map(blockMapper::toDto);
    }
}
