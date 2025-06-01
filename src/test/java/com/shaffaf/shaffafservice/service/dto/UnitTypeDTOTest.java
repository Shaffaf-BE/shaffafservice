package com.shaffaf.shaffafservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UnitTypeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UnitTypeDTO.class);
        UnitTypeDTO unitTypeDTO1 = new UnitTypeDTO();
        unitTypeDTO1.setId(1L);
        UnitTypeDTO unitTypeDTO2 = new UnitTypeDTO();
        assertThat(unitTypeDTO1).isNotEqualTo(unitTypeDTO2);
        unitTypeDTO2.setId(unitTypeDTO1.getId());
        assertThat(unitTypeDTO1).isEqualTo(unitTypeDTO2);
        unitTypeDTO2.setId(2L);
        assertThat(unitTypeDTO1).isNotEqualTo(unitTypeDTO2);
        unitTypeDTO1.setId(null);
        assertThat(unitTypeDTO1).isNotEqualTo(unitTypeDTO2);
    }
}
