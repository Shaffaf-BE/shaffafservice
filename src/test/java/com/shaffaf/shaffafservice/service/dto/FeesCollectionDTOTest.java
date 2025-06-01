package com.shaffaf.shaffafservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FeesCollectionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FeesCollectionDTO.class);
        FeesCollectionDTO feesCollectionDTO1 = new FeesCollectionDTO();
        feesCollectionDTO1.setId(1L);
        FeesCollectionDTO feesCollectionDTO2 = new FeesCollectionDTO();
        assertThat(feesCollectionDTO1).isNotEqualTo(feesCollectionDTO2);
        feesCollectionDTO2.setId(feesCollectionDTO1.getId());
        assertThat(feesCollectionDTO1).isEqualTo(feesCollectionDTO2);
        feesCollectionDTO2.setId(2L);
        assertThat(feesCollectionDTO1).isNotEqualTo(feesCollectionDTO2);
        feesCollectionDTO1.setId(null);
        assertThat(feesCollectionDTO1).isNotEqualTo(feesCollectionDTO2);
    }
}
