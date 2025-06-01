package com.shaffaf.shaffafservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ComplainTypeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ComplainTypeDTO.class);
        ComplainTypeDTO complainTypeDTO1 = new ComplainTypeDTO();
        complainTypeDTO1.setId(1L);
        ComplainTypeDTO complainTypeDTO2 = new ComplainTypeDTO();
        assertThat(complainTypeDTO1).isNotEqualTo(complainTypeDTO2);
        complainTypeDTO2.setId(complainTypeDTO1.getId());
        assertThat(complainTypeDTO1).isEqualTo(complainTypeDTO2);
        complainTypeDTO2.setId(2L);
        assertThat(complainTypeDTO1).isNotEqualTo(complainTypeDTO2);
        complainTypeDTO1.setId(null);
        assertThat(complainTypeDTO1).isNotEqualTo(complainTypeDTO2);
    }
}
