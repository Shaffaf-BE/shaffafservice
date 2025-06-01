package com.shaffaf.shaffafservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ResidentTypeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ResidentTypeDTO.class);
        ResidentTypeDTO residentTypeDTO1 = new ResidentTypeDTO();
        residentTypeDTO1.setId(1L);
        ResidentTypeDTO residentTypeDTO2 = new ResidentTypeDTO();
        assertThat(residentTypeDTO1).isNotEqualTo(residentTypeDTO2);
        residentTypeDTO2.setId(residentTypeDTO1.getId());
        assertThat(residentTypeDTO1).isEqualTo(residentTypeDTO2);
        residentTypeDTO2.setId(2L);
        assertThat(residentTypeDTO1).isNotEqualTo(residentTypeDTO2);
        residentTypeDTO1.setId(null);
        assertThat(residentTypeDTO1).isNotEqualTo(residentTypeDTO2);
    }
}
