package com.shaffaf.shaffafservice.service.mapper;

import static com.shaffaf.shaffafservice.domain.UnitAsserts.*;
import static com.shaffaf.shaffafservice.domain.UnitTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UnitMapperTest {

    private UnitMapper unitMapper;

    @BeforeEach
    void setUp() {
        unitMapper = new UnitMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUnitSample1();
        var actual = unitMapper.toEntity(unitMapper.toDto(expected));
        assertUnitAllPropertiesEquals(expected, actual);
    }
}
