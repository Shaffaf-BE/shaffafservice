package com.shaffaf.shaffafservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ResidentTypeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ResidentType getResidentTypeSample1() {
        return new ResidentType().id(1L).type("type1").createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static ResidentType getResidentTypeSample2() {
        return new ResidentType().id(2L).type("type2").createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static ResidentType getResidentTypeRandomSampleGenerator() {
        return new ResidentType()
            .id(longCount.incrementAndGet())
            .type(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
