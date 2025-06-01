package com.shaffaf.shaffafservice.domain;

import static com.shaffaf.shaffafservice.domain.BlockTestSamples.*;
import static com.shaffaf.shaffafservice.domain.FeesConfigurationTestSamples.*;
import static com.shaffaf.shaffafservice.domain.ProjectTestSamples.*;
import static com.shaffaf.shaffafservice.domain.UnitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shaffaf.shaffafservice.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BlockTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Block.class);
        Block block1 = getBlockSample1();
        Block block2 = new Block();
        assertThat(block1).isNotEqualTo(block2);

        block2.setId(block1.getId());
        assertThat(block1).isEqualTo(block2);

        block2 = getBlockSample2();
        assertThat(block1).isNotEqualTo(block2);
    }

    @Test
    void unitsTest() {
        Block block = getBlockRandomSampleGenerator();
        Unit unitBack = getUnitRandomSampleGenerator();

        block.addUnits(unitBack);
        assertThat(block.getUnits()).containsOnly(unitBack);
        assertThat(unitBack.getBlock()).isEqualTo(block);

        block.removeUnits(unitBack);
        assertThat(block.getUnits()).doesNotContain(unitBack);
        assertThat(unitBack.getBlock()).isNull();

        block.units(new HashSet<>(Set.of(unitBack)));
        assertThat(block.getUnits()).containsOnly(unitBack);
        assertThat(unitBack.getBlock()).isEqualTo(block);

        block.setUnits(new HashSet<>());
        assertThat(block.getUnits()).doesNotContain(unitBack);
        assertThat(unitBack.getBlock()).isNull();
    }

    @Test
    void feesConfigurationTest() {
        Block block = getBlockRandomSampleGenerator();
        FeesConfiguration feesConfigurationBack = getFeesConfigurationRandomSampleGenerator();

        block.addFeesConfiguration(feesConfigurationBack);
        assertThat(block.getFeesConfigurations()).containsOnly(feesConfigurationBack);
        assertThat(feesConfigurationBack.getBlock()).isEqualTo(block);

        block.removeFeesConfiguration(feesConfigurationBack);
        assertThat(block.getFeesConfigurations()).doesNotContain(feesConfigurationBack);
        assertThat(feesConfigurationBack.getBlock()).isNull();

        block.feesConfigurations(new HashSet<>(Set.of(feesConfigurationBack)));
        assertThat(block.getFeesConfigurations()).containsOnly(feesConfigurationBack);
        assertThat(feesConfigurationBack.getBlock()).isEqualTo(block);

        block.setFeesConfigurations(new HashSet<>());
        assertThat(block.getFeesConfigurations()).doesNotContain(feesConfigurationBack);
        assertThat(feesConfigurationBack.getBlock()).isNull();
    }

    @Test
    void projectTest() {
        Block block = getBlockRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        block.setProject(projectBack);
        assertThat(block.getProject()).isEqualTo(projectBack);

        block.project(null);
        assertThat(block.getProject()).isNull();
    }
}
