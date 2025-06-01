package com.shaffaf.shaffafservice.domain;

import static com.shaffaf.shaffafservice.domain.ExpenseTestSamples.*;
import static com.shaffaf.shaffafservice.domain.ExpenseTypeTestSamples.*;
import static com.shaffaf.shaffafservice.domain.ProjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ExpenseTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExpenseType.class);
        ExpenseType expenseType1 = getExpenseTypeSample1();
        ExpenseType expenseType2 = new ExpenseType();
        assertThat(expenseType1).isNotEqualTo(expenseType2);

        expenseType2.setId(expenseType1.getId());
        assertThat(expenseType1).isEqualTo(expenseType2);

        expenseType2 = getExpenseTypeSample2();
        assertThat(expenseType1).isNotEqualTo(expenseType2);
    }

    @Test
    void expensesTest() {
        ExpenseType expenseType = getExpenseTypeRandomSampleGenerator();
        Expense expenseBack = getExpenseRandomSampleGenerator();

        expenseType.addExpenses(expenseBack);
        assertThat(expenseType.getExpenses()).containsOnly(expenseBack);
        assertThat(expenseBack.getExpenseType()).isEqualTo(expenseType);

        expenseType.removeExpenses(expenseBack);
        assertThat(expenseType.getExpenses()).doesNotContain(expenseBack);
        assertThat(expenseBack.getExpenseType()).isNull();

        expenseType.expenses(new HashSet<>(Set.of(expenseBack)));
        assertThat(expenseType.getExpenses()).containsOnly(expenseBack);
        assertThat(expenseBack.getExpenseType()).isEqualTo(expenseType);

        expenseType.setExpenses(new HashSet<>());
        assertThat(expenseType.getExpenses()).doesNotContain(expenseBack);
        assertThat(expenseBack.getExpenseType()).isNull();
    }

    @Test
    void projectTest() {
        ExpenseType expenseType = getExpenseTypeRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        expenseType.setProject(projectBack);
        assertThat(expenseType.getProject()).isEqualTo(projectBack);

        expenseType.project(null);
        assertThat(expenseType.getProject()).isNull();
    }
}
