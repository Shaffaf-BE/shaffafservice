package com.shaffaf.shaffafservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SellerCommissionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SellerCommissionDTO.class);
        SellerCommissionDTO sellerCommissionDTO1 = new SellerCommissionDTO();
        sellerCommissionDTO1.setId(1L);
        SellerCommissionDTO sellerCommissionDTO2 = new SellerCommissionDTO();
        assertThat(sellerCommissionDTO1).isNotEqualTo(sellerCommissionDTO2);
        sellerCommissionDTO2.setId(sellerCommissionDTO1.getId());
        assertThat(sellerCommissionDTO1).isEqualTo(sellerCommissionDTO2);
        sellerCommissionDTO2.setId(2L);
        assertThat(sellerCommissionDTO1).isNotEqualTo(sellerCommissionDTO2);
        sellerCommissionDTO1.setId(null);
        assertThat(sellerCommissionDTO1).isNotEqualTo(sellerCommissionDTO2);
    }
}
