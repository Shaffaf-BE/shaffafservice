package com.shaffaf.shaffafservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ComplainStatusTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ComplainStatus getComplainStatusSample1() {
        return new ComplainStatus().id(1L).status("status1").createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static ComplainStatus getComplainStatusSample2() {
        return new ComplainStatus().id(2L).status("status2").createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static ComplainStatus getComplainStatusRandomSampleGenerator() {
        return new ComplainStatus()
            .id(longCount.incrementAndGet())
            .status(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
