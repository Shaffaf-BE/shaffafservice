package com.shaffaf.shaffafservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ProjectTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Project getProjectSample1() {
        return new Project()
            .id(1L)
            .name("name1")
            .description("description1")
            .unionHeadName("unionHeadName1")
            .unionHeadMobileNumber("unionHeadMobileNumber1")
            .numberOfUnits(1)
            .consentProvidedBy("consentProvidedBy1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static Project getProjectSample2() {
        return new Project()
            .id(2L)
            .name("name2")
            .description("description2")
            .unionHeadName("unionHeadName2")
            .unionHeadMobileNumber("unionHeadMobileNumber2")
            .numberOfUnits(2)
            .consentProvidedBy("consentProvidedBy2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static Project getProjectRandomSampleGenerator() {
        return new Project()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .unionHeadName(UUID.randomUUID().toString())
            .unionHeadMobileNumber(UUID.randomUUID().toString())
            .numberOfUnits(intCount.incrementAndGet())
            .consentProvidedBy(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
