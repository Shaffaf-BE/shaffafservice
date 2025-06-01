package com.shaffaf.shaffafservice.service.mapper;

import static com.shaffaf.shaffafservice.domain.SellerCommissionAsserts.*;
import static com.shaffaf.shaffafservice.domain.SellerCommissionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SellerCommissionMapperTest {

    private SellerCommissionMapper sellerCommissionMapper;

    @BeforeEach
    void setUp() {
        sellerCommissionMapper = new SellerCommissionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSellerCommissionSample1();
        var actual = sellerCommissionMapper.toEntity(sellerCommissionMapper.toDto(expected));
        assertSellerCommissionAllPropertiesEquals(expected, actual);
    }
}
