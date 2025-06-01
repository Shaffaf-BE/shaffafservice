package com.shaffaf.shaffafservice.service.mapper;

import static com.shaffaf.shaffafservice.domain.ProjectDiscountAsserts.*;
import static com.shaffaf.shaffafservice.domain.ProjectDiscountTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectDiscountMapperTest {

    private ProjectDiscountMapper projectDiscountMapper;

    @BeforeEach
    void setUp() {
        projectDiscountMapper = new ProjectDiscountMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProjectDiscountSample1();
        var actual = projectDiscountMapper.toEntity(projectDiscountMapper.toDto(expected));
        assertProjectDiscountAllPropertiesEquals(expected, actual);
    }
}
