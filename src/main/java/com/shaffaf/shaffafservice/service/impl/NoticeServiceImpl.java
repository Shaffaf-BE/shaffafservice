package com.shaffaf.shaffafservice.service.impl;

import com.shaffaf.shaffafservice.domain.Notice;
import com.shaffaf.shaffafservice.repository.NoticeRepository;
import com.shaffaf.shaffafservice.service.NoticeService;
import com.shaffaf.shaffafservice.service.dto.NoticeDTO;
import com.shaffaf.shaffafservice.service.mapper.NoticeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shaffaf.shaffafservice.domain.Notice}.
 */
@Service
@Transactional
public class NoticeServiceImpl implements NoticeService {

    private static final Logger LOG = LoggerFactory.getLogger(NoticeServiceImpl.class);

    private final NoticeRepository noticeRepository;

    private final NoticeMapper noticeMapper;

    public NoticeServiceImpl(NoticeRepository noticeRepository, NoticeMapper noticeMapper) {
        this.noticeRepository = noticeRepository;
        this.noticeMapper = noticeMapper;
    }

    @Override
    public NoticeDTO save(NoticeDTO noticeDTO) {
        LOG.debug("Request to save Notice : {}", noticeDTO);
        Notice notice = noticeMapper.toEntity(noticeDTO);
        notice = noticeRepository.save(notice);
        return noticeMapper.toDto(notice);
    }

    @Override
    public NoticeDTO update(NoticeDTO noticeDTO) {
        LOG.debug("Request to update Notice : {}", noticeDTO);
        Notice notice = noticeMapper.toEntity(noticeDTO);
        notice = noticeRepository.save(notice);
        return noticeMapper.toDto(notice);
    }

    @Override
    public Optional<NoticeDTO> partialUpdate(NoticeDTO noticeDTO) {
        LOG.debug("Request to partially update Notice : {}", noticeDTO);

        return noticeRepository
            .findById(noticeDTO.getId())
            .map(existingNotice -> {
                noticeMapper.partialUpdate(existingNotice, noticeDTO);

                return existingNotice;
            })
            .map(noticeRepository::save)
            .map(noticeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NoticeDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Notices");
        return noticeRepository.findAll(pageable).map(noticeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NoticeDTO> findOne(Long id) {
        LOG.debug("Request to get Notice : {}", id);
        return noticeRepository.findById(id).map(noticeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Notice : {}", id);
        noticeRepository.deleteById(id);
    }
}
