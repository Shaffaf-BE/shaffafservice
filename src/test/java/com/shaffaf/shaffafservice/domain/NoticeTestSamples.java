package com.shaffaf.shaffafservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class NoticeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Notice getNoticeSample1() {
        return new Notice()
            .id(1L)
            .title("title1")
            .description("description1")
            .addedBy("addedBy1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static Notice getNoticeSample2() {
        return new Notice()
            .id(2L)
            .title("title2")
            .description("description2")
            .addedBy("addedBy2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static Notice getNoticeRandomSampleGenerator() {
        return new Notice()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .addedBy(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
