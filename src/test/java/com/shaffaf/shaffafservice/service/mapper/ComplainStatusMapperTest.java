package com.shaffaf.shaffafservice.service.mapper;

import static com.shaffaf.shaffafservice.domain.ComplainStatusAsserts.*;
import static com.shaffaf.shaffafservice.domain.ComplainStatusTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ComplainStatusMapperTest {

    private ComplainStatusMapper complainStatusMapper;

    @BeforeEach
    void setUp() {
        complainStatusMapper = new ComplainStatusMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getComplainStatusSample1();
        var actual = complainStatusMapper.toEntity(complainStatusMapper.toDto(expected));
        assertComplainStatusAllPropertiesEquals(expected, actual);
    }
}
