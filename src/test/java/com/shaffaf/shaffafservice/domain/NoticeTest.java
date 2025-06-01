package com.shaffaf.shaffafservice.domain;

import static com.shaffaf.shaffafservice.domain.NoticeTestSamples.*;
import static com.shaffaf.shaffafservice.domain.ProjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NoticeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Notice.class);
        Notice notice1 = getNoticeSample1();
        Notice notice2 = new Notice();
        assertThat(notice1).isNotEqualTo(notice2);

        notice2.setId(notice1.getId());
        assertThat(notice1).isEqualTo(notice2);

        notice2 = getNoticeSample2();
        assertThat(notice1).isNotEqualTo(notice2);
    }

    @Test
    void projectTest() {
        Notice notice = getNoticeRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        notice.setProject(projectBack);
        assertThat(notice.getProject()).isEqualTo(projectBack);

        notice.project(null);
        assertThat(notice.getProject()).isNull();
    }
}
