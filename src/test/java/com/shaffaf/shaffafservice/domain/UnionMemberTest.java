package com.shaffaf.shaffafservice.domain;

import static com.shaffaf.shaffafservice.domain.ProjectTestSamples.*;
import static com.shaffaf.shaffafservice.domain.UnionMemberTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UnionMemberTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UnionMember.class);
        UnionMember unionMember1 = getUnionMemberSample1();
        UnionMember unionMember2 = new UnionMember();
        assertThat(unionMember1).isNotEqualTo(unionMember2);

        unionMember2.setId(unionMember1.getId());
        assertThat(unionMember1).isEqualTo(unionMember2);

        unionMember2 = getUnionMemberSample2();
        assertThat(unionMember1).isNotEqualTo(unionMember2);
    }

    @Test
    void projectTest() {
        UnionMember unionMember = getUnionMemberRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        unionMember.setProject(projectBack);
        assertThat(unionMember.getProject()).isEqualTo(projectBack);

        unionMember.project(null);
        assertThat(unionMember.getProject()).isNull();
    }
}
