package com.shaffaf.shaffafservice.domain;

import static com.shaffaf.shaffafservice.domain.BlockTestSamples.*;
import static com.shaffaf.shaffafservice.domain.FeesCollectionTestSamples.*;
import static com.shaffaf.shaffafservice.domain.FeesConfigurationTestSamples.*;
import static com.shaffaf.shaffafservice.domain.UnitTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class FeesConfigurationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FeesConfiguration.class);
        FeesConfiguration feesConfiguration1 = getFeesConfigurationSample1();
        FeesConfiguration feesConfiguration2 = new FeesConfiguration();
        assertThat(feesConfiguration1).isNotEqualTo(feesConfiguration2);

        feesConfiguration2.setId(feesConfiguration1.getId());
        assertThat(feesConfiguration1).isEqualTo(feesConfiguration2);

        feesConfiguration2 = getFeesConfigurationSample2();
        assertThat(feesConfiguration1).isNotEqualTo(feesConfiguration2);
    }

    @Test
    void feesCollectionTest() {
        FeesConfiguration feesConfiguration = getFeesConfigurationRandomSampleGenerator();
        FeesCollection feesCollectionBack = getFeesCollectionRandomSampleGenerator();

        feesConfiguration.addFeesCollection(feesCollectionBack);
        assertThat(feesConfiguration.getFeesCollections()).containsOnly(feesCollectionBack);
        assertThat(feesCollectionBack.getFeesConfiguration()).isEqualTo(feesConfiguration);

        feesConfiguration.removeFeesCollection(feesCollectionBack);
        assertThat(feesConfiguration.getFeesCollections()).doesNotContain(feesCollectionBack);
        assertThat(feesCollectionBack.getFeesConfiguration()).isNull();

        feesConfiguration.feesCollections(new HashSet<>(Set.of(feesCollectionBack)));
        assertThat(feesConfiguration.getFeesCollections()).containsOnly(feesCollectionBack);
        assertThat(feesCollectionBack.getFeesConfiguration()).isEqualTo(feesConfiguration);

        feesConfiguration.setFeesCollections(new HashSet<>());
        assertThat(feesConfiguration.getFeesCollections()).doesNotContain(feesCollectionBack);
        assertThat(feesCollectionBack.getFeesConfiguration()).isNull();
    }

    @Test
    void unitTypeTest() {
        FeesConfiguration feesConfiguration = getFeesConfigurationRandomSampleGenerator();
        UnitType unitTypeBack = getUnitTypeRandomSampleGenerator();

        feesConfiguration.setUnitType(unitTypeBack);
        assertThat(feesConfiguration.getUnitType()).isEqualTo(unitTypeBack);

        feesConfiguration.unitType(null);
        assertThat(feesConfiguration.getUnitType()).isNull();
    }

    @Test
    void blockTest() {
        FeesConfiguration feesConfiguration = getFeesConfigurationRandomSampleGenerator();
        Block blockBack = getBlockRandomSampleGenerator();

        feesConfiguration.setBlock(blockBack);
        assertThat(feesConfiguration.getBlock()).isEqualTo(blockBack);

        feesConfiguration.block(null);
        assertThat(feesConfiguration.getBlock()).isNull();
    }
}
