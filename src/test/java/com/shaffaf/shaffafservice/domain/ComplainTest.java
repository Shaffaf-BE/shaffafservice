package com.shaffaf.shaffafservice.domain;

import static com.shaffaf.shaffafservice.domain.ComplainCommentTestSamples.*;
import static com.shaffaf.shaffafservice.domain.ComplainStatusTestSamples.*;
import static com.shaffaf.shaffafservice.domain.ComplainTestSamples.*;
import static com.shaffaf.shaffafservice.domain.ComplainTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ComplainTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Complain.class);
        Complain complain1 = getComplainSample1();
        Complain complain2 = new Complain();
        assertThat(complain1).isNotEqualTo(complain2);

        complain2.setId(complain1.getId());
        assertThat(complain1).isEqualTo(complain2);

        complain2 = getComplainSample2();
        assertThat(complain1).isNotEqualTo(complain2);
    }

    @Test
    void complainCommentsTest() {
        Complain complain = getComplainRandomSampleGenerator();
        ComplainComment complainCommentBack = getComplainCommentRandomSampleGenerator();

        complain.addComplainComments(complainCommentBack);
        assertThat(complain.getComplainComments()).containsOnly(complainCommentBack);
        assertThat(complainCommentBack.getComplain()).isEqualTo(complain);

        complain.removeComplainComments(complainCommentBack);
        assertThat(complain.getComplainComments()).doesNotContain(complainCommentBack);
        assertThat(complainCommentBack.getComplain()).isNull();

        complain.complainComments(new HashSet<>(Set.of(complainCommentBack)));
        assertThat(complain.getComplainComments()).containsOnly(complainCommentBack);
        assertThat(complainCommentBack.getComplain()).isEqualTo(complain);

        complain.setComplainComments(new HashSet<>());
        assertThat(complain.getComplainComments()).doesNotContain(complainCommentBack);
        assertThat(complainCommentBack.getComplain()).isNull();
    }

    @Test
    void complainTypeTest() {
        Complain complain = getComplainRandomSampleGenerator();
        ComplainType complainTypeBack = getComplainTypeRandomSampleGenerator();

        complain.setComplainType(complainTypeBack);
        assertThat(complain.getComplainType()).isEqualTo(complainTypeBack);

        complain.complainType(null);
        assertThat(complain.getComplainType()).isNull();
    }

    @Test
    void complainStatusTest() {
        Complain complain = getComplainRandomSampleGenerator();
        ComplainStatus complainStatusBack = getComplainStatusRandomSampleGenerator();

        complain.setComplainStatus(complainStatusBack);
        assertThat(complain.getComplainStatus()).isEqualTo(complainStatusBack);

        complain.complainStatus(null);
        assertThat(complain.getComplainStatus()).isNull();
    }
}
