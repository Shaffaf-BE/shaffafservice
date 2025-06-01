package com.shaffaf.shaffafservice.domain;

import static com.shaffaf.shaffafservice.domain.ProjectTestSamples.*;
import static com.shaffaf.shaffafservice.domain.SellerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SellerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Seller.class);
        Seller seller1 = getSellerSample1();
        Seller seller2 = new Seller();
        assertThat(seller1).isNotEqualTo(seller2);

        seller2.setId(seller1.getId());
        assertThat(seller1).isEqualTo(seller2);

        seller2 = getSellerSample2();
        assertThat(seller1).isNotEqualTo(seller2);
    }

    @Test
    void projectsTest() {
        Seller seller = getSellerRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        seller.addProjects(projectBack);
        assertThat(seller.getProjects()).containsOnly(projectBack);
        assertThat(projectBack.getSeller()).isEqualTo(seller);

        seller.removeProjects(projectBack);
        assertThat(seller.getProjects()).doesNotContain(projectBack);
        assertThat(projectBack.getSeller()).isNull();

        seller.projects(new HashSet<>(Set.of(projectBack)));
        assertThat(seller.getProjects()).containsOnly(projectBack);
        assertThat(projectBack.getSeller()).isEqualTo(seller);

        seller.setProjects(new HashSet<>());
        assertThat(seller.getProjects()).doesNotContain(projectBack);
        assertThat(projectBack.getSeller()).isNull();
    }
}
