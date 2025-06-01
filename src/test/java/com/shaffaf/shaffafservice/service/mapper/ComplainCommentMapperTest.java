package com.shaffaf.shaffafservice.service.mapper;

import static com.shaffaf.shaffafservice.domain.ComplainCommentAsserts.*;
import static com.shaffaf.shaffafservice.domain.ComplainCommentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ComplainCommentMapperTest {

    private ComplainCommentMapper complainCommentMapper;

    @BeforeEach
    void setUp() {
        complainCommentMapper = new ComplainCommentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getComplainCommentSample1();
        var actual = complainCommentMapper.toEntity(complainCommentMapper.toDto(expected));
        assertComplainCommentAllPropertiesEquals(expected, actual);
    }
}
