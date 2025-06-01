package com.shaffaf.shaffafservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProjectDiscountTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ProjectDiscount getProjectDiscountSample1() {
        return new ProjectDiscount().id(1L).title("title1").createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static ProjectDiscount getProjectDiscountSample2() {
        return new ProjectDiscount().id(2L).title("title2").createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static ProjectDiscount getProjectDiscountRandomSampleGenerator() {
        return new ProjectDiscount()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
