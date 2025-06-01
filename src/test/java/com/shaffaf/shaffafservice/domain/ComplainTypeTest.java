package com.shaffaf.shaffafservice.domain;

import static com.shaffaf.shaffafservice.domain.ComplainTestSamples.*;
import static com.shaffaf.shaffafservice.domain.ComplainTypeTestSamples.*;
import static com.shaffaf.shaffafservice.domain.ProjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ComplainTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ComplainType.class);
        ComplainType complainType1 = getComplainTypeSample1();
        ComplainType complainType2 = new ComplainType();
        assertThat(complainType1).isNotEqualTo(complainType2);

        complainType2.setId(complainType1.getId());
        assertThat(complainType1).isEqualTo(complainType2);

        complainType2 = getComplainTypeSample2();
        assertThat(complainType1).isNotEqualTo(complainType2);
    }

    @Test
    void complainsTest() {
        ComplainType complainType = getComplainTypeRandomSampleGenerator();
        Complain complainBack = getComplainRandomSampleGenerator();

        complainType.addComplains(complainBack);
        assertThat(complainType.getComplains()).containsOnly(complainBack);
        assertThat(complainBack.getComplainType()).isEqualTo(complainType);

        complainType.removeComplains(complainBack);
        assertThat(complainType.getComplains()).doesNotContain(complainBack);
        assertThat(complainBack.getComplainType()).isNull();

        complainType.complains(new HashSet<>(Set.of(complainBack)));
        assertThat(complainType.getComplains()).containsOnly(complainBack);
        assertThat(complainBack.getComplainType()).isEqualTo(complainType);

        complainType.setComplains(new HashSet<>());
        assertThat(complainType.getComplains()).doesNotContain(complainBack);
        assertThat(complainBack.getComplainType()).isNull();
    }

    @Test
    void projectTest() {
        ComplainType complainType = getComplainTypeRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        complainType.setProject(projectBack);
        assertThat(complainType.getProject()).isEqualTo(projectBack);

        complainType.project(null);
        assertThat(complainType.getProject()).isNull();
    }
}
