package com.shaffaf.shaffafservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ComplainStatusDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ComplainStatusDTO.class);
        ComplainStatusDTO complainStatusDTO1 = new ComplainStatusDTO();
        complainStatusDTO1.setId(1L);
        ComplainStatusDTO complainStatusDTO2 = new ComplainStatusDTO();
        assertThat(complainStatusDTO1).isNotEqualTo(complainStatusDTO2);
        complainStatusDTO2.setId(complainStatusDTO1.getId());
        assertThat(complainStatusDTO1).isEqualTo(complainStatusDTO2);
        complainStatusDTO2.setId(2L);
        assertThat(complainStatusDTO1).isNotEqualTo(complainStatusDTO2);
        complainStatusDTO1.setId(null);
        assertThat(complainStatusDTO1).isNotEqualTo(complainStatusDTO2);
    }
}
