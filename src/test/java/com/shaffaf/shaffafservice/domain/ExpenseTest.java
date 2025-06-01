package com.shaffaf.shaffafservice.domain;

import static com.shaffaf.shaffafservice.domain.ExpenseTestSamples.*;
import static com.shaffaf.shaffafservice.domain.ExpenseTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExpenseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Expense.class);
        Expense expense1 = getExpenseSample1();
        Expense expense2 = new Expense();
        assertThat(expense1).isNotEqualTo(expense2);

        expense2.setId(expense1.getId());
        assertThat(expense1).isEqualTo(expense2);

        expense2 = getExpenseSample2();
        assertThat(expense1).isNotEqualTo(expense2);
    }

    @Test
    void expenseTypeTest() {
        Expense expense = getExpenseRandomSampleGenerator();
        ExpenseType expenseTypeBack = getExpenseTypeRandomSampleGenerator();

        expense.setExpenseType(expenseTypeBack);
        assertThat(expense.getExpenseType()).isEqualTo(expenseTypeBack);

        expense.expenseType(null);
        assertThat(expense.getExpenseType()).isNull();
    }
}
