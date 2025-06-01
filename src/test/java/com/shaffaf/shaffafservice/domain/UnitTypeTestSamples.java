package com.shaffaf.shaffafservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UnitTypeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UnitType getUnitTypeSample1() {
        return new UnitType().id(1L).name("name1").createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static UnitType getUnitTypeSample2() {
        return new UnitType().id(2L).name("name2").createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static UnitType getUnitTypeRandomSampleGenerator() {
        return new UnitType()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
