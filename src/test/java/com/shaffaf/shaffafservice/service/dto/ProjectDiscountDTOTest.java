package com.shaffaf.shaffafservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectDiscountDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectDiscountDTO.class);
        ProjectDiscountDTO projectDiscountDTO1 = new ProjectDiscountDTO();
        projectDiscountDTO1.setId(1L);
        ProjectDiscountDTO projectDiscountDTO2 = new ProjectDiscountDTO();
        assertThat(projectDiscountDTO1).isNotEqualTo(projectDiscountDTO2);
        projectDiscountDTO2.setId(projectDiscountDTO1.getId());
        assertThat(projectDiscountDTO1).isEqualTo(projectDiscountDTO2);
        projectDiscountDTO2.setId(2L);
        assertThat(projectDiscountDTO1).isNotEqualTo(projectDiscountDTO2);
        projectDiscountDTO1.setId(null);
        assertThat(projectDiscountDTO1).isNotEqualTo(projectDiscountDTO2);
    }
}
