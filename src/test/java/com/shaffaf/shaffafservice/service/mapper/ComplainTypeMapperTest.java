package com.shaffaf.shaffafservice.service.mapper;

import static com.shaffaf.shaffafservice.domain.ComplainTypeAsserts.*;
import static com.shaffaf.shaffafservice.domain.ComplainTypeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ComplainTypeMapperTest {

    private ComplainTypeMapper complainTypeMapper;

    @BeforeEach
    void setUp() {
        complainTypeMapper = new ComplainTypeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getComplainTypeSample1();
        var actual = complainTypeMapper.toEntity(complainTypeMapper.toDto(expected));
        assertComplainTypeAllPropertiesEquals(expected, actual);
    }
}
