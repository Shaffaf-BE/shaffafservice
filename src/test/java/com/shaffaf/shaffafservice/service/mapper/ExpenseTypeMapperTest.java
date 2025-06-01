package com.shaffaf.shaffafservice.service.mapper;

import static com.shaffaf.shaffafservice.domain.ExpenseTypeAsserts.*;
import static com.shaffaf.shaffafservice.domain.ExpenseTypeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExpenseTypeMapperTest {

    private ExpenseTypeMapper expenseTypeMapper;

    @BeforeEach
    void setUp() {
        expenseTypeMapper = new ExpenseTypeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getExpenseTypeSample1();
        var actual = expenseTypeMapper.toEntity(expenseTypeMapper.toDto(expected));
        assertExpenseTypeAllPropertiesEquals(expected, actual);
    }
}
