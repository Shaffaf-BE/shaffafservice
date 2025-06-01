package com.shaffaf.shaffafservice.service.mapper;

import static com.shaffaf.shaffafservice.domain.FeesConfigurationAsserts.*;
import static com.shaffaf.shaffafservice.domain.FeesConfigurationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FeesConfigurationMapperTest {

    private FeesConfigurationMapper feesConfigurationMapper;

    @BeforeEach
    void setUp() {
        feesConfigurationMapper = new FeesConfigurationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFeesConfigurationSample1();
        var actual = feesConfigurationMapper.toEntity(feesConfigurationMapper.toDto(expected));
        assertFeesConfigurationAllPropertiesEquals(expected, actual);
    }
}
