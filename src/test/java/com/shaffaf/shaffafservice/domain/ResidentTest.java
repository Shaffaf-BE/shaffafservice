package com.shaffaf.shaffafservice.domain;

import static com.shaffaf.shaffafservice.domain.ResidentTestSamples.*;
import static com.shaffaf.shaffafservice.domain.ResidentTypeTestSamples.*;
import static com.shaffaf.shaffafservice.domain.UnitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ResidentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Resident.class);
        Resident resident1 = getResidentSample1();
        Resident resident2 = new Resident();
        assertThat(resident1).isNotEqualTo(resident2);

        resident2.setId(resident1.getId());
        assertThat(resident1).isEqualTo(resident2);

        resident2 = getResidentSample2();
        assertThat(resident1).isNotEqualTo(resident2);
    }

    @Test
    void unitTest() {
        Resident resident = getResidentRandomSampleGenerator();
        Unit unitBack = getUnitRandomSampleGenerator();

        resident.setUnit(unitBack);
        assertThat(resident.getUnit()).isEqualTo(unitBack);

        resident.unit(null);
        assertThat(resident.getUnit()).isNull();
    }

    @Test
    void residentTypeTest() {
        Resident resident = getResidentRandomSampleGenerator();
        ResidentType residentTypeBack = getResidentTypeRandomSampleGenerator();

        resident.setResidentType(residentTypeBack);
        assertThat(resident.getResidentType()).isEqualTo(residentTypeBack);

        resident.residentType(null);
        assertThat(resident.getResidentType()).isNull();
    }
}
