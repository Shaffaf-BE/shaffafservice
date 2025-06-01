package com.shaffaf.shaffafservice.service.mapper;

import static com.shaffaf.shaffafservice.domain.ComplainAsserts.*;
import static com.shaffaf.shaffafservice.domain.ComplainTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ComplainMapperTest {

    private ComplainMapper complainMapper;

    @BeforeEach
    void setUp() {
        complainMapper = new ComplainMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getComplainSample1();
        var actual = complainMapper.toEntity(complainMapper.toDto(expected));
        assertComplainAllPropertiesEquals(expected, actual);
    }
}
