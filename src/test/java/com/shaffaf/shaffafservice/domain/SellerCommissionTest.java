package com.shaffaf.shaffafservice.domain;

import static com.shaffaf.shaffafservice.domain.ProjectTestSamples.*;
import static com.shaffaf.shaffafservice.domain.SellerCommissionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SellerCommissionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SellerCommission.class);
        SellerCommission sellerCommission1 = getSellerCommissionSample1();
        SellerCommission sellerCommission2 = new SellerCommission();
        assertThat(sellerCommission1).isNotEqualTo(sellerCommission2);

        sellerCommission2.setId(sellerCommission1.getId());
        assertThat(sellerCommission1).isEqualTo(sellerCommission2);

        sellerCommission2 = getSellerCommissionSample2();
        assertThat(sellerCommission1).isNotEqualTo(sellerCommission2);
    }

    @Test
    void projectTest() {
        SellerCommission sellerCommission = getSellerCommissionRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        sellerCommission.setProject(projectBack);
        assertThat(sellerCommission.getProject()).isEqualTo(projectBack);

        sellerCommission.project(null);
        assertThat(sellerCommission.getProject()).isNull();
    }
}
