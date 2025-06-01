package com.shaffaf.shaffafservice.domain;

import static com.shaffaf.shaffafservice.domain.FeesConfigurationTestSamples.*;
import static com.shaffaf.shaffafservice.domain.UnitTestSamples.*;
import static com.shaffaf.shaffafservice.domain.UnitTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UnitTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UnitType.class);
        UnitType unitType1 = getUnitTypeSample1();
        UnitType unitType2 = new UnitType();
        assertThat(unitType1).isNotEqualTo(unitType2);

        unitType2.setId(unitType1.getId());
        assertThat(unitType1).isEqualTo(unitType2);

        unitType2 = getUnitTypeSample2();
        assertThat(unitType1).isNotEqualTo(unitType2);
    }

    @Test
    void unitsTest() {
        UnitType unitType = getUnitTypeRandomSampleGenerator();
        Unit unitBack = getUnitRandomSampleGenerator();

        unitType.addUnits(unitBack);
        assertThat(unitType.getUnits()).containsOnly(unitBack);
        assertThat(unitBack.getUnitType()).isEqualTo(unitType);

        unitType.removeUnits(unitBack);
        assertThat(unitType.getUnits()).doesNotContain(unitBack);
        assertThat(unitBack.getUnitType()).isNull();

        unitType.units(new HashSet<>(Set.of(unitBack)));
        assertThat(unitType.getUnits()).containsOnly(unitBack);
        assertThat(unitBack.getUnitType()).isEqualTo(unitType);

        unitType.setUnits(new HashSet<>());
        assertThat(unitType.getUnits()).doesNotContain(unitBack);
        assertThat(unitBack.getUnitType()).isNull();
    }

    @Test
    void feesConfigurationTest() {
        UnitType unitType = getUnitTypeRandomSampleGenerator();
        FeesConfiguration feesConfigurationBack = getFeesConfigurationRandomSampleGenerator();

        unitType.addFeesConfiguration(feesConfigurationBack);
        assertThat(unitType.getFeesConfigurations()).containsOnly(feesConfigurationBack);
        assertThat(feesConfigurationBack.getUnitType()).isEqualTo(unitType);

        unitType.removeFeesConfiguration(feesConfigurationBack);
        assertThat(unitType.getFeesConfigurations()).doesNotContain(feesConfigurationBack);
        assertThat(feesConfigurationBack.getUnitType()).isNull();

        unitType.feesConfigurations(new HashSet<>(Set.of(feesConfigurationBack)));
        assertThat(unitType.getFeesConfigurations()).containsOnly(feesConfigurationBack);
        assertThat(feesConfigurationBack.getUnitType()).isEqualTo(unitType);

        unitType.setFeesConfigurations(new HashSet<>());
        assertThat(unitType.getFeesConfigurations()).doesNotContain(feesConfigurationBack);
        assertThat(feesConfigurationBack.getUnitType()).isNull();
    }
}
