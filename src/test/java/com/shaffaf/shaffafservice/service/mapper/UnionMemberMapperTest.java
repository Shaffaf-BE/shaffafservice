package com.shaffaf.shaffafservice.service.mapper;

import static com.shaffaf.shaffafservice.domain.UnionMemberAsserts.*;
import static com.shaffaf.shaffafservice.domain.UnionMemberTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UnionMemberMapperTest {

    private UnionMemberMapper unionMemberMapper;

    @BeforeEach
    void setUp() {
        unionMemberMapper = new UnionMemberMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUnionMemberSample1();
        var actual = unionMemberMapper.toEntity(unionMemberMapper.toDto(expected));
        assertUnionMemberAllPropertiesEquals(expected, actual);
    }
}
