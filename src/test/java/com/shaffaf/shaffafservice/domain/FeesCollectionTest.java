package com.shaffaf.shaffafservice.domain;

import static com.shaffaf.shaffafservice.domain.FeesCollectionTestSamples.*;
import static com.shaffaf.shaffafservice.domain.FeesConfigurationTestSamples.*;
import static com.shaffaf.shaffafservice.domain.UnitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class FeesCollectionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FeesCollection.class);
        FeesCollection feesCollection1 = getFeesCollectionSample1();
        FeesCollection feesCollection2 = new FeesCollection();
        assertThat(feesCollection1).isNotEqualTo(feesCollection2);

        feesCollection2.setId(feesCollection1.getId());
        assertThat(feesCollection1).isEqualTo(feesCollection2);

        feesCollection2 = getFeesCollectionSample2();
        assertThat(feesCollection1).isNotEqualTo(feesCollection2);
    }

    @Test
    void unitsTest() {
        FeesCollection feesCollection = getFeesCollectionRandomSampleGenerator();
        Unit unitBack = getUnitRandomSampleGenerator();

        feesCollection.addUnits(unitBack);
        assertThat(feesCollection.getUnits()).containsOnly(unitBack);
        assertThat(unitBack.getFeesCollections()).containsOnly(feesCollection);

        feesCollection.removeUnits(unitBack);
        assertThat(feesCollection.getUnits()).doesNotContain(unitBack);
        assertThat(unitBack.getFeesCollections()).doesNotContain(feesCollection);

        feesCollection.units(new HashSet<>(Set.of(unitBack)));
        assertThat(feesCollection.getUnits()).containsOnly(unitBack);
        assertThat(unitBack.getFeesCollections()).containsOnly(feesCollection);

        feesCollection.setUnits(new HashSet<>());
        assertThat(feesCollection.getUnits()).doesNotContain(unitBack);
        assertThat(unitBack.getFeesCollections()).doesNotContain(feesCollection);
    }

    @Test
    void feesConfigurationTest() {
        FeesCollection feesCollection = getFeesCollectionRandomSampleGenerator();
        FeesConfiguration feesConfigurationBack = getFeesConfigurationRandomSampleGenerator();

        feesCollection.setFeesConfiguration(feesConfigurationBack);
        assertThat(feesCollection.getFeesConfiguration()).isEqualTo(feesConfigurationBack);

        feesCollection.feesConfiguration(null);
        assertThat(feesCollection.getFeesConfiguration()).isNull();
    }
}
