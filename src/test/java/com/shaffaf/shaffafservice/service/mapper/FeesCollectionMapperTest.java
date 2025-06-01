package com.shaffaf.shaffafservice.service.mapper;

import static com.shaffaf.shaffafservice.domain.FeesCollectionAsserts.*;
import static com.shaffaf.shaffafservice.domain.FeesCollectionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FeesCollectionMapperTest {

    private FeesCollectionMapper feesCollectionMapper;

    @BeforeEach
    void setUp() {
        feesCollectionMapper = new FeesCollectionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFeesCollectionSample1();
        var actual = feesCollectionMapper.toEntity(feesCollectionMapper.toDto(expected));
        assertFeesCollectionAllPropertiesEquals(expected, actual);
    }
}
