package com.shaffaf.shaffafservice.service.mapper;

import static com.shaffaf.shaffafservice.domain.BlockAsserts.*;
import static com.shaffaf.shaffafservice.domain.BlockTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BlockMapperTest {

    private BlockMapper blockMapper;

    @BeforeEach
    void setUp() {
        blockMapper = new BlockMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBlockSample1();
        var actual = blockMapper.toEntity(blockMapper.toDto(expected));
        assertBlockAllPropertiesEquals(expected, actual);
    }
}
