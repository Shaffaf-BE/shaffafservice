package com.shaffaf.shaffafservice.service.mapper;

import static com.shaffaf.shaffafservice.domain.ResidentTypeAsserts.*;
import static com.shaffaf.shaffafservice.domain.ResidentTypeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResidentTypeMapperTest {

    private ResidentTypeMapper residentTypeMapper;

    @BeforeEach
    void setUp() {
        residentTypeMapper = new ResidentTypeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getResidentTypeSample1();
        var actual = residentTypeMapper.toEntity(residentTypeMapper.toDto(expected));
        assertResidentTypeAllPropertiesEquals(expected, actual);
    }
}
