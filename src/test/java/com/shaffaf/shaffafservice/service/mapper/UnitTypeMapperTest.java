package com.shaffaf.shaffafservice.service.mapper;

import static com.shaffaf.shaffafservice.domain.UnitTypeAsserts.*;
import static com.shaffaf.shaffafservice.domain.UnitTypeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UnitTypeMapperTest {

    private UnitTypeMapper unitTypeMapper;

    @BeforeEach
    void setUp() {
        unitTypeMapper = new UnitTypeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUnitTypeSample1();
        var actual = unitTypeMapper.toEntity(unitTypeMapper.toDto(expected));
        assertUnitTypeAllPropertiesEquals(expected, actual);
    }
}
