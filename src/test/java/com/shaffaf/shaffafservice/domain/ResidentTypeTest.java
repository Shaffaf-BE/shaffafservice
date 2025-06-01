package com.shaffaf.shaffafservice.domain;

import static com.shaffaf.shaffafservice.domain.ResidentTestSamples.*;
import static com.shaffaf.shaffafservice.domain.ResidentTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ResidentTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ResidentType.class);
        ResidentType residentType1 = getResidentTypeSample1();
        ResidentType residentType2 = new ResidentType();
        assertThat(residentType1).isNotEqualTo(residentType2);

        residentType2.setId(residentType1.getId());
        assertThat(residentType1).isEqualTo(residentType2);

        residentType2 = getResidentTypeSample2();
        assertThat(residentType1).isNotEqualTo(residentType2);
    }

    @Test
    void residentsTest() {
        ResidentType residentType = getResidentTypeRandomSampleGenerator();
        Resident residentBack = getResidentRandomSampleGenerator();

        residentType.addResidents(residentBack);
        assertThat(residentType.getResidents()).containsOnly(residentBack);
        assertThat(residentBack.getResidentType()).isEqualTo(residentType);

        residentType.removeResidents(residentBack);
        assertThat(residentType.getResidents()).doesNotContain(residentBack);
        assertThat(residentBack.getResidentType()).isNull();

        residentType.residents(new HashSet<>(Set.of(residentBack)));
        assertThat(residentType.getResidents()).containsOnly(residentBack);
        assertThat(residentBack.getResidentType()).isEqualTo(residentType);

        residentType.setResidents(new HashSet<>());
        assertThat(residentType.getResidents()).doesNotContain(residentBack);
        assertThat(residentBack.getResidentType()).isNull();
    }
}
