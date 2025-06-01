package com.shaffaf.shaffafservice.domain;

import static com.shaffaf.shaffafservice.domain.ComplainStatusTestSamples.*;
import static com.shaffaf.shaffafservice.domain.ComplainTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ComplainStatusTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ComplainStatus.class);
        ComplainStatus complainStatus1 = getComplainStatusSample1();
        ComplainStatus complainStatus2 = new ComplainStatus();
        assertThat(complainStatus1).isNotEqualTo(complainStatus2);

        complainStatus2.setId(complainStatus1.getId());
        assertThat(complainStatus1).isEqualTo(complainStatus2);

        complainStatus2 = getComplainStatusSample2();
        assertThat(complainStatus1).isNotEqualTo(complainStatus2);
    }

    @Test
    void complainsTest() {
        ComplainStatus complainStatus = getComplainStatusRandomSampleGenerator();
        Complain complainBack = getComplainRandomSampleGenerator();

        complainStatus.addComplains(complainBack);
        assertThat(complainStatus.getComplains()).containsOnly(complainBack);
        assertThat(complainBack.getComplainStatus()).isEqualTo(complainStatus);

        complainStatus.removeComplains(complainBack);
        assertThat(complainStatus.getComplains()).doesNotContain(complainBack);
        assertThat(complainBack.getComplainStatus()).isNull();

        complainStatus.complains(new HashSet<>(Set.of(complainBack)));
        assertThat(complainStatus.getComplains()).containsOnly(complainBack);
        assertThat(complainBack.getComplainStatus()).isEqualTo(complainStatus);

        complainStatus.setComplains(new HashSet<>());
        assertThat(complainStatus.getComplains()).doesNotContain(complainBack);
        assertThat(complainBack.getComplainStatus()).isNull();
    }
}
