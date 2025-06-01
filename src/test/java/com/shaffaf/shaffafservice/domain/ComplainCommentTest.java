package com.shaffaf.shaffafservice.domain;

import static com.shaffaf.shaffafservice.domain.ComplainCommentTestSamples.*;
import static com.shaffaf.shaffafservice.domain.ComplainTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ComplainCommentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ComplainComment.class);
        ComplainComment complainComment1 = getComplainCommentSample1();
        ComplainComment complainComment2 = new ComplainComment();
        assertThat(complainComment1).isNotEqualTo(complainComment2);

        complainComment2.setId(complainComment1.getId());
        assertThat(complainComment1).isEqualTo(complainComment2);

        complainComment2 = getComplainCommentSample2();
        assertThat(complainComment1).isNotEqualTo(complainComment2);
    }

    @Test
    void complainTest() {
        ComplainComment complainComment = getComplainCommentRandomSampleGenerator();
        Complain complainBack = getComplainRandomSampleGenerator();

        complainComment.setComplain(complainBack);
        assertThat(complainComment.getComplain()).isEqualTo(complainBack);

        complainComment.complain(null);
        assertThat(complainComment.getComplain()).isNull();
    }
}
