package com.shaffaf.shaffafservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BlockTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Block getBlockSample1() {
        return new Block().id(1L).name("name1").createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static Block getBlockSample2() {
        return new Block().id(2L).name("name2").createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static Block getBlockRandomSampleGenerator() {
        return new Block()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
