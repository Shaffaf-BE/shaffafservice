package com.shaffaf.shaffafservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ComplainDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ComplainDTO.class);
        ComplainDTO complainDTO1 = new ComplainDTO();
        complainDTO1.setId(1L);
        ComplainDTO complainDTO2 = new ComplainDTO();
        assertThat(complainDTO1).isNotEqualTo(complainDTO2);
        complainDTO2.setId(complainDTO1.getId());
        assertThat(complainDTO1).isEqualTo(complainDTO2);
        complainDTO2.setId(2L);
        assertThat(complainDTO1).isNotEqualTo(complainDTO2);
        complainDTO1.setId(null);
        assertThat(complainDTO1).isNotEqualTo(complainDTO2);
    }
}
