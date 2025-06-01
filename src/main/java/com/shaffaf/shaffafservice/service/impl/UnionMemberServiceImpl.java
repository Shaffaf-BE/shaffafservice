package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.UnionMember;
import com.shaffaf.shaffafservice.repository.UnionMemberRepository;
import com.shaffaf.shaffafservice.service.UnionMemberService;
import com.shaffaf.shaffafservice.service.dto.UnionMemberDTO;
import com.shaffaf.shaffafservice.service.mapper.UnionMemberMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shaffaf.shaffafservice.domain.UnionMember}.
 */
@Service
@Transactional
public class UnionMemberServiceImpl implements UnionMemberService {

    private static final Logger LOG = LoggerFactory.getLogger(UnionMemberServiceImpl.class);

    private final UnionMemberRepository unionMemberRepository;

    private final UnionMemberMapper unionMemberMapper;

    public UnionMemberServiceImpl(UnionMemberRepository unionMemberRepository, UnionMemberMapper unionMemberMapper) {
        this.unionMemberRepository = unionMemberRepository;
        this.unionMemberMapper = unionMemberMapper;
    }

    @Override
    public UnionMemberDTO save(UnionMemberDTO unionMemberDTO) {
        LOG.debug("Request to save UnionMember : {}", unionMemberDTO);
        UnionMember unionMember = unionMemberMapper.toEntity(unionMemberDTO);
        unionMember = unionMemberRepository.save(unionMember);
        return unionMemberMapper.toDto(unionMember);
    }

    @Override
    public UnionMemberDTO update(UnionMemberDTO unionMemberDTO) {
        LOG.debug("Request to update UnionMember : {}", unionMemberDTO);
        UnionMember unionMember = unionMemberMapper.toEntity(unionMemberDTO);
        unionMember = unionMemberRepository.save(unionMember);
        return unionMemberMapper.toDto(unionMember);
    }

    @Override
    public Optional<UnionMemberDTO> partialUpdate(UnionMemberDTO unionMemberDTO) {
        LOG.debug("Request to partially update UnionMember : {}", unionMemberDTO);

        return unionMemberRepository
            .findById(unionMemberDTO.getId())
            .map(existingUnionMember -> {
                unionMemberMapper.partialUpdate(existingUnionMember, unionMemberDTO);

                return existingUnionMember;
            })
            .map(unionMemberRepository::save)
            .map(unionMemberMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UnionMemberDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all UnionMembers");
        return unionMemberRepository.findAll(pageable).map(unionMemberMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UnionMemberDTO> findOne(Long id) {
        LOG.debug("Request to get UnionMember : {}", id);
        return unionMemberRepository.findById(id).map(unionMemberMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete UnionMember : {}", id);
        unionMemberRepository.deleteById(id);
    }
}
