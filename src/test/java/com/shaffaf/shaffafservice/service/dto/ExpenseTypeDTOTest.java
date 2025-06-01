package com.shaffaf.shaffafservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExpenseTypeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExpenseTypeDTO.class);
        ExpenseTypeDTO expenseTypeDTO1 = new ExpenseTypeDTO();
        expenseTypeDTO1.setId(1L);
        ExpenseTypeDTO expenseTypeDTO2 = new ExpenseTypeDTO();
        assertThat(expenseTypeDTO1).isNotEqualTo(expenseTypeDTO2);
        expenseTypeDTO2.setId(expenseTypeDTO1.getId());
        assertThat(expenseTypeDTO1).isEqualTo(expenseTypeDTO2);
        expenseTypeDTO2.setId(2L);
        assertThat(expenseTypeDTO1).isNotEqualTo(expenseTypeDTO2);
        expenseTypeDTO1.setId(null);
        assertThat(expenseTypeDTO1).isNotEqualTo(expenseTypeDTO2);
    }
}
