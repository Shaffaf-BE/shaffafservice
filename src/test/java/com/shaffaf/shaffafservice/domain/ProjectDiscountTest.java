package com.shaffaf.shaffafservice.domain;

import static com.shaffaf.shaffafservice.domain.ProjectDiscountTestSamples.*;
import static com.shaffaf.shaffafservice.domain.ProjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectDiscountTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectDiscount.class);
        ProjectDiscount projectDiscount1 = getProjectDiscountSample1();
        ProjectDiscount projectDiscount2 = new ProjectDiscount();
        assertThat(projectDiscount1).isNotEqualTo(projectDiscount2);

        projectDiscount2.setId(projectDiscount1.getId());
        assertThat(projectDiscount1).isEqualTo(projectDiscount2);

        projectDiscount2 = getProjectDiscountSample2();
        assertThat(projectDiscount1).isNotEqualTo(projectDiscount2);
    }

    @Test
    void projectTest() {
        ProjectDiscount projectDiscount = getProjectDiscountRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        projectDiscount.setProject(projectBack);
        assertThat(projectDiscount.getProject()).isEqualTo(projectBack);

        projectDiscount.project(null);
        assertThat(projectDiscount.getProject()).isNull();
    }
}
