package com.shaffaf.shaffafservice.service.mapper;

import static com.shaffaf.shaffafservice.domain.ExpenseAsserts.*;
import static com.shaffaf.shaffafservice.domain.ExpenseTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExpenseMapperTest {

    private ExpenseMapper expenseMapper;

    @BeforeEach
    void setUp() {
        expenseMapper = new ExpenseMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getExpenseSample1();
        var actual = expenseMapper.toEntity(expenseMapper.toDto(expected));
        assertExpenseAllPropertiesEquals(expected, actual);
    }
}
