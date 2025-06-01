package com.shaffaf.shaffafservice.service.mapper;

import static com.shaffaf.shaffafservice.domain.NoticeAsserts.*;
import static com.shaffaf.shaffafservice.domain.NoticeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NoticeMapperTest {

    private NoticeMapper noticeMapper;

    @BeforeEach
    void setUp() {
        noticeMapper = new NoticeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getNoticeSample1();
        var actual = noticeMapper.toEntity(noticeMapper.toDto(expected));
        assertNoticeAllPropertiesEquals(expected, actual);
    }
}
