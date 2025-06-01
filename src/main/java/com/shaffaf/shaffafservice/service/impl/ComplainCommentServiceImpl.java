package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.ComplainComment;
import com.shaffaf.shaffafservice.repository.ComplainCommentRepository;
import com.shaffaf.shaffafservice.service.ComplainCommentService;
import com.shaffaf.shaffafservice.service.dto.ComplainCommentDTO;
import com.shaffaf.shaffafservice.service.mapper.ComplainCommentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shaffaf.shaffafservice.domain.ComplainComment}.
 */
@Service
@Transactional
public class ComplainCommentServiceImpl implements ComplainCommentService {

    private static final Logger LOG = LoggerFactory.getLogger(ComplainCommentServiceImpl.class);

    private final ComplainCommentRepository complainCommentRepository;

    private final ComplainCommentMapper complainCommentMapper;

    public ComplainCommentServiceImpl(ComplainCommentRepository complainCommentRepository, ComplainCommentMapper complainCommentMapper) {
        this.complainCommentRepository = complainCommentRepository;
        this.complainCommentMapper = complainCommentMapper;
    }

    @Override
    public ComplainCommentDTO save(ComplainCommentDTO complainCommentDTO) {
        LOG.debug("Request to save ComplainComment : {}", complainCommentDTO);
        ComplainComment complainComment = complainCommentMapper.toEntity(complainCommentDTO);
        complainComment = complainCommentRepository.save(complainComment);
        return complainCommentMapper.toDto(complainComment);
    }

    @Override
    public ComplainCommentDTO update(ComplainCommentDTO complainCommentDTO) {
        LOG.debug("Request to update ComplainComment : {}", complainCommentDTO);
        ComplainComment complainComment = complainCommentMapper.toEntity(complainCommentDTO);
        complainComment = complainCommentRepository.save(complainComment);
        return complainCommentMapper.toDto(complainComment);
    }

    @Override
    public Optional<ComplainCommentDTO> partialUpdate(ComplainCommentDTO complainCommentDTO) {
        LOG.debug("Request to partially update ComplainComment : {}", complainCommentDTO);

        return complainCommentRepository
            .findById(complainCommentDTO.getId())
            .map(existingComplainComment -> {
                complainCommentMapper.partialUpdate(existingComplainComment, complainCommentDTO);

                return existingComplainComment;
            })
            .map(complainCommentRepository::save)
            .map(complainCommentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComplainCommentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ComplainComments");
        return complainCommentRepository.findAll(pageable).map(complainCommentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ComplainCommentDTO> findOne(Long id) {
        LOG.debug("Request to get ComplainComment : {}", id);
        return complainCommentRepository.findById(id).map(complainCommentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ComplainComment : {}", id);
        complainCommentRepository.deleteById(id);
    }
}
