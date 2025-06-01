package com.shaffaf.shaffafservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FeesConfigurationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FeesConfigurationDTO.class);
        FeesConfigurationDTO feesConfigurationDTO1 = new FeesConfigurationDTO();
        feesConfigurationDTO1.setId(1L);
        FeesConfigurationDTO feesConfigurationDTO2 = new FeesConfigurationDTO();
        assertThat(feesConfigurationDTO1).isNotEqualTo(feesConfigurationDTO2);
        feesConfigurationDTO2.setId(feesConfigurationDTO1.getId());
        assertThat(feesConfigurationDTO1).isEqualTo(feesConfigurationDTO2);
        feesConfigurationDTO2.setId(2L);
        assertThat(feesConfigurationDTO1).isNotEqualTo(feesConfigurationDTO2);
        feesConfigurationDTO1.setId(null);
        assertThat(feesConfigurationDTO1).isNotEqualTo(feesConfigurationDTO2);
    }
}
