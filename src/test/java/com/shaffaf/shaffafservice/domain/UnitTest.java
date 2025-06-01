package com.shaffaf.shaffafservice.domain;

import static com.shaffaf.shaffafservice.domain.BlockTestSamples.*;
import static com.shaffaf.shaffafservice.domain.FeesCollectionTestSamples.*;
import static com.shaffaf.shaffafservice.domain.ResidentTestSamples.*;
import static com.shaffaf.shaffafservice.domain.UnitTestSamples.*;
import static com.shaffaf.shaffafservice.domain.UnitTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UnitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Unit.class);
        Unit unit1 = getUnitSample1();
        Unit unit2 = new Unit();
        assertThat(unit1).isNotEqualTo(unit2);

        unit2.setId(unit1.getId());
        assertThat(unit1).isEqualTo(unit2);

        unit2 = getUnitSample2();
        assertThat(unit1).isNotEqualTo(unit2);
    }

    @Test
    void residentsTest() {
        Unit unit = getUnitRandomSampleGenerator();
        Resident residentBack = getResidentRandomSampleGenerator();

        unit.addResidents(residentBack);
        assertThat(unit.getResidents()).containsOnly(residentBack);
        assertThat(residentBack.getUnit()).isEqualTo(unit);

        unit.removeResidents(residentBack);
        assertThat(unit.getResidents()).doesNotContain(residentBack);
        assertThat(residentBack.getUnit()).isNull();

        unit.residents(new HashSet<>(Set.of(residentBack)));
        assertThat(unit.getResidents()).containsOnly(residentBack);
        assertThat(residentBack.getUnit()).isEqualTo(unit);

        unit.setResidents(new HashSet<>());
        assertThat(unit.getResidents()).doesNotContain(residentBack);
        assertThat(residentBack.getUnit()).isNull();
    }

    @Test
    void feesCollectionsTest() {
        Unit unit = getUnitRandomSampleGenerator();
        FeesCollection feesCollectionBack = getFeesCollectionRandomSampleGenerator();

        unit.addFeesCollections(feesCollectionBack);
        assertThat(unit.getFeesCollections()).containsOnly(feesCollectionBack);

        unit.removeFeesCollections(feesCollectionBack);
        assertThat(unit.getFeesCollections()).doesNotContain(feesCollectionBack);

        unit.feesCollections(new HashSet<>(Set.of(feesCollectionBack)));
        assertThat(unit.getFeesCollections()).containsOnly(feesCollectionBack);

        unit.setFeesCollections(new HashSet<>());
        assertThat(unit.getFeesCollections()).doesNotContain(feesCollectionBack);
    }

    @Test
    void unitTypeTest() {
        Unit unit = getUnitRandomSampleGenerator();
        UnitType unitTypeBack = getUnitTypeRandomSampleGenerator();

        unit.setUnitType(unitTypeBack);
        assertThat(unit.getUnitType()).isEqualTo(unitTypeBack);

        unit.unitType(null);
        assertThat(unit.getUnitType()).isNull();
    }

    @Test
    void blockTest() {
        Unit unit = getUnitRandomSampleGenerator();
        Block blockBack = getBlockRandomSampleGenerator();

        unit.setBlock(blockBack);
        assertThat(unit.getBlock()).isEqualTo(blockBack);

        unit.block(null);
        assertThat(unit.getBlock()).isNull();
    }
}
