package com.shaffaf.shaffafservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UnitTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Unit getUnitSample1() {
        return new Unit().id(1L).unitNumber("unitNumber1").createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static Unit getUnitSample2() {
        return new Unit().id(2L).unitNumber("unitNumber2").createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static Unit getUnitRandomSampleGenerator() {
        return new Unit()
            .id(longCount.incrementAndGet())
            .unitNumber(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
