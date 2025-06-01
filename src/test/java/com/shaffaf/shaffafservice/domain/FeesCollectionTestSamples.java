package com.shaffaf.shaffafservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FeesCollectionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static FeesCollection getFeesCollectionSample1() {
        return new FeesCollection()
            .id(1L)
            .title("title1")
            .description("description1")
            .amountCollectedBy("amountCollectedBy1")
            .paidBy("paidBy1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static FeesCollection getFeesCollectionSample2() {
        return new FeesCollection()
            .id(2L)
            .title("title2")
            .description("description2")
            .amountCollectedBy("amountCollectedBy2")
            .paidBy("paidBy2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static FeesCollection getFeesCollectionRandomSampleGenerator() {
        return new FeesCollection()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .amountCollectedBy(UUID.randomUUID().toString())
            .paidBy(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
