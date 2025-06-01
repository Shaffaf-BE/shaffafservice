package com.shaffaf.shaffafservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FeesConfigurationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static FeesConfiguration getFeesConfigurationSample1() {
        return new FeesConfiguration()
            .id(1L)
            .title("title1")
            .description("description1")
            .configuredBy("configuredBy1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static FeesConfiguration getFeesConfigurationSample2() {
        return new FeesConfiguration()
            .id(2L)
            .title("title2")
            .description("description2")
            .configuredBy("configuredBy2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static FeesConfiguration getFeesConfigurationRandomSampleGenerator() {
        return new FeesConfiguration()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .configuredBy(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
